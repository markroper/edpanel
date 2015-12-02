package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.StudentSectionGradePersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.Section;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.Term;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.gradeformula.GradeFormula;
import com.scholarscore.models.ui.ScoreAsOfWeek;
import com.scholarscore.models.ui.SectionGradeWithProgression;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by cwallace on 9/16/2015.
 */
public class StudentSectionGradeManagerImpl implements StudentSectionGradeManager {

    StudentSectionGradePersistence studentSectionGradePersistence;

    OrchestrationManager pm;

    private static final String STUDENT_SECTION_GRADE = "student section grade";


    public void setStudentSectionGradePersistence(StudentSectionGradePersistence studentSectionGradePersistence) {
        this.studentSectionGradePersistence = studentSectionGradePersistence;
    }

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }

    @Override
    public ServiceResponse<Collection<StudentSectionGrade>> getAllStudentSectionGrades(
            long schoolId, long yearId, long termId, long sectionId) {
        StatusCode code = pm.getSectionManager().sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(studentSectionGradePersistence.selectAll(sectionId));
    }

    @Override
    public ServiceResponse<Collection<StudentSectionGrade>> getAllStudentSectionGradesByTerm(
            long schoolId, long yearId, long termId) {
        return new ServiceResponse<>(studentSectionGradePersistence.selectAllByTerm(termId, schoolId));
    }


    @Override
    public ServiceResponse<Collection<StudentSectionGrade>> getSectionGradesForStudent(long studentId) {
        StatusCode code = pm.getStudentManager().studentExists(studentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        Collection<StudentSectionGrade> grades = studentSectionGradePersistence.selectAllByStudent(studentId);
        return new ServiceResponse<>(grades);
    }

    @Override
    //Comment so I can reference this in the diff
    public StatusCode studentSectionGradeExists(long schoolId, long yearId,
                                                long termId, long sectionId, long studentId) {
        StatusCode code = pm.getSectionManager().sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.isOK()) {
            return code;
        }
        code = pm.getStudentManager().studentExists(studentId);
        if(!code.isOK()) {
            return code;
        }
        StudentSectionGrade ssg = studentSectionGradePersistence.select(sectionId, studentId);
        if(null == ssg) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND,
                    new Object[]{STUDENT_SECTION_GRADE,
                            "section id: " + sectionId + ", student id: " + studentId});
        }
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    @Override
    public ServiceResponse<StudentSectionGrade> getStudentSectionGrade(
            long schoolId, long yearId, long termId, long sectionId,
            long studentId) {
        StatusCode code = studentSectionGradeExists(
                schoolId, yearId, termId, sectionId, studentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        StudentSectionGrade grade = studentSectionGradePersistence.select(sectionId, studentId);
        Boolean complete = grade.getComplete();
        //Only calculate the grade if there is not a grade on it already
        if((null == complete || complete.equals(Boolean.FALSE)) && null == grade.getGrade()) {
            Section sect = grade.getSection();
            ServiceResponse<Collection<StudentAssignment>> assignmentResp =
                    pm.getStudentAssignmentManager().getOneSectionOneStudentsAssignments(studentId, schoolId, yearId, termId, sectionId);
            Term term = sect.getTerm();
            if(null == assignmentResp.getCode() || assignmentResp.getCode().isOK()) {
                //TODO: this applies a single formula to all assignments, instead we need to break it up by term
                GradeFormula formula = sect.getGradeFormula();
                if(null != term) {
                    if(null == formula) {
                        formula = new GradeFormula();
                        formula.setStartDate(term.getStartDate());
                        formula.setEndDate(term.getEndDate());
                    } else {
                        formula = formula.resolveFormulaMatchingDates(
                                term.getStartDate(),
                                term.getEndDate());
                        if(null == formula) {
                            formula = sect.getGradeFormula();
                        }
                    }
                }
                Collection<StudentAssignment> assignments = assignmentResp.getValue();
                if(null != formula && null != assignments) {
                    HashSet<StudentAssignment> assignmentSet = new HashSet<StudentAssignment>(assignments);
                    Double calculatedGrade = formula.calculateGrade(assignmentSet);
                    grade.setGrade(calculatedGrade);
                }
            }
        }
        return new ServiceResponse<>(grade);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ServiceResponse<SectionGradeWithProgression> getStudentSectionGradeByWeek(long schoolId, long yearId, long termId, long sectionId, long studentId) {
        StatusCode code = studentSectionGradeExists(
                schoolId, yearId, termId, sectionId, studentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        ServiceResponse<StudentSectionGrade> ssgResp = getStudentSectionGrade(schoolId, yearId, termId, sectionId, studentId);

        SectionGradeWithProgression gradeWithProgression = new SectionGradeWithProgression();
        gradeWithProgression.setCurrentOverallGrade(ssgResp.getValue().getGrade());
        gradeWithProgression.setTermGrades(ssgResp.getValue().getTermGrades());
        Section section = ssgResp.getValue().getSection();

        List<ScoreAsOfWeek> grades = new ArrayList<>();
        ServiceResponse<Collection<StudentAssignment>> assignmentResp =
                pm.getStudentAssignmentManager().getOneSectionOneStudentsAssignments(studentId, schoolId, yearId, termId, sectionId);
        Term term = section.getTerm();
        if(null == assignmentResp.getCode() || assignmentResp.getCode().isOK()) {
            GradeFormula formula = section.getGradeFormula();
            if(null != term) {
                if(null == formula) {
                    formula = new GradeFormula();
                    formula.setStartDate(term.getStartDate());
                    formula.setEndDate(term.getEndDate());
                } else {
                    formula = formula.resolveFormulaMatchingDate(LocalDate.now());
                    if(null == formula) {
                        formula = section.getGradeFormula();
                    }
                    formula.setStartDate(section.getGradeFormula().getStartDate());
                    formula.setEndDate(section.getGradeFormula().getEndDate());
                }
            }
            ArrayList<StudentAssignment> assignments = (ArrayList<StudentAssignment>)assignmentResp.getValue();
            gradeWithProgression.setCurrentCategoryGrades(formula.calculateCategoryGrades(new HashSet<>(assignments)));
            //Sort by due date
            LocalDate currentLastDayOfWeek = null;
            int i = 1;
            assignments.sort((object1, object2) -> object1.getAssignment().getDueDate().compareTo(object2.getAssignment().getDueDate()));
            for(StudentAssignment a: assignments) {
                LocalDate dueDate = a.getAssignment().getDueDate();
                int daysToAdd = DayOfWeek.SATURDAY.getValue() - dueDate.getDayOfWeek().getValue();
                LocalDate endOfWeek = dueDate.plusDays(daysToAdd);
                if(null == currentLastDayOfWeek) {
                    currentLastDayOfWeek = endOfWeek;
                }
                if(!currentLastDayOfWeek.equals(endOfWeek)) {
                    Set<StudentAssignment> subassignments = new HashSet<>(assignments.subList(0, i));
                    ScoreAsOfWeek g = new ScoreAsOfWeek();
                    g.setWeekEnding(currentLastDayOfWeek);
                    g.setScore(formula.calculateGrade(subassignments));
                    grades.add(g);
                    currentLastDayOfWeek = endOfWeek;
                }
                i++;
            }
        }
        gradeWithProgression.setWeeklyGradeProgression(grades);
        return new ServiceResponse<>(gradeWithProgression);
    }

    @Override
    public ServiceResponse<Long> createStudentSectionGrade(long schoolId,
                                                           long yearId, long termId, long sectionId, long studentId,
                                                           StudentSectionGrade grade) {
        StatusCode code = pm.getSectionManager().sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        code = pm.getStudentManager().studentExists(studentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(
                studentSectionGradePersistence.insert(sectionId, studentId, grade));
    }

    @Override
    public ServiceResponse<Void> createStudentSectionGrades(long schoolId,
                                                            long yearId,
                                                            long termId,
                                                            long sectionId,
                                                            List<StudentSectionGrade> grades) {
        StatusCode code = pm.getSectionManager().sectionExists(schoolId, yearId, termId, sectionId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        studentSectionGradePersistence.insertAll(sectionId, grades);
        return new ServiceResponse<>((Void)null);

    }

    @Override
    public ServiceResponse<Long> replaceStudentSectionGrade(long schoolId,
                                                            long yearId, long termId, long sectionId, long studentId, StudentSectionGrade grade) {
        StatusCode code = studentSectionGradeExists(
                schoolId, yearId, termId, sectionId, studentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(
                studentSectionGradePersistence.update(sectionId, studentId, grade));
    }

    @Override
    public ServiceResponse<Long> updateStudentSectionGrade(long schoolId,
                                                           long yearId, long termId, long sectionId, long studentId, StudentSectionGrade grade) {
        StatusCode code = studentSectionGradeExists(
                schoolId, yearId, termId, sectionId, studentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        grade.mergePropertiesIfNull(studentSectionGradePersistence.select(sectionId, studentId));
        return new ServiceResponse<>(
                studentSectionGradePersistence.update(sectionId, studentId, grade));
    }

    @Override
    public ServiceResponse<Long> deleteStudentSectionGrade(long schoolId,
                                                           long yearId, long termId, long sectionId, long studentId) {
        StatusCode code = studentSectionGradeExists(
                schoolId, yearId, termId, sectionId, studentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        studentSectionGradePersistence.delete(sectionId, studentId);
        return new ServiceResponse<>((Long) null);
    }
}
