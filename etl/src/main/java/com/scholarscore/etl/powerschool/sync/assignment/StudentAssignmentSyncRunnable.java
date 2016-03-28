package com.scholarscore.etl.powerschool.sync.assignment;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.PowerSchoolSyncResult;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final static Logger LOGGER = LoggerFactory.getLogger(StudentAssignmentSyncRunnable.class);
    private IPowerSchoolClient powerSchool;
    private IAPIClient edPanel;
    private School school;
    private Section createdSection;
    private Assignment assignment;
    private Map<Long, MutablePair<Student, PsSectionScoreId>> ssidToStudent;
    private Map<Long, Long> assignmentTableIdToAssignmentSsid;
    private PowerSchoolSyncResult results;

    public StudentAssignmentSyncRunnable(IPowerSchoolClient powerSchool,
                                         IAPIClient edPanel,
                                         School school,
                                         Section createdSection,
                                         Assignment assignment,
                                         Map<Long, MutablePair<Student, PsSectionScoreId>> ssidToStudent,
                                         Map<Long, Long> assignmentTableIdToAssignmentSsid,
                                         PowerSchoolSyncResult results) {
        this.powerSchool = powerSchool;
        this.edPanel = edPanel;
        this.school = school;
        this.createdSection = createdSection;
        this.ssidToStudent = ssidToStudent;
        this.assignmentTableIdToAssignmentSsid = assignmentTableIdToAssignmentSsid;
        this.assignment = assignment;
        this.results = results;
    }

    @Override
    public void run() {
        this.syncCreateUpdateDelete(results);
    }

    @Override
    public ConcurrentHashMap<Long, StudentAssignment> syncCreateUpdateDelete(PowerSchoolSyncResult results) {
        ConcurrentHashMap<Long, StudentAssignment> source = null;
        try {
            source = resolveAllFromSourceSystem(results);
        } catch (HttpClientException e) {
            LOGGER.warn("Unable to resolve student assignments for " +
                    "section with name: " + createdSection.getName() +
                    ", ID: " + createdSection.getId() +
                    ", SSID: " + createdSection.getSourceSystemId() +
                    ", & School ID: " + school.getId());
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
            LOGGER.debug("Failed Student Assignment get from EdPanel for createdSection " + 
            (createdSection!= null ? createdSection.getSourceSystemId() : "null") +
                    " and assignment " +
            (assignment != null ? assignment.getSourceSystemId() : "null"));             
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
                } else {
                    LOGGER.warn("edPanelStudentAssignment.getStudent().getId() " +
                            "is not equal to SourceStudentAssignment.getStudent().getId()!");
                }
                Long sourceStudentAssignmentId = sourceStudentAssignment.getAssignment().getId();
                Long edPanelStudentAssignmentId = edPanelStudentAssignment.getAssignment().getId();
                if(sourceStudentAssignmentId.equals(edPanelStudentAssignmentId)) {
                    sourceStudentAssignment.setAssignment(edPanelStudentAssignment.getAssignment());
                } else {
                    LOGGER.warn("edPanelStudentAssignment.getAssignment().getId() (returned:" + edPanelStudentAssignmentId + ") " + 
                            "is not equal to SourceStudentAssignment.getAssignment().getId() (" + sourceStudentAssignmentId + ") !");
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
                }
                results.studentAssignmentUpdated(
                        Long.valueOf(createdSection.getSourceSystemId()),
                        Long.valueOf(this.assignment.getSourceSystemId()),
                        entry.getKey(),
                        sourceStudentAssignment.getId());
            }
        }
        if(!studentAssignmentsToCreate.isEmpty()) {
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
                for (StudentAssignment s : studentAssignmentsToCreate) {
                    results.studentAssignmentCreated(
                            Long.valueOf(createdSection.getSourceSystemId()),
                            Long.valueOf(this.assignment.getSourceSystemId()),
                            Long.valueOf(s.getStudent().getSourceSystemId()),
                            ids.get(i));
                    i++;
                }
            } catch (HttpClientException e) {
                for (StudentAssignment s : studentAssignmentsToCreate) {
                    // NOTE - we couldn't create the student assignments for any of the assignment,
                    // so use the parent ID in this case.
                    results.studentAssignmentCreateFailed(
                            Long.valueOf(createdSection.getSourceSystemId()),
                            Long.valueOf(this.assignment.getSourceSystemId()),
                            Long.valueOf(assignment.getSourceSystemId()));
                }
            }
        } else {
            // not a problem if the sync is incremental (which the sync engine doesn't currently track) -- sometimes there's
            // just no new assignments created.
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

    protected ConcurrentHashMap<Long, StudentAssignment> resolveAllFromSourceSystem(PowerSchoolSyncResult results) throws HttpClientException {
        // We have assignment.getSourceSystemId(), which is a DCID.
        // We need to call powerSchool.getStudentScoresByAssignmentId(Long ~) which takes a *tableId* not a DCID
        // thus we must convert it first.
        Long assignmentSsid = Long.valueOf(assignment.getSourceSystemId());
        Long assignmentTableId = assignmentTableIdToAssignmentSsid.get(assignmentSsid);
        if (assignmentTableId == null) {
            LOGGER.warn("Big problem: cannot get Assignment TableID for Assignment with SSID " + assignmentSsid);
        } else {
            LOGGER.trace("For assignment SSID " + assignmentSsid + ", successfully found tableId " + assignmentTableId);
        }
        //Retrieve students' scores
        PsResponse<PsAssignmentScoreWrapper> assScores =
                powerSchool.getStudentScoresByAssignmentId(assignmentTableId);
        ConcurrentHashMap<Long, StudentAssignment> studentAssignmentsToCreate = new ConcurrentHashMap<>();
        if (null != assScores && null != assScores.record) {
            for (PsResponseInner<PsAssignmentScoreWrapper> sc : assScores.record) {
                PsScore score = sc.tables.sectionscoresassignments;
                StudentAssignment studAss = new StudentAssignment();
                if(null != score.getExempt() && score.getExempt().equals("1")) {
                    studAss.setExempt(true);
                } else {
                    studAss.setExempt(false);
                }
                studAss.setComment(score.getComment_value());
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
                    // TODO Jordan: record this in sync results, make trace level logging
                    LOGGER.debug("Unable to parse awarded points, will be set to null. " + score.getScore());
                }
                studAss.setAwardedPoints(awardedPoints);
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
