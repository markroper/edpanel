package com.scholarscore.api.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scholarscore.api.util.ErrorCodes;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.Section;
import com.scholarscore.models.SectionAssignment;
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
        if(null == schoolId || !schools.containsKey(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == yearId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(yearId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, yearId });
        }
        if(null == schoolYears.get(schoolId).get(yearId).findTermById(termId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ TERM, termId });
        }
        if(!sections.containsKey(termId) || !sections.get(termId).containsKey(sectId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION, sectId });
        }
        
        Collection<SectionAssignment> returnSections = new ArrayList<>();
        if(null != sections.get(termId).get(sectId).getSectionAssignments()) {
            returnSections = sections.get(termId).get(sectId).getSectionAssignments();
        }
        return respond(returnSections);
    }
    
    @ApiOperation(
            value = "Get a section assignment", 
            notes = "Given an section assignment ID, return the section assignment instance", 
            response = SectionAssignment.class)
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
        if(null == schoolId || !schools.containsKey(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == yearId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(yearId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, yearId });
        }
        if(null == schoolYears.get(schoolId).get(yearId).findTermById(termId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ TERM, termId });
        }
        if(!sections.containsKey(termId) || !sections.get(termId).containsKey(sectId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION, sectId });
        }     
        if(null == sections.get(termId).get(sectId).getSectionAssignments() || 
                null == sections.get(termId).get(sectId).findAssignmentById(assignmentId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION_ASSIGNMENT, assignmentId });
        } 
        return respond(sections.get(termId).get(sectId).findAssignmentById(assignmentId));
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
            @RequestBody @Valid SectionAssignment sectionAssignment) {
        if(null == schoolId || !schools.containsKey(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == yearId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(yearId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, yearId });
        }
        if(null == schoolYears.get(schoolId).get(yearId).findTermById(termId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ TERM, termId });
        }
        if(!sections.containsKey(termId) || !sections.get(termId).containsKey(sectId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION, sectId });
        }  
        if(null == sections.get(termId).get(sectId).getSectionAssignments()) {
            sections.get(termId).get(sectId).setSectionAssignments(new ArrayList<SectionAssignment>());
        } 
        //TODO: check for the student with id studentId
        sectionAssignment.setId(sectionAssignmentCounter.getAndIncrement());
        sections.get(termId).get(sectId).getSectionAssignments().add(sectionAssignment);
        return respond(new EntityId(sectionAssignment.getId()));
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
            @RequestBody @Valid SectionAssignment sectionAssignment) {
        if(null == schoolId || !schools.containsKey(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == yearId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(yearId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, yearId });
        }
        if(null == schoolYears.get(schoolId).get(yearId).findTermById(termId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ TERM, termId });
        }
        if(!sections.containsKey(termId) || !sections.get(termId).containsKey(sectId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION, sectId });
        } 
        if(null == sections.get(termId).get(sectId).getSectionAssignments() || 
                null == sections.get(termId).get(sectId).findAssignmentById(assignmentId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION_ASSIGNMENT, assignmentId });
        }
        sectionAssignment.setId(assignmentId);
        List<SectionAssignment> assignments = sections.get(termId).get(sectId).getSectionAssignments();
        replaceSectionAssignment(assignments, sectionAssignment);
        return respond(new EntityId(assignmentId));
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
            @RequestBody @Valid SectionAssignment sectionAssignment) {
        if(null == schoolId || !schools.containsKey(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == yearId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(yearId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, yearId });
        }
        if(null == schoolYears.get(schoolId).get(yearId).findTermById(termId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ TERM, termId });
        }
        if(!sections.containsKey(termId) || !sections.get(termId).containsKey(sectId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION, sectId });
        }
        if(null == sections.get(termId).get(sectId).getSectionAssignments() || 
                null == sections.get(termId).get(sectId).findAssignmentById(assignmentId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION_ASSIGNMENT, assignmentId });
        }
        sectionAssignment.setId(assignmentId);
        sectionAssignment.mergePropertiesIfNull(sections.get(termId).get(sectId).findAssignmentById(assignmentId));   
        List<SectionAssignment> assignments = sections.get(termId).get(sectId).getSectionAssignments();
        replaceSectionAssignment(assignments, sectionAssignment);
        return respond(new EntityId(assignmentId));
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
        if(null == schoolId || !schools.containsKey(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == yearId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(yearId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, yearId });
        }
        if(null == schoolYears.get(schoolId).get(yearId).findTermById(termId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ TERM, termId });
        }
        if(!sections.containsKey(termId) || !sections.get(termId).containsKey(sectId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION, sectId });
        }
        if(null == sections.get(termId).get(sectId).getSectionAssignments() || 
                null == sections.get(termId).get(sectId).findAssignmentById(assignmentId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION_ASSIGNMENT, assignmentId });
        } 
        SectionAssignment sectAssignment = sections.get(termId).get(sectId).findAssignmentById(assignmentId);;
        sections.get(termId).get(sectId).getSectionAssignments().remove(sectAssignment);
        return respond((Section)null);
    }
    
    private void replaceSectionAssignment(List<SectionAssignment> assignments, SectionAssignment assignment) {
        int idx = -1;
        for(int i = 0; i < assignments.size(); i++) {
            if(assignments.get(i).getId().equals(assignment.getId())) {
                idx = i;
                break;
            }
        }
        if(idx >= 0) {
            assignments.set(idx, assignment);
        }
    }
}
