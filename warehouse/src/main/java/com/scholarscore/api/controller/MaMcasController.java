package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.annotation.StudentAccessible;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.state.ma.McasResult;
import com.scholarscore.util.EdPanelDateUtil;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by markroper on 4/10/16.
 */
@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT)
public class MaMcasController extends BaseController {
    @ApiOperation(
            value = "Get all MCAS results for a student",
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/schools/{schoolId}/students/{studentId}/mcas",
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible(paramName = "studentId")
    public @ResponseBody
    ResponseEntity getAll(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId) {
        return respond(pm.getMcasManager().getAllMcasResultsForStudent(schoolId, studentId));
    }

    @ApiOperation(
            value = "Get an MCAS result ID",
            response = McasResult.class)
    @RequestMapping(
            value = "/schools/{schoolId}/students/{studentId}/mcas/{mcasId}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible(paramName = "studentId")
    public @ResponseBody ResponseEntity get(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "mcasId", required = true, value = "MCAS ID")
            @PathVariable(value="mcasId") Long mcasId) {
        return respond(pm.getMcasManager().getMcasResult(schoolId, studentId, mcasId));
    }

    @ApiOperation(
            value = "Create an MCAS result for a student",
            notes = "Creates, assigns an ID to, persists and returns a behavior event",
            response = EntityId.class)
    @RequestMapping(
            value = "/schools/{schoolId}/students/{studentId}/mcas",
            method = RequestMethod.POST,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity create(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @RequestBody @Valid McasResult result) {
        return respond(pm.getMcasManager().createMcasResult(schoolId, studentId, result));
    }

    @ApiOperation(
            value = "Create multiple MCAS results",
            response = List.class)
    @RequestMapping(
            value = "/mcas",
            method = RequestMethod.POST,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity bulkCreate(
            @RequestBody @Valid List<McasResult> results) {
        return respond(pm.getMcasManager().createMcasResults(results));
    }

    @ApiOperation(
            value = "Overwrite an existing MCAS result",
            response = EntityId.class)
    @RequestMapping(
            value = "/schools/{schoolId}/students/{studentId}/mcas/{mcasId}",
            method = RequestMethod.PUT,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replaceBehavior(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "mcasId", required = true, value = "MCAS ID")
            @PathVariable(value="mcasId") Long mcasId,
            @RequestBody @Valid McasResult result) {
        return respond(pm.getMcasManager().replaceMcasResult(schoolId, studentId, mcasId, result));
    }

    @ApiOperation(
            value = "Delete an MCAS result",
            response = Void.class)
    @RequestMapping(
            value = "/schools/{schoolId}/students/{studentId}/mcas/{mcasId}",
            method = RequestMethod.DELETE,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteBehavior(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "mcasId", required = true, value = "MCAS ID")
            @PathVariable(value="mcasId") Long mcasId) {
        return respond(pm.getMcasManager().deleteMcasResult(schoolId, studentId, mcasId));
    }
}
