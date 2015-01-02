package com.scholarscore.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scholarscore.models.Assignment;
import com.scholarscore.models.EntityId;
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
@RequestMapping("/api/v1/schools/{schoolId}/courses/{courseId}/assignments")
public class AssignmentController extends BaseController {
    @ApiOperation(
            value = "Get all assignments", 
            notes = "Retrieve all assignments", 
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAllAssignments(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "courseId", required = true, value = "Course ID")
            @PathVariable(value="courseId") Long courseId) {
        return respond(PM.getAllAssignments(schoolId, courseId));
    }
    
    @ApiOperation(
            value = "Get an assignment", 
            notes = "Given an assignment ID, the endpoint returns the assignment", 
            response = Assignment.class)
    @RequestMapping(
            value = "/{assignmentId}", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAssignment(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "courseId", required = true, value = "Course ID")
            @PathVariable(value="courseId") Long courseId,
            @ApiParam(name = "assignmentId", required = true, value = "Assignment ID")
            @PathVariable(value="assignmentId") Long assignmentId) {
        return respond(PM.getAssignment(schoolId, courseId, assignmentId));
    }

    @ApiOperation(
            value = "Create an assignment", 
            notes = "Creates, assigns and ID to, persists and returns an assignment",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST, 
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity createAssignment(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "courseId", required = true, value = "Course ID")
            @PathVariable(value="courseId") Long courseId,
            @RequestBody @Valid Assignment assignment) {
        return respond(PM.createAssignment(schoolId, courseId, assignment));
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
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "courseId", required = true, value = "Course ID")
            @PathVariable(value="courseId") Long courseId,
            @ApiParam(name = "assignmentId", required = true, value = "Assignment ID")
            @PathVariable(value="assignmentId") Long assignmentId,
            @RequestBody @Valid Assignment assignment) {
        return respond(PM.replaceAssignment(schoolId, courseId, assignmentId, assignment));
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
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "courseId", required = true, value = "Course ID")
            @PathVariable(value="courseId") Long courseId,
            @ApiParam(name = "assignmentId", required = true, value = "Assignment ID")
            @PathVariable(value="assignmentId") Long assignmentId,
            @RequestBody @Valid Assignment assignment) {
        return respond(PM.updateAssignment(schoolId, courseId, assignmentId, assignment));
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
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "courseId", required = true, value = "Course ID")
            @PathVariable(value="courseId") Long courseId,
            @ApiParam(name = "assignmentId", required = true, value = "Assignment ID")
            @PathVariable(value="assignmentId") Long assignmentId) {
        return respond(PM.deleteAssignment(schoolId, courseId, assignmentId));
    }
}
