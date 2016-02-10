package com.scholarscore.api.controller.uiControllers;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.annotation.StudentAccessible;
import com.scholarscore.api.controller.BaseController;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.goal.SectionGradeGoal;
import com.scholarscore.models.goal.Goal;
import com.scholarscore.models.goal.GoalType;
import com.scholarscore.models.ui.SectionGradeWithProgression;
import com.scholarscore.models.ui.StudentSectionDashboardData;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * These may be moved out of the core API and into a UI server in the future.
 * In fact they should be, but I've run out of time before the beta ;)
 *
 * Created by markroper on 11/17/15.
 */
@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/ui/students/{studentId}")
public class UiEndpointsController extends BaseController {

    @ApiOperation(
            value = "Get all the data for a single student needed for the student dashboard",
            notes = "Returns the current sections, section grades, section assignments, and grade progressions for a student",
            response = School.class)
    @RequestMapping(
            value = "/schools/{schoolId}/years/{schoolYearId}/terms/{termId}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible(paramName = "studentId")
    public @ResponseBody ResponseEntity
        getSchool(@ApiParam(name = "studentId", required = true, value = "Student ID")
             @PathVariable(value="studentId") Long studentId,
             @ApiParam(name = "schoolId", required = true, value = "School ID")
             @PathVariable(value="schoolId") Long schoolId,
             @ApiParam(name = "schoolYearId", required = true, value = "School year long ID")
             @PathVariable(value="schoolYearId") Long schoolYearId,
             @ApiParam(name = "termId", required = true, value = "Term ID")
             @PathVariable(value="termId") Long termId) {
        List<StudentSectionDashboardData> response = new ArrayList<>();
        ServiceResponse<Collection<Section>> sectionsResponse =
                pm.getSectionManager().getAllSections(studentId, schoolId, schoolYearId, termId);
        ServiceResponse<Collection<Goal>> goalsResponse = pm.getGoalManager().getAllGoals(studentId);
        HashMap<Long, SectionGradeGoal> sectionGoalMap = new HashMap<>();
        for (Goal goal: goalsResponse.getValue()) {
            if (goal.getGoalType() == GoalType.SECTION_GRADE) {
                SectionGradeGoal cumGoal = (SectionGradeGoal)goal;
                sectionGoalMap.put(cumGoal.getSection().getId(), cumGoal);
            }
        }
        Student onlyIdStudent = new Student.StudentBuilder().withId(studentId).build();
        if(null == sectionsResponse.getCode()) {
            for(Section s: sectionsResponse.getValue()) {
                StudentSectionDashboardData sectionDashData = new StudentSectionDashboardData();
                sectionDashData.setSection(s);
                SectionGradeGoal sectionGoal = sectionGoalMap.get(s.getId());
                if ( null != sectionGoal) {
                    sectionDashData.setGradeGoal(sectionGoal);
                } else {
                    Set<Staff> persons = s.getTeachers();
                    Staff t = null;
                    if(null != persons && !persons.isEmpty()) {
                        t = persons.iterator().next();
                    }
                    SectionGradeGoal fullSectionGradeGoalByBuilder = new SectionGradeGoal.SectionGradeGoalBuilder().
                            withSection(s).
                            withStudent(onlyIdStudent).
                            withApproved(Boolean.FALSE).
                            withDesiredValue(80D).
                            withName("Section Goal").
                            withStaff(t).
                            build();
                    ServiceResponse<Long> createdGoalResp =
                            pm.getGoalManager().createGoal(studentId, fullSectionGradeGoalByBuilder);
                    if(null != createdGoalResp.getValue()) {
                        fullSectionGradeGoalByBuilder.setId(createdGoalResp.getValue());
                    }
                    sectionDashData.setGradeGoal(fullSectionGradeGoalByBuilder);
                }
                ServiceResponse<Collection<StudentAssignment>> studAssesResp = pm.getStudentAssignmentManager().
                        getOneSectionOneStudentsAssignments(studentId,  s.getId());
                if(null == studAssesResp.getCode()) {
                    sectionDashData.setStudentAssignments(new ArrayList<>(studAssesResp.getValue()));
                }
                if(null == sectionDashData.getStudentAssignments() ||
                        sectionDashData.getStudentAssignments().isEmpty()){
                    continue;
                } else {
                    for(StudentAssignment sa: sectionDashData.getStudentAssignments()) {
                        sa.setStudent(null);
                    }
                }

                ServiceResponse<SectionGradeWithProgression> gradesByWeekResp =
                    pm.getStudentSectionGradeManager().getStudentSectionGradeByWeek(
                            schoolId, schoolYearId, termId, s.getId(), studentId);
                if(null == gradesByWeekResp.getCode()) {
                    sectionDashData.setGradeProgression(gradesByWeekResp.getValue());
                    sectionDashData.getGradeGoal().setCalculatedValue(gradesByWeekResp.getValue().getCurrentOverallGrade());
                }
                response.add(sectionDashData);
            }
        }
        return respond(new ServiceResponse<>(response));
    }

    @ApiOperation(
            value = "Get all the data for a single student needed for the student dashboard",
            notes = "Returns the current sections, section grades, section assignments, and grade progressions for a student",
            response = School.class)
    @RequestMapping(
            value = "/schools/{schoolId}/years/{schoolYearId}/terms/{termId}/teacher/{teacherId}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible(paramName = "studentId")
    public @ResponseBody ResponseEntity
    getStudentDemerits(@ApiParam(name = "studentId", required = true, value = "Student ID")
              @PathVariable(value="studentId") Long studentId,
              @ApiParam(name = "schoolId", required = true, value = "School ID")
              @PathVariable(value="schoolId") Long schoolId,
              @ApiParam(name = "schoolYearId", required = true, value = "School year long ID")
              @PathVariable(value="schoolYearId") Long schoolYearId,
              @ApiParam(name = "termId", required = true, value = "Term ID")
              @PathVariable(value="termId") Long termId,
               @ApiParam(name = "teacherId", required = true, value = "Teacher ID")
               @PathVariable(value="teacherId") Long teacherId) {
        //TODO THIS SHOULD ALL BE DONE BY QUERY GENERATOR
        Collection<Behavior> teacherAssignedDemerits = new ArrayList<>();
        Collection<Behavior> behaviors = pm.getBehaviorManager().getAllBehaviors(studentId).getValue();
        for (Behavior b : behaviors) {
            if (b.getAssigner().getId().equals(teacherId)) {
                //Teacher matches
                if (b.getBehaviorCategory() == BehaviorCategory.DEMERIT) {
                    //It is a demerit
                    teacherAssignedDemerits.add(b);
                }
            }
        }
        return respond(new ServiceResponse<>(teacherAssignedDemerits));
    }
}
