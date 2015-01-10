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

import com.scholarscore.models.Assignment;
import com.scholarscore.models.EntityId;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/api/v1/schools/{schoolId}/years/{yearId}/terms/{termId}/sections/{sectId}/sectassignments")
public class SectionAssignmentController extends BaseController {
    @ApiOperation(
            value = "Get all section assignments", 
            notes = "Retrieve all section assignments within a section", 
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAllSectionAssignments(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yearId", required = true, value = "School year ID")
            @PathVariable(value="yearId") Long yearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId,
            @ApiParam(name = "sectId", required = true, value = "Section ID")
            @PathVariable(value="sectId") Long sectId) {
        return respond(PM.getAllAssignments(schoolId, yearId, termId, sectId));
    }
    
    @ApiOperation(
            value = "Get a section assignment", 
            notes = "Given an section assignment ID, return the section assignment instance", 
            response = Assignment.class)
    @RequestMapping(
            value = "/{assignmentId}", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getSectionAssignment(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yearId", required = true, value = "School year ID")
            @PathVariable(value="yearId") Long yearId,
            @ApiParam(name = "termId", required = true, value = "Term long ID")
            @PathVariable(value="termId") Long termId,
            @ApiParam(name = "sectId", required = true, value = "Section ID")
            @PathVariable(value="sectId") Long sectId,
            @ApiParam(name = "assignmentId", required = true, value = "Section assignment ID")
            @PathVariable(value="assignmentId") Long assignmentId) {
        return respond(PM.getAssignment(schoolId, yearId, termId, sectId, assignmentId));
    }

    @ApiOperation(
            value = "Create a section assignment", 
            notes = "Creates, assigns and ID to, persists and returns a section assignment",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST, 
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity createSectionAssignment(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yearId", required = true, value = "School year ID")
            @PathVariable(value="yearId") Long yearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId,
            @ApiParam(name = "sectId", required = true, value = "Section ID")
            @PathVariable(value="sectId") Long sectId,
            @RequestBody @Valid Assignment sectionAssignment) {
        return respond(PM.createAssignment(schoolId, yearId, termId, sectId, sectionAssignment));
    }

    @ApiOperation(
            value = "Overwrite an existing section assignment", 
            notes = "Overwrites an existing section assignment with the ID provided",
            response = EntityId.class)
    @RequestMapping(
            value = "/{assignmentId}",
            method = RequestMethod.PUT, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replaceSectionAssignment(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yearId", required = true, value = "School year ID")
            @PathVariable(value="yearId") Long yearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId,
            @ApiParam(name = "sectId", required = true, value = "Section ID")
            @PathVariable(value="sectId") Long sectId,
            @ApiParam(name = "assignmentId", required = true, value = "Section assignment ID")
            @PathVariable(value="assignmentId") Long assignmentId,
            @RequestBody @Valid Assignment sectionAssignment) {
        return respond(PM.replaceAssignment(schoolId, yearId, termId, sectId, assignmentId, sectionAssignment));
    }
    
    @ApiOperation(
            value = "Update an existing section assignment", 
            notes = "Updates an existing section assigmment. Will not overwrite existing values with null.",
            response = EntityId.class)
    @RequestMapping(
            value = "/{assignmentId}", 
            method = RequestMethod.PATCH, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity updateTerm(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yearId", required = true, value = "School year ID")
            @PathVariable(value="yearId") Long yearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId,
            @ApiParam(name = "sectId", required = true, value = "Section ID")
            @PathVariable(value="sectId") Long sectId,
            @ApiParam(name = "assignmentId", required = true, value = "Section assignment ID")
            @PathVariable(value="assignmentId") Long assignmentId,
            @RequestBody @Valid Assignment sectionAssignment) {
        return respond(PM.updateAssignment(schoolId, yearId, termId, sectId, assignmentId, sectionAssignment));
    }

    @ApiOperation(
            value = "Delete a section assignment", 
            notes = "Deletes the section assignment with the ID provided",
            response = Void.class)
    @RequestMapping(
            value = "/{assignmentId}", 
            method = RequestMethod.DELETE, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteTerm(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yearId", required = true, value = "School year ID")
            @PathVariable(value="yearId") Long yearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId,
            @ApiParam(name = "sectId", required = true, value = "Section ID")
            @PathVariable(value="sectId") Long sectId,
            @ApiParam(name = "assignmentId", required = true, value = "Section assignment ID")
            @PathVariable(value="assignmentId") Long assignmentId) {
        return respond(PM.deleteAssignment(schoolId, yearId, termId, sectId, assignmentId));
    }
}
