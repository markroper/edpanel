package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.Goal;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by cwallace on 9/17/2015.
 */
@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/students/{studentId}/goals")
public class GoalsController extends BaseController {


    @ApiOperation(
            value = "Get all goals for a student",
            notes = "Retrieve all goals",
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody
    ResponseEntity getAll(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId) {
        return respond(pm.getBehaviorManager().getAllBehaviors(studentId));
    }

    @ApiOperation(
            value = "Get a goal by ID",
            notes = "Retrieves one specific goal by ID",
            response = Behavior.class)
    @RequestMapping(
            value = "/{behaviorId}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity get(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "behaviorId", required = true, value = "Behavior ID")
            @PathVariable(value="behaviorId") Long behaviorId) {
        return respond(pm.getBehaviorManager().getBehavior(studentId, behaviorId));
    }

    @ApiOperation(
            value = "Create a goal",
            notes = "Creates, assigning an ID to, and persists a student goal",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity create(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @RequestBody @Valid Goal goal) {
        return respond(pm.getGoalManager().createGoal(studentId, goal));
    }

    @ApiOperation(
            value = "Overwrite an existing goal",
            notes = "Overwrites an existing goal for the specified student with the ID provided",
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
            value = "Update an existing goal",
            notes = "Updates an existing goal's properties. Will not overwrite existing values with null.",
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
            value = "Delete a goal",
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

}
