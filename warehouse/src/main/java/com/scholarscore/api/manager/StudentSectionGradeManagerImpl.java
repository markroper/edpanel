package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.StudentSectionGradePersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.grade.GradeAsOfWeek;
import com.scholarscore.models.Section;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.Term;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.grade.SectionGradeWithProgression;
import com.scholarscore.models.gradeformula.GradeFormula;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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
            //Get the section, and pull off the section formula
            ServiceResponse<Section> sect = pm.getSectionManager().getSection(schoolId, yearId, termId, sectionId);
            if(null == sect.getCode() || sect.getCode().isOK()) {
                ServiceResponse<Collection<StudentAssignment>> assignmentResp =
                        pm.getStudentAssignmentManager().getOneSectionOneStudentsAssignments(studentId, schoolId, yearId, termId, sectionId);
                ServiceResponse<Term> termResp = pm.getTermManager().getTerm(schoolId, yearId, termId);
                Term term = null;
                if(null == termResp.getCode() || termResp.getCode().isOK()) {
                    term = termResp.getValue();
                }
                if(null == assignmentResp.getCode() || assignmentResp.getCode().isOK()) {
                    //TODO: this applies a single formula to all assignments, instead we need to break it up by term
                    GradeFormula formula = sect.getValue().getGradeFormula();
                    if(null != term) {
                        if(null == formula) {
                            formula = new GradeFormula();
                            formula.setStartDate(term.getStartDate());
                            formula.setEndDate(term.getEndDate());
                        } else {
                            formula = formula.resolveFormulaMatchingDates(term.getStartDate(), term.getEndDate());
                            if(null == formula) {
                                formula = sect.getValue().getGradeFormula();
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

        List<GradeAsOfWeek> grades = new ArrayList<>();
        ServiceResponse<Section> sect = pm.getSectionManager().getSection(schoolId, yearId, termId, sectionId);
        if(null == sect.getCode() || sect.getCode().isOK()) {
            ServiceResponse<Collection<StudentAssignment>> assignmentResp =
                    pm.getStudentAssignmentManager().getOneSectionOneStudentsAssignments(studentId, schoolId, yearId, termId, sectionId);
            ServiceResponse<Term> termResp = pm.getTermManager().getTerm(schoolId, yearId, termId);
            Term term = null;
            if(null == termResp.getCode() || termResp.getCode().isOK()) {
                term = termResp.getValue();
            }
            if(null == assignmentResp.getCode() || assignmentResp.getCode().isOK()) {
                GradeFormula formula = sect.getValue().getGradeFormula();
                if(null != term) {
                    if(null == formula) {
                        formula = new GradeFormula();
                        formula.setStartDate(term.getStartDate());
                        formula.setEndDate(term.getEndDate());
                    } else {
                        formula = formula.resolveFormulaMatchingDates(term.getStartDate(), term.getEndDate());
                        if(null == formula) {
                            formula = sect.getValue().getGradeFormula();
                        }
                    }
                }
                ArrayList<StudentAssignment> assignments = (ArrayList<StudentAssignment>)assignmentResp.getValue();
                //Sort by due date
                assignments.stream().sorted((object1, object2) ->
                        object1.getAssignment().getDueDate().compareTo(object2.getAssignment().getDueDate()));
                Date currentLastDayOfWeek = null;
                int i = 1;
                Calendar cal  = Calendar.getInstance();
                for(StudentAssignment a: assignments) {
                    Date dueDate = a.getAssignment().getDueDate();
                    cal.setTime(dueDate);
                    int currentDay = cal.get(Calendar.DAY_OF_WEEK);
                    int leftDays= Calendar.SATURDAY - currentDay;
                    cal.add(Calendar.DATE, leftDays);
                    if(null == currentLastDayOfWeek) {
                        currentLastDayOfWeek = cal.getTime();
                    }
                    if(!currentLastDayOfWeek.equals(cal.getTime())) {
                        Set<StudentAssignment> subassignments = new HashSet<>(assignments.subList(0, i));
                        GradeAsOfWeek g = new GradeAsOfWeek();
                        g.setWeekEnding(currentLastDayOfWeek);
                        g.setScore(formula.calculateGrade(subassignments));
                        grades.add(g);
                        currentLastDayOfWeek = cal.getTime();
                    }
                    i++;
                }
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
