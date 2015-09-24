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
            value = "Get all goals for a teacher",
            notes = "Retrieve all goals",
            response = List.class)
    @RequestMapping(
            value = {"/teacher/{teacherId}"},
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody
    ResponseEntity getAllForTeacher(
            //TODO HOW CAN I GET THIS CONTROLLER TO TAKE THE PATH /teacher/id/goal!
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "teacherId", required = true, value = "IGoal ID")
            @PathVariable(value="teacherId") Long teacherId){
        return respond(pm.getGoalManager().getAllGoalsTeacher(teacherId));
    }


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
        return respond(pm.getGoalManager().getAllGoals(studentId));
    }

    @ApiOperation(
            value = "Get a goal by ID",
            notes = "Retrieves one specific goal by ID",
            response = Behavior.class)
    @RequestMapping(
            value = "/{goalId}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity get(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "goalId", required = true, value = "IGoal ID")
            @PathVariable(value="goalId") Long goalId) {
        return respond(pm.getGoalManager().getGoal(studentId, goalId));
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
            value = "/{goalId}",
            method = RequestMethod.PUT,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replaceGoal(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "goalId", required = true, value = "IGoal ID")
            @PathVariable(value="goalId") Long goalId,
            @RequestBody @Valid Goal goal) {
        return respond(pm.getGoalManager().replaceGoal(studentId, goalId, goal));
    }

    @ApiOperation(
            value = "Update an existing goal",
            notes = "Updates an existing goal's properties. Will not overwrite existing values with null.",
            response = EntityId.class)
    @RequestMapping(
            value = "/{goalId}",
            method = RequestMethod.PATCH,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity updateGoal(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "goalId", required = true, value = "IGoal ID")
            @PathVariable(value="goalId") Long goalId,
            @RequestBody @Valid Goal goal) {
        return respond(pm.getGoalManager().updateGoal(studentId, goalId, goal));
    }

    @ApiOperation(
            value = "Delete a goal",
            response = Void.class)
    @RequestMapping(
            value = "/{goalId}",
            method = RequestMethod.DELETE,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteGoal(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "goalId", required = true, value = "IGoal ID")
            @PathVariable(value="goalId") Long goalId) {
        return respond(pm.getGoalManager().deleteGoal(studentId, goalId));
    }

}
