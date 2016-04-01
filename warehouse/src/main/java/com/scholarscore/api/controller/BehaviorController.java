package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.annotation.StudentAccessible;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.EntityId;
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
import java.util.List;

/**
 * User: jordan
 * Date: 8/8/15
 * Time: 4:59 PM
 * 
 * Performs CRUD operations on student behavior events (e.g. suspensions, demerits, merits, etc)
 */
@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/students/{studentId}/behaviors")
public class BehaviorController extends BaseController {

    
    @ApiOperation(
            value = "Get all behaviors",
            notes = "Retrieve all behavior events",
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible(paramName = "studentId")
    public @ResponseBody ResponseEntity getAll(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId) {
        return respond(pm.getBehaviorManager().getAllBehaviors(studentId));
    }

    @ApiOperation(
            value = "Get a behavior by ID",
            notes = "Retrieves one specific behavior event by ID",
            response = Behavior.class)
    @RequestMapping(
            value = "/{behaviorId}",
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
            value = "Overwrite an existing behavior",
            notes = "Overwrites an existing behavior entity for the specified student with the ID provided",
            response = EntityId.class)
    @RequestMapping(
            value = "/{behaviorId}",
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
            value = "/{behaviorId}",
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
            value = "/{behaviorId}",
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
            value = "/{ssid}/ssid",
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
