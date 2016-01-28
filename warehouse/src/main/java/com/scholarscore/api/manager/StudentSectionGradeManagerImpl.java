package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.StudentSectionGradePersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.grade.SectionGrade;
import com.scholarscore.models.grade.StudentSectionGrade;
import com.scholarscore.models.gradeformula.GradeFormula;
import com.scholarscore.models.ui.ScoreAsOfWeek;
import com.scholarscore.models.ui.SectionGradeWithProgression;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
        if((null == complete || complete.equals(Boolean.FALSE)) &&
                (null == grade.getOverallGrade() || null == grade.getOverallGrade().getScore())) {
            Section sect = grade.getSection();
            ServiceResponse<Collection<StudentAssignment>> assignmentResp =
                    pm.getStudentAssignmentManager().getOneSectionOneStudentsAssignments(studentId, sectionId);
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
                    SectionGrade overall = new SectionGrade();
                    overall.setDate(LocalDate.now());
                    overall.setScore(calculatedGrade);
                    grade.setOverallGrade(overall);
                }
            }
        }
        return new ServiceResponse<>(grade);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ServiceResponse<SectionGradeWithProgression> getStudentSectionGradeByWeek(
            long schoolId, long yearId, long termId, long sectionId, long studentId) {
        StatusCode code = studentSectionGradeExists(
                schoolId, yearId, termId, sectionId, studentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        ServiceResponse<StudentSectionGrade> ssgResp = getStudentSectionGrade(schoolId, yearId, termId, sectionId, studentId);

        SectionGradeWithProgression gradeWithProgression = new SectionGradeWithProgression();
        gradeWithProgression.setCurrentOverallGrade(ssgResp.getValue().getOverallGrade().getScore());
        gradeWithProgression.setTermGrades(ssgResp.getValue().getTermGrades());
        Section section = ssgResp.getValue().getSection();

        List<ScoreAsOfWeek> grades = new ArrayList<>();
        ServiceResponse<Collection<StudentAssignment>> assignmentResp =
                pm.getStudentAssignmentManager().getOneSectionOneStudentsAssignments(studentId, sectionId);
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
            Map<LocalDate, SectionGrade> storedGradeMap = getStoredGradeHistoryForStudentInSection(sectionId, studentId, null, null);
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
                    //If there is a cached grade from the SIS, use it, otherwise calculate the grade
                    if(storedGradeMap.containsKey(endOfWeek)) {
                        g.setScore(storedGradeMap.get(endOfWeek).getScore());
                    } else {
                        g.setScore(formula.calculateGrade(subassignments));
                    }
                    grades.add(g);
                    currentLastDayOfWeek = endOfWeek;
                }
                i++;
            }
        }
        gradeWithProgression.setWeeklyGradeProgression(grades);
        return new ServiceResponse<>(gradeWithProgression);
    }

    private Map<LocalDate, SectionGrade> getStoredGradeHistoryForStudentInSection(
            long sectionId, long studentId, LocalDate start, LocalDate end) {
        List<SectionGrade> sgs =
                studentSectionGradePersistence.getSectionGradeOverTime(studentId, sectionId, start, end);
        Map<LocalDate, SectionGrade> gradeMap = new HashMap<>();
        if(null != sgs) {
            for (SectionGrade g : sgs) {
                gradeMap.put(g.getDate(), g);
            }
        }
        return gradeMap;
    }

    /**
     * Returns a student's section grade as of the specified date, pulling the value first from the cached historical
     * values stored from the SIS.  If there is not stored value, one is calculated.  If the date is out of bounds
     * or the request is otherwise unresolvable, null is returned.
     *
     * @param studentId
     * @param sectionId
     * @param date
     * @return
     */
    public SectionGrade getStudentSectionGradeAsOfDate(long studentId, long sectionId, LocalDate date) {
        StudentSectionGrade grade = studentSectionGradePersistence.select(sectionId, studentId);
        if(null == grade) {
            return null;
        }
        Map<LocalDate, SectionGrade> storedGradeMap = getStoredGradeHistoryForStudentInSection(sectionId, studentId, null, null);
        if(storedGradeMap.containsKey(date)){
            return storedGradeMap.get(date);
        }

        Section section = grade.getSection();
        ServiceResponse<Collection<StudentAssignment>> assignmentResp =
                pm.getStudentAssignmentManager().getOneSectionOneStudentsAssignments(studentId, sectionId);
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
            assignments.sort((object1, object2) -> object1.getAssignment().getDueDate().compareTo(object2.getAssignment().getDueDate()));
            int i = 0;
            if(null != assignments) {
                for (StudentAssignment a : assignments) {
                    i++;
                    LocalDate dueDate = a.getAssignment().getDueDate();
                    if (dueDate.isEqual(date) || dueDate.isAfter(date)) {
                        continue;
                    }
                    Set<StudentAssignment> subassignments = new HashSet<>(assignments.subList(0, i));
                    SectionGrade gr = new SectionGrade();
                    gr.setSectionFk(sectionId);
                    gr.setStudentFk(studentId);
                    gr.setDate(date);
                    gr.setScore(formula.calculateGrade(subassignments));
                    return gr;
                }
            }
        }
        return null;
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
        if(null != grade.getOverallGrade()) {
            grade.getOverallGrade().setStudentFk(studentId);
            grade.getOverallGrade().setSectionFk(sectionId);
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
        for(StudentSectionGrade ssg: grades) {
            if(null != ssg.getOverallGrade()) {
                ssg.getOverallGrade().setStudentFk(ssg.getStudent().getId());
                ssg.getOverallGrade().setSectionFk(sectionId);
            }
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
        if(null != grade.getOverallGrade()) {
            grade.getOverallGrade().setStudentFk(grade.getStudent().getId());
            grade.getOverallGrade().setSectionFk(sectionId);
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
        if(null != grade.getOverallGrade()) {
            grade.getOverallGrade().setStudentFk(studentId);
            grade.getOverallGrade().setSectionFk(sectionId);
        }
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
