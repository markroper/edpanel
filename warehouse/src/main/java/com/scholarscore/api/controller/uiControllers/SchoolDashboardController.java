package com.scholarscore.api.controller.uiControllers;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.controller.BaseController;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.School;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.grade.Score;
import com.scholarscore.models.grade.StudentSectionGrade;
import com.scholarscore.models.ui.BreakdownCategories;
import com.scholarscore.models.ui.GenderBreakdown;
import com.scholarscore.models.ui.RaceBreakdown;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This serves as a UI server for information needed for the School Data Dashboard,
 * it renders information in ready to go chart form
 * Created by cwallace on 12/2/15.
 */
@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/ui/school/{schoolId}")
public class SchoolDashboardController extends BaseController {
    private final static Logger LOGGER = LoggerFactory.getLogger(SchoolDashboardController.class);

    @ApiOperation(
            value = "Returns graphable results for assignment scores broken down by various student meta data",
            response = List.class)
    @RequestMapping(
            value = "/assignmentanalyses",
            method = RequestMethod.POST,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody
    ResponseEntity getGraphableAssignmentResults(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @RequestBody List<Long> assignmentIds) {
        Collection<School> schools = pm.getSchoolManager().getAllSchools();
        Map<Long, String> schoolMap = new HashMap<>();
        for(School s: schools) {
            schoolMap.put(s.getId(), s.getName());
        }
        ServiceResponse<Collection<StudentAssignment>> asses =
            pm.getStudentAssignmentManager().getAllStudentAssignments(schoolId, assignmentIds);
        List<AssignmentResult> resultList = new ArrayList<>();
        if(null != asses.getValue()) {
            for(StudentAssignment sa: asses.getValue()) {
                AssignmentResult r = new AssignmentResult();
                r.setStudent(sa.getStudent());
                Long avail = sa.getAvailablePoints();
                Double awarded = sa.getAwardedPoints();
                if(null != avail && !avail.equals(0L) && null != awarded) {
                    r.setScore(awarded/avail);
                    resultList.add(r);
                } else {
                    LOGGER.info("There was an assignment with null values in the requested set");
                }
            }
        }
        AssignmentResults results = new AssignmentResults();
        results.setResults(resultList);
        results.setAssignmentIds(assignmentIds);
        results.setSchoolIdToName(schoolMap);
        results.calculateAndSetQuartiles();
        return respond(results);
    }

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
