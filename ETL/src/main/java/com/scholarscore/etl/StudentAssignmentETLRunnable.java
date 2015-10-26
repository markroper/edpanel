package com.scholarscore.etl;

import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.powerschool.api.model.PsSection;
import com.scholarscore.etl.powerschool.api.model.assignment.PGAssignment;
import com.scholarscore.etl.powerschool.api.model.assignment.PsAssignment;
import com.scholarscore.etl.powerschool.api.model.assignment.PsAssignmentFactory;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsAssignmentScores;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsScore;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsSectionScoreId;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PsAssignmentType;
import com.scholarscore.etl.powerschool.api.response.AssignmentScoresResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.user.Student;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * For a given SectionAssignment, a thread can be spun up to handle resolving the student assignment scores
 * for that assignment from powerschool and creating the corresponding entries in EdPanel.
 *
 * Created by markroper on 10/25/15.
 */
public class StudentAssignmentETLRunnable implements Runnable {
    private IPowerSchoolClient powerSchool;
    private IAPIClient edPanel;
    private School school;
    private Term sectionTerm;
    private Section createdSection;
    private PsSection powerSection;
    private PGAssignment powerAssignment;
    private Map<Long, PsAssignmentType> typeIdToType;
    private Map<Long, MutablePair<Student, PsSectionScoreId>> ssidToStudent;

    public StudentAssignmentETLRunnable(IPowerSchoolClient powerSchool,
                                        IAPIClient edPanel,
                                        School school,
                                        Term sectionTerm,
                                        Section createdSection,
                                        PsSection powerSection,
                                        PGAssignment powerAssignment,
                                        Map<Long, PsAssignmentType> typeIdToType,
                                        Map<Long, MutablePair<Student, PsSectionScoreId>> ssidToStudent) {
        this.powerSchool = powerSchool;
        this.edPanel = edPanel;
        this.school = school;
        this.sectionTerm = sectionTerm;
        this.createdSection = createdSection;
        this.powerSection = powerSection;
        this.powerAssignment = powerAssignment;
        this.typeIdToType = typeIdToType;
        this.ssidToStudent = ssidToStudent;
    }

    @Override
    public void run() {
        PsAssignment pa = powerAssignment.tables.pgassignments;
        PsAssignmentType psType = typeIdToType.get(Long.valueOf(pa.getPgcategoriesid());
        Assignment edpanelAssignment = PsAssignmentFactory.fabricate(
                pa,
                psType);
        edpanelAssignment.setWeight(pa.getWeight());
        edpanelAssignment.setSection(createdSection);
        edpanelAssignment.setUserDefinedType(psType.getName());
        edpanelAssignment.setIncludeInFinalGrades(pa.getIncludeinfinalgrades());
        edpanelAssignment.setSectionFK(createdSection.getId());
        Assignment createdAssignment = edPanel.createSectionAssignment(
                school.getId(),
                sectionTerm.getSchoolYear().getId(),
                sectionTerm.getId(),
                createdSection.getId(),
                edpanelAssignment);
        //Retrieve students' scores
        AssignmentScoresResponse assScores =
                powerSchool.getStudentScoresByAssignmentId(Long.valueOf(pa.getDcid()));
        if (null != assScores && null != assScores.record) {

            List<StudentAssignment> studentAssignmentsToCreate = Collections.synchronizedList(new ArrayList<>());

            for (PsAssignmentScores sc : assScores.record) {
                PsScore score = sc.tables.sectionscoresassignments;
                StudentAssignment studAss = new StudentAssignment();
                studAss.setAssignment(createdAssignment);
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
                studentAssignmentsToCreate.add(studAss);
            }
            //We've now generated an EdPanel StudentAssignment for each PS student assignment
            //Call the bulk create for student assignments for this assignment.
            if (!studentAssignmentsToCreate.isEmpty()) {
                edPanel.createStudentAssignments(
                        school.getId(),
                        sectionTerm.getSchoolYear().getId(),
                        sectionTerm.getId(),
                        createdSection.getId(),
                        createdAssignment.getId(),
                        studentAssignmentsToCreate);
            }
        }
    }
}
