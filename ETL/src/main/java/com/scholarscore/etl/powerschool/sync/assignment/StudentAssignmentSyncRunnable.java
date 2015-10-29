package com.scholarscore.etl.powerschool.sync.assignment;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsAssignmentScores;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsScore;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsSectionScoreId;
import com.scholarscore.etl.powerschool.api.response.AssignmentScoresResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.sync.ISync;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.user.Student;
import org.apache.commons.lang3.tuple.MutablePair;

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

    public StudentAssignmentSyncRunnable(IPowerSchoolClient powerSchool,
                                         IAPIClient edPanel,
                                         School school,
                                         Section createdSection,
                                         Assignment assignment,
                                         Map<Long, MutablePair<Student, PsSectionScoreId>> ssidToStudent) {
        this.powerSchool = powerSchool;
        this.edPanel = edPanel;
        this.school = school;
        this.createdSection = createdSection;
        this.ssidToStudent = ssidToStudent;
        this.assignment = assignment;
    }

    @Override
    public void run() {
        this.syncCreateUpdateDelete();
    }

    @Override
    public ConcurrentHashMap<Long, StudentAssignment> syncCreateUpdateDelete() {
        ConcurrentHashMap<Long, StudentAssignment> source = resolveAllFromSourceSystem();
        ConcurrentHashMap<Long, StudentAssignment> ed = resolveFromEdPanel();

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
                     edPanel.replaceStudentAssignment(
                            school.getId(),
                            createdSection.getTerm().getSchoolYear().getId(),
                            createdSection.getTerm().getId(),
                            createdSection.getId(),
                            assignment.getId(),
                            sourceStudentAssignment);
                }
            }
        }
        //Perform the bulk creates!
        edPanel.createStudentAssignments(
                school.getId(),
                createdSection.getTerm().getSchoolYear().getId(),
                createdSection.getTerm().getId(),
                createdSection.getId(),
                assignment.getId(),
                studentAssignmentsToCreate);

        //Delete anything IN EdPanel that is NOT in source system
        Iterator<Map.Entry<Long, StudentAssignment>> edpanelIterator = ed.entrySet().iterator();
        while(edpanelIterator.hasNext()) {
            Map.Entry<Long, StudentAssignment> entry = edpanelIterator.next();
            if(!source.containsKey(entry.getKey())) {
                edPanel.deleteStudentAssignment(
                        school.getId(),
                        createdSection.getTerm().getSchoolYear().getId(),
                        createdSection.getTerm().getId(),
                        createdSection.getId(),
                        assignment.getId(),
                        entry.getValue());
            }
        }
        return source;
    }

    protected ConcurrentHashMap<Long, StudentAssignment> resolveAllFromSourceSystem() {
        //Retrieve students' scores
        AssignmentScoresResponse assScores =
                powerSchool.getStudentScoresByAssignmentId(Long.valueOf(assignment.getSourceSystemId()));
        ConcurrentHashMap<Long, StudentAssignment> studentAssignmentsToCreate = new ConcurrentHashMap<>();
        if (null != assScores && null != assScores.record) {
            for (PsAssignmentScores sc : assScores.record) {
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

    protected ConcurrentHashMap<Long, StudentAssignment> resolveFromEdPanel() {
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