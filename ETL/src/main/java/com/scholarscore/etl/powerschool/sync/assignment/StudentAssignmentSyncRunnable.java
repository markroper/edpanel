package com.scholarscore.etl.powerschool.sync.assignment;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.SyncResult;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsAssignmentScoreWrapper;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsScore;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsSectionScoreId;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponseInner;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.user.Student;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * For a given SectionAssignment, a thread can be spun up to handle resolving the student assignment scores
 * for that assignment from powerschool and creating the corresponding entries in EdPanel.
 *
 * Created by markroper on 10/25/15.
 */
public class StudentAssignmentSyncRunnable implements Runnable, ISync<StudentAssignment> {
    private IPowerSchoolClient powerSchool;
    private IAPIClient edPanel;
    private School school;
    private Section createdSection;
    private Assignment assignment;
    private Map<Long, MutablePair<Student, PsSectionScoreId>> ssidToStudent;
    private SyncResult results;

    public StudentAssignmentSyncRunnable(IPowerSchoolClient powerSchool,
                                         IAPIClient edPanel,
                                         School school,
                                         Section createdSection,
                                         Assignment assignment,
                                         Map<Long, MutablePair<Student, PsSectionScoreId>> ssidToStudent,
                                         SyncResult results) {
        this.powerSchool = powerSchool;
        this.edPanel = edPanel;
        this.school = school;
        this.createdSection = createdSection;
        this.ssidToStudent = ssidToStudent;
        this.assignment = assignment;
        this.results = results;
    }

    @Override
    public void run() {
        this.syncCreateUpdateDelete(results);
    }

    @Override
    public ConcurrentHashMap<Long, StudentAssignment> syncCreateUpdateDelete(SyncResult results) {
        ConcurrentHashMap<Long, StudentAssignment> source = null;
        try {
            source = resolveAllFromSourceSystem();
        } catch (HttpClientException e) {
            results.studentAssignmentSourceGetFailed(
                    Long.valueOf(createdSection.getSourceSystemId()),
                    Long.valueOf(this.assignment.getSourceSystemId()),
                    Long.valueOf(this.assignment.getSourceSystemId()),
                    this.assignment.getId());
            return new ConcurrentHashMap<>();
        }
        ConcurrentHashMap<Long, StudentAssignment> ed = null;
        try {
            ed = resolveFromEdPanel();
        } catch (HttpClientException e) {
            results.studentAssignmentEdPanelGetFailed(
                    Long.valueOf(createdSection.getSourceSystemId()),
                    Long.valueOf(this.assignment.getSourceSystemId()),
                    Long.valueOf(this.assignment.getSourceSystemId()),
                    this.assignment.getId());
            return new ConcurrentHashMap<>();
        }

        Iterator<Map.Entry<Long, StudentAssignment>> sourceIterator = source.entrySet().iterator();
        List<StudentAssignment> studentAssignmentsToCreate = new ArrayList<>();
        //Find & perform the inserts and updates, if any
        while(sourceIterator.hasNext()) {
            Map.Entry<Long, StudentAssignment> entry = sourceIterator.next();
            StudentAssignment sourceStudentAssignment = entry.getValue();
            StudentAssignment edPanelStudentAssignment = ed.get(entry.getKey());
            if(null == edPanelStudentAssignment){
                studentAssignmentsToCreate.add(sourceStudentAssignment);
            } else {
                sourceStudentAssignment.setId(edPanelStudentAssignment.getId());
                sourceStudentAssignment.setStudent(edPanelStudentAssignment.getStudent());
                if(sourceStudentAssignment.getStudent().getId().equals(edPanelStudentAssignment.getStudent().getId())) {
                    sourceStudentAssignment.setStudent(edPanelStudentAssignment.getStudent());
                }
                if(sourceStudentAssignment.getAssignment().getId().equals(edPanelStudentAssignment.getAssignment().getId())) {
                    sourceStudentAssignment.setAssignment(edPanelStudentAssignment.getAssignment());
                }
                if(!edPanelStudentAssignment.equals(sourceStudentAssignment)) {
                    try {
                        edPanel.replaceStudentAssignment(
                                school.getId(),
                                createdSection.getTerm().getSchoolYear().getId(),
                                createdSection.getTerm().getId(),
                                createdSection.getId(),
                                assignment.getId(),
                                sourceStudentAssignment);
                    } catch (IOException e) {
                        results.studentAssignmentUpdateFailed(
                                Long.valueOf(createdSection.getSourceSystemId()),
                                Long.valueOf(this.assignment.getSourceSystemId()),
                                entry.getKey(),
                                sourceStudentAssignment.getId());
                        continue;
                    }
                    results.studentAssignmentUpdated(
                            Long.valueOf(createdSection.getSourceSystemId()),
                            Long.valueOf(this.assignment.getSourceSystemId()),
                            entry.getKey(),
                            sourceStudentAssignment.getId());
                }
            }
        }
        //Perform the bulk creates!
        try {
            List<Long> ids = edPanel.createStudentAssignments(
                    school.getId(),
                    createdSection.getTerm().getSchoolYear().getId(),
                    createdSection.getTerm().getId(),
                    createdSection.getId(),
                    assignment.getId(),
                    studentAssignmentsToCreate);

            int i = 0;
            for(StudentAssignment s: studentAssignmentsToCreate) {
                results.studentAssignmentCreated(
                        Long.valueOf(createdSection.getSourceSystemId()),
                        Long.valueOf(this.assignment.getSourceSystemId()),
                        Long.valueOf(s.getStudent().getSourceSystemId()),
                        ids.get(i));
                i++;
            }
        } catch (HttpClientException e) {
            for(StudentAssignment s: studentAssignmentsToCreate) {
                // NOTE - we couldn't create the student assignments for any of the assignment,
                // so use the parent ID in this case.
                results.studentAssignmentCreateFailed(
                        Long.valueOf(createdSection.getSourceSystemId()),
                        Long.valueOf(this.assignment.getSourceSystemId()),
                        Long.valueOf(assignment.getSourceSystemId()));
            }
        }

        //Delete anything IN EdPanel that is NOT in source system
        Iterator<Map.Entry<Long, StudentAssignment>> edpanelIterator = ed.entrySet().iterator();
        while(edpanelIterator.hasNext()) {
            Map.Entry<Long, StudentAssignment> entry = edpanelIterator.next();
            if(!source.containsKey(entry.getKey())) {
                try {
                    edPanel.deleteStudentAssignment(
                            school.getId(),
                            createdSection.getTerm().getSchoolYear().getId(),
                            createdSection.getTerm().getId(),
                            createdSection.getId(),
                            assignment.getId(),
                            entry.getValue());
                } catch (HttpClientException e) {
                    results.studentAssignmentDeleteFailed(
                            Long.valueOf(createdSection.getSourceSystemId()),
                            Long.valueOf(this.assignment.getSourceSystemId()),
                            entry.getKey(),
                            entry.getValue().getId());
                    continue;
                }
                results.studentAssignmentDeleted(
                        Long.valueOf(createdSection.getSourceSystemId()),
                        Long.valueOf(this.assignment.getSourceSystemId()),
                        entry.getKey(),
                        entry.getValue().getId());
            }
        }
        return source;
    }

