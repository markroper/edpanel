package com.scholarscore.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scholarscore.models.Assignment;
import com.scholarscore.models.EntityId;
import com.scholarscore.api.util.ErrorCode;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import javax.validation.Valid;

/**
 * The class defines the REST controller end points for create, read, update and delete operations on the Assignment resource.
 * 
 * @author markroper
 * @see com.scholarscore.models.Assignment
 */
@Controller
@RequestMapping("/api/v1/assignment")
public class AssignmentController {
    //TODO: @mroper we need to add a real persistence layer that we call instead of manipulating this map
    public static final String JSON_ACCEPT_HEADER = "application/json";
    public static Map<Long, Assignment> assignments = new HashMap<>();
    public static Long nextAssignmentId = 0l;

    @ApiOperation(
            value = "Get all assignments", 
            notes = "Retrieve all assignments", 
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAllAssignments() {
        List<Assignment> assignmentsList = null;
        HttpStatus status = HttpStatus.NOT_FOUND;
        if(!assignments.isEmpty()) {
            assignmentsList = new ArrayList<>(assignments.values());
            status = HttpStatus.OK;
        }
        return new ResponseEntity<>(assignmentsList, status);
    }
    
    @ApiOperation(
            value = "Get an assignment", 
            notes = "Given an assignment ID, the endpoint returns the assignment ID", 
            response = Assignment.class)
    @RequestMapping(
            value = "/{assignmentId}", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAssignment(
            @ApiParam(name = "assignmentId", required = true, value = "The assignment long ID")
            @PathVariable(value="assignmentId") Long assignmentId) {
        Assignment assignment = null;
        HttpStatus status = HttpStatus.NOT_FOUND;
        if(assignments.containsKey(assignmentId)) {
            assignment = assignments.get(assignmentId);
            status = HttpStatus.OK;
        }
        return new ResponseEntity<>(assignment, status);
    }

    @ApiOperation(
            value = "Create an assignment", 
            notes = "Creates, assigns and ID to, persists and returns an assignment",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST, 
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity createAssignment(@RequestBody @Valid Assignment assignment) {
        assignment.setId(nextAssignmentId++);
        assignments.put(assignment.getId(), assignment);
        return new ResponseEntity<>(new EntityId(assignment.getId()), HttpStatus.OK);
    }

    @ApiOperation(
            value = "Overwrite an existing assignment", 
            notes = "Overwrites an existing assignment with the ID provided",
            response = EntityId.class)
    @RequestMapping(
            value = "/{assignmentId}", 
            method = RequestMethod.PUT, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replaceAssignment(
            @ApiParam(name = "assignmentId", required = true, value = "The assignment ID")
            @PathVariable(value="assignmentId") Long assignmentId,
            @RequestBody @Valid Assignment assignment) {
        ResponseEntity returnValue = null;
        if(null != assignmentId && assignments.containsKey(assignmentId)) {
            returnValue = new ResponseEntity<>(new EntityId(assignmentId), HttpStatus.OK);
            assignments.put(assignmentId, assignment);
        } else {
            returnValue = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return returnValue;
    }
    
    @ApiOperation(
            value = "Update an existing assignment", 
            notes = "Updates an existing assignment. Will not overwrite existing values with null.",
            response = EntityId.class)
    @RequestMapping(
            value = "/{assignmentId}", 
            method = RequestMethod.PATCH, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity updateAssignment(
            @ApiParam(name = "assignmentId", required = true, value = "The assignment ID")
            @PathVariable(value="assignmentId") Long assignmentId,
            @RequestBody @Valid Assignment assignment) {
        ResponseEntity returnValue = null;
        if(null != assignment && null != assignmentId && assignments.containsKey(assignmentId)) {
            assignment.mergePropertiesIfNull(assignments.get(assignmentId));
            assignments.put(assignmentId, assignment);
            returnValue = new ResponseEntity<>(new EntityId(assignmentId), HttpStatus.OK);
        } else {
            returnValue = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return returnValue;
    }

    @ApiOperation(
            value = "Delete an assignment", 
            notes = "Deletes the assignment with the ID provided",
            response = Void.class)
    @RequestMapping(
            value = "/{assignmentId}", 
            method = RequestMethod.DELETE, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteAssignment(
            @ApiParam(name = "assignmentId", required = true, value = "The assignment ID")
            @PathVariable(value="assignmentId") Long assignmentId) {
        HttpStatus status = HttpStatus.OK;
        if(null == assignmentId) {
            status = HttpStatus.BAD_REQUEST;
        } else if (!assignments.containsKey(assignmentId)) {
            status = HttpStatus.NOT_FOUND;
        }
        assignments.remove(assignmentId);
        return new ResponseEntity<>(null, status);
    }
}
