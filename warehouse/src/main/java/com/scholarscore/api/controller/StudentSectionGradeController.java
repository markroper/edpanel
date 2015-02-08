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

import com.scholarscore.api.ApiConsts;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.StudentSectionGrade;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/schools/{schoolId}/years/{yearId}/terms/{termId}/sections/{sectId}/grades")
public class StudentSectionGradeController extends BaseController {
    @ApiOperation(
            value = "Get all student grades in a section", 
            notes = "Retrieve all student grades in a section", 
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAllStudentSectionGrades(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yearId", required = true, value = "School year ID")
            @PathVariable(value="yearId") Long yearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId,
            @ApiParam(name = "sectId", required = true, value = "Section ID")
            @PathVariable(value="sectId") Long sectId) {
        return respond(getStudentSectionGradeManager().getAllStudentSectionGrades(schoolId, yearId, termId, sectId));
    }
    
    @ApiOperation(
            value = "Get a student grade in a section", 
            notes = "Get a student grade in a section", 
            response = StudentSectionGrade.class)
    @RequestMapping(
            value = "/students/{studId}", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getStudentSectionGrade(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yearId", required = true, value = "School year ID")
            @PathVariable(value="yearId") Long yearId,
            @ApiParam(name = "termId", required = true, value = "Term long ID")
            @PathVariable(value="termId") Long termId,
            @ApiParam(name = "sectId", required = true, value = "Section ID")
            @PathVariable(value="sectId") Long sectId,
            @ApiParam(name = "studId", required = true, value = "Student ID")
            @PathVariable(value="studId") Long studId) {
        return respond(getStudentSectionGradeManager().getStudentSectionGrade(schoolId, yearId, termId, sectId, studId));
    }

    @ApiOperation(
            value = "Create a student grade in a section", 
            notes = "Creates, assigns and ID to, persists and returns a student section grade",
            response = EntityId.class)
    @RequestMapping(
            value = "/students/{studId}", 
            method = RequestMethod.POST, 
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity createStudentSectionGrade(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yearId", required = true, value = "School year ID")
            @PathVariable(value="yearId") Long yearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId,
            @ApiParam(name = "sectId", required = true, value = "Section ID")
            @PathVariable(value="sectId") Long sectId,
            @ApiParam(name = "studId", required = true, value = "Student ID")
            @PathVariable(value="studId") Long studId,
            @RequestBody @Valid StudentSectionGrade studentSectionGrade) {
        return respond(getStudentSectionGradeManager().createStudentSectionGrade(schoolId, yearId, termId, sectId, studId, studentSectionGrade));
    }

    @ApiOperation(
            value = "Overwrite an existing student section grade", 
            notes = "Overwrites an existing student section grade",
            response = EntityId.class)
    @RequestMapping(
            value = "/students/{studId}",
            method = RequestMethod.PUT, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replaceStudentSectionGrade(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yearId", required = true, value = "School year ID")
            @PathVariable(value="yearId") Long yearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId,
            @ApiParam(name = "sectId", required = true, value = "Section ID")
            @PathVariable(value="sectId") Long sectId,
            @ApiParam(name = "studId", required = true, value = "Student ID")
            @PathVariable(value="studId") Long studId,
            @RequestBody @Valid StudentSectionGrade studentSectionGrade) {
        return respond(getStudentSectionGradeManager().replaceStudentSectionGrade(
                schoolId, yearId, termId, sectId, studId, studentSectionGrade));
    }
    
    @ApiOperation(
            value = "Update an existing student section grade", 
            notes = "Updates an existing student section grade. Will not overwrite existing values with null.",
            response = EntityId.class)
    @RequestMapping(
            value = "/students/{studId}", 
            method = RequestMethod.PATCH, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity updateGrade(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yearId", required = true, value = "School year ID")
            @PathVariable(value="yearId") Long yearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId,
            @ApiParam(name = "sectId", required = true, value = "Section ID")
            @PathVariable(value="sectId") Long sectId,
            @ApiParam(name = "studId", required = true, value = "Student ID")
            @PathVariable(value="studId") Long studId,
            @RequestBody @Valid StudentSectionGrade studentSectionGrade) {
        return respond(getStudentSectionGradeManager().updateStudentSectionGrade(
                schoolId, yearId, termId, sectId, studId, studentSectionGrade));
    }

    @ApiOperation(
            value = "Delete a student section grade", 
            notes = "Deletes the student section grade with the ID provided",
            response = Void.class)
    @RequestMapping(
            value = "/students/{studId}", 
            method = RequestMethod.DELETE, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteSectionGrade(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yearId", required = true, value = "School year ID")
            @PathVariable(value="yearId") Long yearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId,
            @ApiParam(name = "sectId", required = true, value = "Section ID")
            @PathVariable(value="sectId") Long sectId,
            @ApiParam(name = "studId", required = true, value = "Student ID")
            @PathVariable(value="studId") Long studId) {
        return respond(getStudentSectionGradeManager().deleteStudentSectionGrade(schoolId, yearId, termId, sectId, studId));
    }
}
