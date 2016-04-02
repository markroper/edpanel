package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.annotation.StudentAccessible;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.EntityId;
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
 * User: jordan
 * Date: 8/8/15
 * Time: 4:59 PM
 * 
 * Performs CRUD operations on student behavior events (e.g. suspensions, demerits, merits, etc)
 */
@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT)
public class BehaviorController extends BaseController {
    private static final String STUDENT_BEHAVIOR = "/students/{studentId}/behaviors";
    
    @ApiOperation(
            value = "Get all behaviors",
            notes = "Retrieve all behavior events",
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET,
            value = STUDENT_BEHAVIOR,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible(paramName = "studentId")
    public @ResponseBody ResponseEntity getAll(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @RequestParam(value="cutoffDate", required = false)
            @DateTimeFormat(pattern = EdPanelDateUtil.EDPANEL_DATE_FORMAT) LocalDate cuttoffDate) {
        return respond(pm.getBehaviorManager().getAllBehaviors(studentId, cuttoffDate));
    }

    @ApiOperation(
            value = "Get a behavior by ID",
            notes = "Retrieves one specific behavior event by ID",
            response = Behavior.class)
    @RequestMapping(
            value = STUDENT_BEHAVIOR + "/{behaviorId}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible(paramName = "studentId")
    public @ResponseBody ResponseEntity get(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "behaviorId", required = true, value = "Behavior ID")
            @PathVariable(value="behaviorId") Long behaviorId) {
        return respond(pm.getBehaviorManager().getBehavior(studentId, behaviorId));
    }
    
    @ApiOperation(
            value = "Create a behavior",
            notes = "Creates, assigns an ID to, persists and returns a behavior event",
            response = EntityId.class)
    @RequestMapping(
            value = STUDENT_BEHAVIOR,
            method = RequestMethod.POST,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity create(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId, 
            @RequestBody @Valid Behavior behavior) {
        return respond(pm.getBehaviorManager().createBehavior(studentId, behavior));
    }

    @ApiOperation(
            value = "Create multiple behaviors",
            notes = "Creates, assigns IDs to, persists behavior events",
            response = List.class)
    @RequestMapping(
            value = "/behaviors",
            method = RequestMethod.POST,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity bulkCreate(
            @RequestBody @Valid List<Behavior> behaviors) {
        return respond(pm.getBehaviorManager().createBehaviors(behaviors));
    }
    
    @ApiOperation(
            value = "Overwrite an existing behavior",
            notes = "Overwrites an existing behavior entity for the specified student with the ID provided",
            response = EntityId.class)
    @RequestMapping(
            value = STUDENT_BEHAVIOR + "/{behaviorId}",
            method = RequestMethod.PUT,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replaceBehavior(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "behaviorId", required = true, value = "Behavior ID")
            @PathVariable(value="behaviorId") Long behaviorId,
            @RequestBody @Valid Behavior behavior) {
        return respond(pm.getBehaviorManager().replaceBehavior(studentId, behaviorId, behavior));
    }

    @ApiOperation(
            value = "Update an existing behavior",
            notes = "Updates an existing behavior's properties. Will not overwrite existing values with null.",
            response = EntityId.class)
    @RequestMapping(
            value = STUDENT_BEHAVIOR + "/{behaviorId}",
            method = RequestMethod.PATCH,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity updateBehavior(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "behaviorId", required = true, value = "Behavior ID")
            @PathVariable(value="behaviorId") Long behaviorId,
            @RequestBody @Valid Behavior behavior) {
        return respond(pm.getBehaviorManager().updateBehavior(studentId, behaviorId, behavior));
    }

    @ApiOperation(
            value = "Delete a behavior",
            response = Void.class)
    @RequestMapping(
            value = STUDENT_BEHAVIOR + "/{behaviorId}",
            method = RequestMethod.DELETE,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteBehavior(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "behaviorId", required = true, value = "Behavior ID")
            @PathVariable(value="behaviorId") Long behaviorId) {
        return respond(pm.getBehaviorManager().deleteBehavior(studentId, behaviorId));
    }

    @ApiOperation(
            value = "Delete a behavior by source system ID",
            response = Void.class)
    @RequestMapping(
            value = STUDENT_BEHAVIOR + "/{ssid}/ssid",
            method = RequestMethod.DELETE,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteBehaviorBySsid(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "ssid", required = true, value = "SSID ID")
            @PathVariable(value="ssid") Long ssid) {
        return respond(pm.getBehaviorManager().deleteBehaviorBySsid(studentId, ssid));
    }
    
}
