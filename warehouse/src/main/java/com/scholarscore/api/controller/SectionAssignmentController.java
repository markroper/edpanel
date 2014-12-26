package com.scholarscore.api.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
@RequestMapping("/api/v1/schools/{schoolId}/years/{schoolYearId}/terms/{termId}/sections/{sectionId}/sectionassignments")
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
            @ApiParam(name = "schoolYearId", required = true, value = "School year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId,
            @ApiParam(name = "sectionId", required = true, value = "Section ID")
            @PathVariable(value="sectionId") Long sectionId) {
        if(null == schoolId || !schoolExists(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == schoolYearId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(schoolYearId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, schoolYearId });
        }
        if(null == schoolYears.get(schoolId).get(schoolYearId).findTermById(termId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ TERM, termId });
        }
        if(!sections.containsKey(termId) || !sections.get(termId).containsKey(sectionId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION, sectionId });
        }
        
        Collection<SectionAssignment> returnSections = new ArrayList<>();
        if(null != sections.get(termId).get(sectionId).getSectionAssignments()) {
            returnSections = sections.get(termId).get(sectionId).getSectionAssignments().values();
        }
        return respond(returnSections);
    }
    
    @ApiOperation(
            value = "Get a section assignment", 
            notes = "Given an section assignment ID, return the section assignment instance", 
            response = SectionAssignment.class)
    @RequestMapping(
            value = "/{sectAssignmentId}", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getSectionAssignment(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "School year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId,
            @ApiParam(name = "termId", required = true, value = "Term long ID")
            @PathVariable(value="termId") Long termId,
            @ApiParam(name = "sectionId", required = true, value = "Section ID")
            @PathVariable(value="sectionId") Long sectionId,
            @ApiParam(name = "sectAssignmentId", required = true, value = "Section assignment ID")
            @PathVariable(value="sectAssignmentId") Long sectAssignmentId) {
        if(null == schoolId || !schoolExists(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == schoolYearId || !schoolExists(schoolId) || !schoolYears.get(schoolId).containsKey(schoolYearId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, schoolYearId });
        }
        if(null == schoolYears.get(schoolId).get(schoolYearId).findTermById(termId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ TERM, termId });
        }
        if(!sections.containsKey(termId) || !sections.get(termId).containsKey(sectionId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION, sectionId });
        }     
        if(null == sections.get(termId).get(sectionId).getSectionAssignments() || 
                !sections.get(termId).get(sectionId).getSectionAssignments().containsKey(sectAssignmentId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION_ASSIGNMENT, sectAssignmentId });
        } 
        return respond(sections.get(termId).get(sectionId).getSectionAssignments().get(sectAssignmentId));
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
            @ApiParam(name = "schoolYearId", required = true, value = "School year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId,
            @ApiParam(name = "sectionId", required = true, value = "Section ID")
            @PathVariable(value="sectionId") Long sectionId,
            @RequestBody @Valid SectionAssignment sectionAssignment) {
        if(null == schoolId || !schoolExists(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == schoolYearId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(schoolYearId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, schoolYearId });
        }
        if(null == schoolYears.get(schoolId).get(schoolYearId).findTermById(termId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ TERM, termId });
        }
        if(!sections.containsKey(termId) || !sections.get(termId).containsKey(sectionId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION, sectionId });
        }  
        if(null == sections.get(termId).get(sectionId).getSectionAssignments()) {
            sections.get(termId).get(sectionId).setSectionAssignments(new HashMap<Long, SectionAssignment>());
        } 
        sectionAssignment.setId(sectionAssignmentCounter.getAndIncrement());
        sectionAssignment.setSectionId(sectionId);
        sections.get(termId).get(sectionId).getSectionAssignments().put(sectionAssignment.getId(), sectionAssignment);
        return respond(new EntityId(sectionAssignment.getId()));
    }

    @ApiOperation(
            value = "Overwrite an existing section assignment", 
            notes = "Overwrites an existing section assignment with the ID provided",
            response = EntityId.class)
    @RequestMapping(
            value = "/{sectAssignmentId}",
            method = RequestMethod.PUT, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replaceSectionAssignment(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "School year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId,
            @ApiParam(name = "sectionId", required = true, value = "Section ID")
            @PathVariable(value="sectionId") Long sectionId,
            @ApiParam(name = "sectAssignmentId", required = true, value = "Section assignment ID")
            @PathVariable(value="sectAssignmentId") Long sectAssignmentId,
            @RequestBody @Valid SectionAssignment sectionAssignment) {
        if(null == schoolId || !schoolExists(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == schoolYearId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(schoolYearId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, schoolYearId });
        }
        if(null == schoolYears.get(schoolId).get(schoolYearId).findTermById(termId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ TERM, termId });
        }
        if(!sections.containsKey(termId) || !sections.get(termId).containsKey(sectionId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION, sectionId });
        } 
        if(null == sections.get(termId).get(sectionId).getSectionAssignments() || 
                !sections.get(termId).get(sectionId).getSectionAssignments().containsKey(sectAssignmentId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION_ASSIGNMENT, sectAssignmentId });
        }
        sectionAssignment.setId(sectAssignmentId);
        sectionAssignment.setSectionId(sectionId);
        sections.get(termId).get(sectionId).getSectionAssignments().put(sectAssignmentId, sectionAssignment);
        return respond(new EntityId(sectAssignmentId));
    }
    
    @ApiOperation(
            value = "Update an existing section assignment", 
            notes = "Updates an existing section assigmment. Will not overwrite existing values with null.",
            response = EntityId.class)
    @RequestMapping(
            value = "/{sectAssignmentId}", 
            method = RequestMethod.PATCH, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity updateTerm(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "School year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId,
            @ApiParam(name = "sectionId", required = true, value = "Section ID")
            @PathVariable(value="sectionId") Long sectionId,
            @ApiParam(name = "sectAssignmentId", required = true, value = "Section assignment ID")
            @PathVariable(value="sectAssignmentId") Long sectAssignmentId,
            @RequestBody @Valid SectionAssignment sectionAssignment) {
        if(null == schoolId || !schoolExists(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == schoolYearId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(schoolYearId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, schoolYearId });
        }
        if(null == schoolYears.get(schoolId).get(schoolYearId).findTermById(termId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ TERM, termId });
        }
        if(!sections.containsKey(termId) || !sections.get(termId).containsKey(sectionId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION, sectionId });
        }
        if(null == sections.get(termId).get(sectionId).getSectionAssignments() || 
                !sections.get(termId).get(sectionId).getSectionAssignments().containsKey(sectAssignmentId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION_ASSIGNMENT, sectAssignmentId });
        }
        sectionAssignment.setId(sectAssignmentId);
        sectionAssignment.setSectionId(sectionId);
        sectionAssignment.mergePropertiesIfNull(sections.get(termId).get(sectionId).getSectionAssignments().get(sectAssignmentId));
        sections.get(termId).get(sectionId).getSectionAssignments().put(sectAssignmentId, sectionAssignment);
        return respond(new EntityId(sectAssignmentId));
    }

    @ApiOperation(
            value = "Delete a section assignment", 
            notes = "Deletes the section assignment with the ID provided",
            response = Void.class)
    @RequestMapping(
            value = "/{sectAssignmentId}", 
            method = RequestMethod.DELETE, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteTerm(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "School year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId,
            @ApiParam(name = "sectionId", required = true, value = "Section ID")
            @PathVariable(value="sectionId") Long sectionId,
            @ApiParam(name = "sectAssignmentId", required = true, value = "Section assignment ID")
            @PathVariable(value="sectAssignmentId") Long sectAssignmentId) {
        if(null == schoolId || !schoolExists(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == schoolYearId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(schoolYearId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, schoolYearId });
        }
        if(null == schoolYears.get(schoolId).get(schoolYearId).findTermById(termId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ TERM, termId });
        }
        if(!sections.containsKey(termId) || !sections.get(termId).containsKey(sectionId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION, sectionId });
        }
        if(null == sections.get(termId).get(sectionId).getSectionAssignments() || 
                !sections.get(termId).get(sectionId).getSectionAssignments().containsKey(sectAssignmentId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION_ASSIGNMENT, sectAssignmentId });
        } 
        sections.get(termId).get(sectionId).getSectionAssignments().remove(sectAssignmentId);
        return respond((Section)null);
    }
}
