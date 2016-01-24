package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.annotation.StudentAccessible;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.assignment.StudentAssignment;
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
 * TODO: make the get student assignments endpoints filter by yser in the case where the requesting user is a student
 */
@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/schools/{schoolId}/years/{yrId}/terms/{tId}/sections/{sId}/assignments/{assignId}/studentassignments")
public class StudentAssignmentController extends BaseController {


    @ApiOperation(
            value = "Get all student assignments", 
            notes = "Retrieve all student assignments within a section", 
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible
    public @ResponseBody ResponseEntity getAllSectionAssignments(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yrId", required = true, value = "School year ID")
            @PathVariable(value="yrId") Long yrId,
            @ApiParam(name = "tId", required = true, value = "Term ID")
            @PathVariable(value="tId") Long tId,
            @ApiParam(name = "sId", required = true, value = "Section ID")
            @PathVariable(value="sId") Long sId,
            @ApiParam(name = "assignId", required = true, value = "Assignment ID")
            @PathVariable(value="assignId") Long assignId) {
        return respond(pm.getStudentAssignmentManager().getAllStudentAssignments(schoolId, yrId, tId, sId, assignId));
    }
    
    @ApiOperation(
            value = "Get a student assignment", 
            notes = "Given an student assignment ID, return the student assignment instance", 
            response = StudentAssignment.class)
    @RequestMapping(
            value = "/{studAssignId}", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible
    public @ResponseBody ResponseEntity getStudentAssignment(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yrId", required = true, value = "School year ID")
            @PathVariable(value="yrId") Long yrId,
            @ApiParam(name = "tId", required = true, value = "Term long ID")
            @PathVariable(value="tId") Long tId,
            @ApiParam(name = "sId", required = true, value = "Section ID")
            @PathVariable(value="sId") Long sId,
            @ApiParam(name = "assignId", required = true, value = "Assignment ID")
            @PathVariable(value="assignId") Long assignId,
            @ApiParam(name = "studAssignId", required = true, value = "Student assignment ID")
            @PathVariable(value="studAssignId") Long studAssignId) {
        return respond(pm.getStudentAssignmentManager().getStudentAssignment(schoolId, yrId, tId, sId, assignId, studAssignId));
    }

    @ApiOperation(
            value = "Create a student assignment", 
            notes = "Creates, assigns and ID to, persists and returns a student assignment",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST, 
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity createStudentAssignment(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yrId", required = true, value = "School year ID")
            @PathVariable(value="yrId") Long yrId,
            @ApiParam(name = "tId", required = true, value = "Term ID")
            @PathVariable(value="tId") Long tId,
            @ApiParam(name = "sId", required = true, value = "Section ID")
            @PathVariable(value="sId") Long sId,
            @ApiParam(name = "assignId", required = true, value = "Assignment ID")
            @PathVariable(value="assignId") Long assignId,
            @RequestBody @Valid StudentAssignment studentAssignment) {
         return respond(pm.getStudentAssignmentManager().createStudentAssignment(schoolId, yrId, tId, sId, assignId, studentAssignment));
    }

    @ApiOperation(
            value = "Create multiple student assignments",
            notes = "Creates, assigns IDs to, and persists multiple student assignment",
            response = List.class)
    @RequestMapping(
            value = "/bulk",
            method = RequestMethod.POST,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity createBulkStudentAssignments(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yrId", required = true, value = "School year ID")
            @PathVariable(value="yrId") Long yrId,
            @ApiParam(name = "tId", required = true, value = "Term ID")
            @PathVariable(value="tId") Long tId,
            @ApiParam(name = "sId", required = true, value = "Section ID")
            @PathVariable(value="sId") Long sId,
            @ApiParam(name = "assignId", required = true, value = "Assignment ID")
            @PathVariable(value="assignId") Long assignId,
            @RequestBody List<StudentAssignment> studentAssignments) {
        return respond(pm.getStudentAssignmentManager().createBulkStudentAssignment(schoolId, yrId, tId, sId, assignId, studentAssignments));
    }

    @ApiOperation(
            value = "Overwrite an existing student assignment", 
            notes = "Overwrites an existing student assignment with the ID provided",
            response = EntityId.class)
    @RequestMapping(
            value = "/{studAssignId}",
            method = RequestMethod.PUT, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replaceStudentAssignment(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yrId", required = true, value = "School year ID")
            @PathVariable(value="yrId") Long yrId,
            @ApiParam(name = "tId", required = true, value = "Term ID")
            @PathVariable(value="tId") Long tId,
            @ApiParam(name = "sId", required = true, value = "Section ID")
            @PathVariable(value="sId") Long sId,
            @ApiParam(name = "assignId", required = true, value = "Assignment ID")
            @PathVariable(value="assignId") Long assignId,
            @ApiParam(name = "studAssignId", required = true, value = "Section assignment ID")
            @PathVariable(value="studAssignId") Long studAssignId,
            @RequestBody @Valid StudentAssignment studentAssignment) {
        return respond(pm.getStudentAssignmentManager().replaceStudentAssignment(schoolId, yrId, tId, sId, assignId, studAssignId, studentAssignment));
    }
    
    @ApiOperation(
            value = "Update an existing student assignment", 
            notes = "Updates an existing student assigmment. Will not overwrite existing values with null.",
            response = EntityId.class)
    @RequestMapping(
            value = "/{studAssignId}", 
            method = RequestMethod.PATCH, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity updateStudentAssignment(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yrId", required = true, value = "School year ID")
            @PathVariable(value="yrId") Long yrId,
            @ApiParam(name = "tId", required = true, value = "Term ID")
            @PathVariable(value="tId") Long tId,
            @ApiParam(name = "sId", required = true, value = "Section ID")
            @PathVariable(value="sId") Long sId,
            @ApiParam(name = "assignId", required = true, value = "Assignment ID")
            @PathVariable(value="assignId") Long assignId,
            @ApiParam(name = "studAssignId", required = true, value = "Section assignment ID")
            @PathVariable(value="studAssignId") Long studAssignId,
            @RequestBody @Valid StudentAssignment studentAssignment) {
        return respond(pm.getStudentAssignmentManager().updateStudentAssignment(schoolId, yrId, tId, sId, assignId, studAssignId, studentAssignment));
    }

    @ApiOperation(
            value = "Delete a student assignment", 
            notes = "Deletes the student assignment with the ID provided",
            response = Void.class)
    @RequestMapping(
            value = "/{studAssignId}", 
            method = RequestMethod.DELETE, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteStudentAssignment(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yrId", required = true, value = "School year ID")
            @PathVariable(value="yrId") Long yrId,
            @ApiParam(name = "tId", required = true, value = "Term ID")
            @PathVariable(value="tId") Long tId,
            @ApiParam(name = "sId", required = true, value = "Section ID")
            @PathVariable(value="sId") Long sId,
            @ApiParam(name = "assignId", required = true, value = "Assignment ID")
            @PathVariable(value="assignId") Long assignId,
            @ApiParam(name = "studAssignId", required = true, value = "Section assignment ID")
            @PathVariable(value="studAssignId") Long studAssignId) {
        return respond(pm.getStudentAssignmentManager().deleteStudentAssignment(schoolId, yrId, tId, sId, assignId, studAssignId));
    }
}
