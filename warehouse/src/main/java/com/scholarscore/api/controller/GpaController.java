package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.gpa.Gpa;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by markroper on 11/24/15.
 */
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/students/{studentId}/gpas")
public class GpaController extends BaseController {
    //TODO: remove the GPA related endpoint in StudentController.js, replace references to it to an API here
    @ApiOperation(
            value = "Get a student's current GPA",
            notes = "Given a student ID, the endpoint returns the student's most recently stored GPA",
            response = Gpa.class)
    @RequestMapping(
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getGpa(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value = "studentId") Long studentId)
    {
//        Collection<? extends WeightedGradable> courseGrades =
//                pm.getStudentSectionGradeManager().getSectionGradesForStudent(studentId).getValue();
//        return respond(GradeUtil.calculateGPA(gpaScale, courseGrades));
        return null;
    }

    @ApiOperation(
            value = "Get a student's historical GPA's",
            notes = "Given a student ID, the endpoint returns the student's most recently stored GPA",
            response = List.class)
    @RequestMapping(
            value = "/historicals",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getGpasOverTime(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value = "studentId") Long studentId)
    {
//        Collection<? extends WeightedGradable> courseGrades =
//                pm.getStudentSectionGradeManager().getSectionGradesForStudent(studentId).getValue();
//        return respond(GradeUtil.calculateGPA(gpaScale, courseGrades));
        return null;
    }

    @ApiOperation(
            value = "Create a student GPA",
            notes = "Create a student GPA and if the calculated date is the most recent, make it the current GPA",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity createGpa(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value = "studentId") Long studentId,
            @RequestBody @Valid Gpa gpa)
    {
//        Collection<? extends WeightedGradable> courseGrades =
//                pm.getStudentSectionGradeManager().getSectionGradesForStudent(studentId).getValue();
//        return respond(GradeUtil.calculateGPA(gpaScale, courseGrades));
        return null;
    }

    @ApiOperation(
            value = "Delete a student GPA",
            notes = "Delete a student GPA and if it was the most recent, find the next most recent GPA and set it the current",
            response = Void.class)
    @RequestMapping(
            value = "/{gpaId}",
            method = RequestMethod.DELETE,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteGpa(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value = "studentId") Long studentId,
            @ApiParam(name = "gpaId", required = true, value = "GPA ID")
            @PathVariable(value = "gpaId") Long gpaId)
    {
//        Collection<? extends WeightedGradable> courseGrades =
//                pm.getStudentSectionGradeManager().getSectionGradesForStudent(studentId).getValue();
//        return respond(GradeUtil.calculateGPA(gpaScale, courseGrades));
        return null;
    }

    @ApiOperation(
            value = "Update a student GPA",
            notes = "Update a student GPA and if it was the most recent, find the next most recent GPA and set it the current",
            response = Void.class)
    @RequestMapping(
            value = "/{gpaId}",
            method = RequestMethod.PUT,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity updateGpa(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value = "studentId") Long studentId,
            @ApiParam(name = "gpaId", required = true, value = "GPA ID")
            @PathVariable(value = "gpaId") Long gpaId,
            @RequestBody @Valid Gpa gpa)
    {
//        Collection<? extends WeightedGradable> courseGrades =
//                pm.getStudentSectionGradeManager().getSectionGradesForStudent(studentId).getValue();
//        return respond(GradeUtil.calculateGPA(gpaScale, courseGrades));
        return null;
    }
}
