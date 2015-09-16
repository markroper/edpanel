package com.scholarscore.api.controller;

import java.util.List;

import javax.validation.Valid;

import com.scholarscore.api.persistence.SectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scholarscore.api.ApiConsts;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.Section;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/schools/{schoolId}/years/{schoolYearId}/terms/{termId}")
public class SectionController extends BaseController {

    @Autowired
    SectionManager sectionManager;

    @ApiOperation(
            value = "Get all sections", 
            notes = "Retrieve all sections in a term, school year, and school", 
            response = List.class)
    @RequestMapping(
            value = "/sections",
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
        return respond(sectionManager.getAllSections(schoolId, schoolYearId, termId));
    }
    
    @ApiOperation(
            value = "Get all sections taught by teacher", 
            notes = "Retrieve all sections in a term taught by a given teacher", 
            response = List.class)
    @RequestMapping(
            value = "/teachers/{teacherId}/sections",
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAllSectionsTaughtByTeacher(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "School year long ID")
            @PathVariable(value="schoolYearId") Long schoolYearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId,
            @ApiParam(name = "teacherId", required = true, value = "Teacher ID")
            @PathVariable(value="teacherId") Long teacherId) {
        return respond(getSectionManager().getAllSectionsByTeacher(schoolId, schoolYearId, termId, teacherId));
    }
    
    @ApiOperation(
            value = "Get a section", 
            notes = "Given a section ID, return the section instance", 
            response = Section.class)
    @RequestMapping(
            value = "/sections/{sectionId}", 
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
        return respond(sectionManager.getSection(schoolId, schoolYearId, termId, sectionId));
    }

    @ApiOperation(
            value = "Create a section", 
            notes = "Creates, assigns and ID to, persists and returns a section ID",
            response = EntityId.class)
    @RequestMapping(
            value = "/sections",
            method = RequestMethod.POST, 
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity createSection(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "School year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId,
            @RequestBody @Valid Section section) throws JsonProcessingException {
        return respond(sectionManager.createSection(schoolId, schoolYearId, termId, section));
    }

    @ApiOperation(
            value = "Overwrite an existing section", 
            notes = "Overwrites an existing section with the ID provided",
            response = EntityId.class)
    @RequestMapping(
            value = "/sections/{sectionId}",
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
            @RequestBody @Valid Section section) throws JsonProcessingException {
        return respond(sectionManager.replaceSection(schoolId, schoolYearId, termId, sectionId, section));
    }
    
    @ApiOperation(
            value = "Update an existing section", 
            notes = "Updates an existing section. Will not overwrite existing values with null.",
            response = EntityId.class)
    @RequestMapping(
            value = "/sections/{sectionId}", 
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
            @RequestBody @Valid Section section) throws JsonProcessingException {
        return respond(sectionManager.updateSection(schoolId, schoolYearId, termId, sectionId, section));
    }

    @ApiOperation(
            value = "Delete a section", 
            notes = "Deletes the section with the ID provided",
            response = Void.class)
    @RequestMapping(
            value = "/sections/{sectionId}", 
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
        return respond(sectionManager.deleteSection(schoolId, schoolYearId, termId, sectionId));
    }
}
