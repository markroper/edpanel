package com.scholarscore.api.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
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
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/api/v1/schools/{schoolId}/years/{schoolYearId}/terms/{termId}/sections")
public class SectionController extends BaseController {
    @ApiOperation(
            value = "Get all sections", 
            notes = "Retrieve all sections in a term, school year, and school", 
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAllSections(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "School year long ID")
            @PathVariable(value="schoolYearId") Long schoolYearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId) {
        if(null == schoolId || !schoolExists(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == schoolYearId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(schoolYearId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, schoolYearId });
        }
        if(null == schoolYears.get(schoolId).get(schoolYearId).findTermById(termId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ TERM, termId });
        }
        
        Collection<Section> returnSections = new ArrayList<Section>();
        if(sections.containsKey(termId)) {
            returnSections = sections.get(termId).values();
        }
        return respond(returnSections);
    }
    
    @ApiOperation(
            value = "Get a section", 
            notes = "Given a section ID, return the section instance", 
            response = Section.class)
    @RequestMapping(
            value = "/{sectionId}", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getSection(
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
        return respond(sections.get(termId).get(sectionId));
    }

    @ApiOperation(
            value = "Create a section", 
            notes = "Creates, assigns and ID to, persists and returns a section ID",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST, 
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity createTerm(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "School year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId,
            @RequestBody @Valid Section section) {
        if(null == schoolId || !schoolExists(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == schoolYearId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(schoolYearId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, schoolYearId });
        }
        if(null == schoolYears.get(schoolId).get(schoolYearId).findTermById(termId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ TERM, termId });
        }
        if(!sections.containsKey(termId)) {
            sections.put(termId, new HashMap<Long, Section>());
        } 
        
        section.setId(sectionCounter.getAndIncrement());
        section.setTermId(termId);
        section.setYearId(schoolYearId);
        sections.get(termId).put(section.getId(), section);
        return respond(new EntityId(section.getId()));
    }

    @ApiOperation(
            value = "Overwrite an existing section", 
            notes = "Overwrites an existing section with the ID provided",
            response = EntityId.class)
    @RequestMapping(
            value = "/{sectionId}",
            method = RequestMethod.PUT, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replaceTerm(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "School year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId,
            @ApiParam(name = "sectionId", required = true, value = "Section ID")
            @PathVariable(value="sectionId") Long sectionId,
            @RequestBody @Valid Section section) {
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
        section.setId(sectionId);
        section.setTermId(termId);
        section.setYearId(schoolYearId);
        sections.get(termId).put(sectionId, section);
        return respond(new EntityId(sectionId));
    }
    
    @ApiOperation(
            value = "Update an existing section", 
            notes = "Updates an existing section. Will not overwrite existing values with null.",
            response = EntityId.class)
    @RequestMapping(
            value = "/{sectionId}", 
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
            @RequestBody @Valid Section section) {
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
        section.setId(sectionId);
        section.setTermId(termId);
        section.setYearId(schoolYearId);
        section.mergePropertiesIfNull(sections.get(termId).get(sectionId));
        sections.get(termId).put(sectionId, section);
        return respond(new EntityId(sectionId));
    }

    @ApiOperation(
            value = "Delete a section", 
            notes = "Deletes the section with the ID provided",
            response = Void.class)
    @RequestMapping(
            value = "/{sectionId}", 
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
        sections.get(termId).remove(sectionId);
        return respond((Section)null);
    }
}