    protected ConcurrentHashMap<Long, StudentAssignment> resolveAllFromSourceSystem() throws HttpClientException {
        //Retrieve students' scores
        PsResponse<PsAssignmentScoreWrapper> assScores =
                powerSchool.getStudentScoresByAssignmentId(Long.valueOf(assignment.getSourceSystemId()));
        ConcurrentHashMap<Long, StudentAssignment> studentAssignmentsToCreate = new ConcurrentHashMap<>();
        if (null != assScores && null != assScores.record) {
            for (PsResponseInner<PsAssignmentScoreWrapper> sc : assScores.record) {
                PsScore score = sc.tables.sectionscoresassignments;
                StudentAssignment studAss = new StudentAssignment();
                studAss.setAssignment(assignment);
                //Resolve the student, or move on
                MutablePair<Student, PsSectionScoreId> sectionScoreIdAndStudent =
                        ssidToStudent.get(Long.valueOf(score.getFdcid()));
                if (null != sectionScoreIdAndStudent) {
                    studAss.setStudent(sectionScoreIdAndStudent.getLeft());
                } else {
                    continue;
                }
                //Resolve the points
                Double awardedPoints = null;
                try {
                    awardedPoints = Double.valueOf(score.getScore());
                } catch (NumberFormatException e) {
                    //NO OP
                }
                if (null == awardedPoints) {
                    studAss.setCompleted(false);
                } else {
                    studAss.setAwardedPoints(awardedPoints);
                    studAss.setCompleted(true);
                }
                studentAssignmentsToCreate.put(
                        Long.valueOf(studAss.getStudent().getSourceSystemId()),
                        studAss);
            }
        }
        return studentAssignmentsToCreate;
    }

    protected ConcurrentHashMap<Long, StudentAssignment> resolveFromEdPanel() throws HttpClientException {
        StudentAssignment[] studentAssignments = edPanel.getStudentAssignments(
                school.getId(),
                createdSection.getTerm().getSchoolYear().getId(),
                createdSection.getTerm().getId(),
                createdSection.getId(),
                assignment.getId());
        ConcurrentHashMap<Long, StudentAssignment> saMap = new ConcurrentHashMap<>();
        for(StudentAssignment a : studentAssignments) {
            Long id = null;
            String ssid = a.getStudent().getSourceSystemId();
            if(null != ssid) {
                id = Long.valueOf(ssid);
                saMap.put(id, a);
            }
        }
        return saMap;
    }
}
