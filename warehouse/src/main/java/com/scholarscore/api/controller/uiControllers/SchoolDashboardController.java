package com.scholarscore.api.controller.uiControllers;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.controller.BaseController;
import com.scholarscore.models.School;
import com.scholarscore.models.grade.Score;
import com.scholarscore.models.grade.StudentSectionGrade;
import com.scholarscore.models.ui.BreakdownCategories;
import com.scholarscore.models.ui.GenderBreakdown;
import com.scholarscore.models.ui.RaceBreakdown;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

/**
 * This serves as a UI server for information needed for the School Data Dashboard,
 * it renders information in ready to go chart form
 * Created by cwallace on 12/2/15.
 */
@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/ui/school/{schoolId}")
public class SchoolDashboardController extends BaseController {

    @ApiOperation(
            value = "Get all the data needed to generate the failing classes chart",
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

        if (breakdown == BreakdownCategories.RACE) {
            RaceBreakdown raceBreakdown = new RaceBreakdown();
            for (StudentSectionGrade grade : studentSectionGrades) {
                raceBreakdown.addToTotal(grade.getStudent());
                if (grade.getTermGrades() != null) {
                    Score termScore = grade.getTermGrades().get(termId);
                    if (null != termScore) {
                        Double termGrade = termScore.getScore();
                        if (termGrade != null && termGrade < 70) {
                            raceBreakdown.addFailingGrade(grade.getStudent());
                        }
                    }

                }

            }

            return respond(raceBreakdown.buildReturnObject());

        } else {
            GenderBreakdown genderBreakdown = new GenderBreakdown();
            for (StudentSectionGrade grade : studentSectionGrades) {
                genderBreakdown.addToTotal(grade.getStudent());
                if (grade.getTermGrades() != null) {
                    Score termScore = grade.getTermGrades().get(termId);
                    if (null != termScore) {
                        Double termGrade = termScore.getScore();
                        if (termGrade != null && termGrade < 70) {
                            genderBreakdown.addFailingGrade(grade.getStudent());
                        }
                    }
                }
            }

            return respond(genderBreakdown.buildReturnObject());
        }

    }
}
