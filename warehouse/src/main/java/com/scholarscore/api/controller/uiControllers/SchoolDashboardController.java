package com.scholarscore.api.controller.uiControllers;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.controller.BaseController;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.School;
import com.scholarscore.models.Section;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.goal.CumulativeGradeGoal;
import com.scholarscore.models.goal.Goal;
import com.scholarscore.models.goal.GoalType;
import com.scholarscore.models.ui.BreakdownCategories;
import com.scholarscore.models.ui.GenderBreakdown;
import com.scholarscore.models.ui.SectionGradeWithProgression;
import com.scholarscore.models.ui.StudentSectionDashboardData;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * This serves as a UI server for information needed for the School Data Dashboard,
 * it renders information in ready to go chart form
 * Created by cwallace on 12/2/15.
 */
@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/ui/school/{schoolId}")
public class SchoolDashboardController extends BaseController {

    @ApiOperation(
            value = "Get all the data needed to generate teh failing classes chart",
            notes = "Returns two arrays of arrays, see stackedbar directive for return object",
            response = School.class)
    @RequestMapping(
            value = "/years/{schoolYearId}/terms/{termId}/classes",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody
    ResponseEntity
    getFailingClasses(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
              @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "School year long ID")
              @PathVariable(value="schoolYearId") Long schoolYearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
              @PathVariable(value="termId") Long termId,
            @ApiParam(name="breakdown", required = false, value= "Category to breakdown data by")
            @RequestParam(required = false, value = "breakdown") BreakdownCategories breakdown){


        Collection<StudentSectionGrade> studentSectionGrades = pm.getStudentSectionGradeManager().getAllStudentSectionGradesByTerm(schoolId, schoolYearId, termId).getValue();

        GenderBreakdown genderBreakdown = new GenderBreakdown();
        for (StudentSectionGrade grade : studentSectionGrades) {
            //totalStudents.add(grade.getStudent());
            genderBreakdown.addToTotal(grade.getStudent());
            if (grade.getGrade() != null && grade.getGrade() < 70) {
                genderBreakdown.addFailingGrade(grade.getStudent());
            }
        }


        return respond(genderBreakdown.buildReturnObject());
    }
}
