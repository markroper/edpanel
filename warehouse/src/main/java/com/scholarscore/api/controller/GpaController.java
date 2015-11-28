package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.gpa.Gpa;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by markroper on 11/24/15.
 */
@Controller
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
        return respond(pm.getGpaManager().getGpa(studentId));
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
        List<Long> studentIds = new ArrayList<>();
        studentIds.add(studentId);
        return respond(pm.getGpaManager().getAllGpasForStudents(
                studentIds, LocalDate.of(2000, 1, 1), LocalDate.now().plusYears(1L)));
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
        return respond(pm.getGpaManager().createGpa(studentId, gpa));
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
        return respond(pm.getGpaManager().deleteGpa(studentId, gpaId));
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
        return respond(pm.getGpaManager().updateGpa(studentId, gpaId, gpa));
    }
}
