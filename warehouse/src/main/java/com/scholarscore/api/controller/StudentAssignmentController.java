package com.scholarscore.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scholarscore.models.EntityId;
import com.scholarscore.models.StudentAssignment;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/api/v1/schools/{schoolId}/years/{yrId}/terms/{tId}/sections/{sId}/assignments/{assignId}/studentassignments")
public class StudentAssignmentController extends BaseController {
    @ApiOperation(
            value = "Get all student assignments", 
            notes = "Retrieve all student assignments within a section", 
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAllSectionAssignments(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yrId", required = true, value = "School year ID")
            @PathVariable(value="yrId") Long yrId,
            @ApiParam(name = "tId", required = true, value = "Term ID")
            @PathVariable(value="tId") Long tId,
            @ApiParam(name = "sId", required = true, value = "Section ID")
            @PathVariable(value="sId") Long sId,
            @ApiParam(name = "assignId", required = true, value = "Section assignment ID")
            @PathVariable(value="assignId") Long assignId) {
        return respond(PM.getAllStudentAssignments(schoolId, yrId, tId, sId, assignId));
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
    public @ResponseBody ResponseEntity getStudentAssignment(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yrId", required = true, value = "School year ID")
            @PathVariable(value="yrId") Long yrId,
            @ApiParam(name = "tId", required = true, value = "Term long ID")
            @PathVariable(value="tId") Long tId,
            @ApiParam(name = "sId", required = true, value = "Section ID")
            @PathVariable(value="sId") Long sId,
            @ApiParam(name = "assignId", required = true, value = "Section assignment ID")
            @PathVariable(value="assignId") Long assignId,
            @ApiParam(name = "studAssignId", required = true, value = "Student assignment ID")
            @PathVariable(value="studAssignId") Long studAssignId) {
        return respond(PM.getStudentAssignment(schoolId, yrId, tId, sId, assignId, studAssignId));
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
            @ApiParam(name = "assignId", required = true, value = "Section assignment ID")
            @PathVariable(value="assignId") Long assignId,
            @RequestBody @Valid StudentAssignment studentAssignment) {
         return respond(PM.createStudentAssignment(schoolId, yrId, tId, sId, assignId, studentAssignment));
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
            @ApiParam(name = "assignId", required = true, value = "Section assignment ID")
            @PathVariable(value="assignId") Long assignId,
            @ApiParam(name = "studAssignId", required = true, value = "Section assignment ID")
            @PathVariable(value="studAssignId") Long studAssignId,
            @RequestBody @Valid StudentAssignment studentAssignment) {
        return respond(PM.replaceStudentAssignment(schoolId, yrId, tId, sId, assignId, studAssignId, studentAssignment));
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
            @ApiParam(name = "assignId", required = true, value = "Section assignment ID")
            @PathVariable(value="assignId") Long assignId,
            @ApiParam(name = "studAssignId", required = true, value = "Section assignment ID")
            @PathVariable(value="studAssignId") Long studAssignId,
            @RequestBody @Valid StudentAssignment studentAssignment) {
        return respond(PM.updateStudentAssignment(schoolId, yrId, tId, sId, assignId, studAssignId, studentAssignment));
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
            @ApiParam(name = "assignId", required = true, value = "Section assignment ID")
            @PathVariable(value="assignId") Long assignId,
            @ApiParam(name = "studAssignId", required = true, value = "Section assignment ID")
            @PathVariable(value="studAssignId") Long studAssignId) {
        return respond(PM.deleteStudentAssignment(schoolId, yrId, tId, sId, assignId, studAssignId));
    }
}
