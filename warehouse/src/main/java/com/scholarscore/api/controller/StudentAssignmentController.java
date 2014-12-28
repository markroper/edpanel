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
import com.scholarscore.models.StudentAssignment;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/api/v1/schools/{schoolId}/years/{yrId}/terms/{tId}/sections/{sId}/sectassignments/{sAssignId}/studentassignments")
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
            @ApiParam(name = "sAssignId", required = true, value = "Section assignment ID")
            @PathVariable(value="sAssignId") Long sAssignId) {
        if(null == schoolId || !schoolExists(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == yrId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(yrId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, yrId });
        }
        if(null == schoolYears.get(schoolId).get(yrId).findTermById(tId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ TERM, tId });
        }
        if(!sections.containsKey(tId) || !sections.get(tId).containsKey(sId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION, sId });
        }
        if(null == sections.get(tId).get(sId).getSectionAssignments() || 
                !sections.get(tId).get(sId).getSectionAssignments().containsKey(sAssignId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION_ASSIGNMENT, sAssignId });
        }
        Collection<StudentAssignment> returnSections = new ArrayList<>();
        if(null != studentAssignments.get(sAssignId)) {
            returnSections = studentAssignments.get(sAssignId).values();
        }
        return respond(returnSections);
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
            @ApiParam(name = "sAssignId", required = true, value = "Section assignment ID")
            @PathVariable(value="sAssignId") Long sAssignId,
            @ApiParam(name = "studAssignId", required = true, value = "Student assignment ID")
            @PathVariable(value="studAssignId") Long studAssignId) {
        if(null == schoolId || !schoolExists(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == yrId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(yrId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, yrId });
        }
        if(null == schoolYears.get(schoolId).get(yrId).findTermById(tId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ TERM, tId });
        }
        if(!sections.containsKey(tId) || !sections.get(tId).containsKey(sId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION, sId });
        }     
        if(null == sections.get(tId).get(sId).getSectionAssignments() || 
                !sections.get(tId).get(sId).getSectionAssignments().containsKey(sAssignId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION_ASSIGNMENT, sAssignId });
        } 
        if(!studentAssignments.containsKey(sAssignId) || !studentAssignments.get(sAssignId).containsKey(studAssignId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT_ASSIGNMENT, studAssignId });
        }
        return respond(studentAssignments.get(sAssignId).get(studAssignId));
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
            @ApiParam(name = "sAssignId", required = true, value = "Section assignment ID")
            @PathVariable(value="sAssignId") Long sAssignId,
            @RequestBody @Valid StudentAssignment studentAssignment) {
        if(null == schoolId || !schoolExists(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == yrId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(yrId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, yrId });
        }
        if(null == schoolYears.get(schoolId).get(yrId).findTermById(tId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ TERM, tId });
        }
        if(!sections.containsKey(tId) || !sections.get(tId).containsKey(sId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION, sId });
        }  
        if(null == sections.get(tId).get(sId).getSectionAssignments() || 
                !sections.get(tId).get(sId).getSectionAssignments().containsKey(sAssignId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION_ASSIGNMENT, sAssignId });
        }
        if(null == sections.get(tId).get(sId).getEnrolledStudents() || 
                !sections.get(tId).get(sId).getEnrolledStudents().contains(studentAssignment.getStudentId())) {
            return respond(ErrorCodes.ENTITY_INVALID_IN_CONTEXT, new Object[]{ STUDENT, studentAssignment.getStudentId(), SECTION, sId });
        }
        if(null == studentAssignments.get(sAssignId)) {
            studentAssignments.put(sAssignId, new HashMap<Long, StudentAssignment>());
        } 
        studentAssignment.setId(studentAssignmentCounter.getAndIncrement());
        studentAssignment.setSectionAssignmentId(sAssignId);
        studentAssignments.get(sAssignId).put(studentAssignment.getId(), studentAssignment);
        return respond(new EntityId(studentAssignment.getId()));
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
            @ApiParam(name = "sAssignId", required = true, value = "Section assignment ID")
            @PathVariable(value="sAssignId") Long sAssignId,
            @ApiParam(name = "studAssignId", required = true, value = "Section assignment ID")
            @PathVariable(value="studAssignId") Long studAssignId,
            @RequestBody @Valid StudentAssignment studentAssignment) {
        if(null == schoolId || !schoolExists(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == yrId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(yrId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, yrId });
        }
        if(null == schoolYears.get(schoolId).get(yrId).findTermById(tId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ TERM, tId });
        }
        if(!sections.containsKey(tId) || !sections.get(tId).containsKey(sId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION, sId });
        } 
        if(null == sections.get(tId).get(sId).getSectionAssignments() || 
                !sections.get(tId).get(sId).getSectionAssignments().containsKey(sAssignId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION_ASSIGNMENT, studAssignId });
        }
        if(!studentAssignments.containsKey(sAssignId) || !studentAssignments.get(sAssignId).containsKey(studAssignId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT_ASSIGNMENT, studAssignId });
        }
        studentAssignment.setId(studAssignId);
        studentAssignment.setSectionAssignmentId(sAssignId);
        studentAssignments.get(sAssignId).put(studAssignId, studentAssignment);
        return respond(new EntityId(studAssignId));
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
            @ApiParam(name = "sAssignId", required = true, value = "Section assignment ID")
            @PathVariable(value="sAssignId") Long sAssignId,
            @ApiParam(name = "studAssignId", required = true, value = "Section assignment ID")
            @PathVariable(value="studAssignId") Long studAssignId,
            @RequestBody @Valid StudentAssignment studentAssignment) {
        if(null == schoolId || !schoolExists(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == yrId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(yrId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, yrId });
        }
        if(null == schoolYears.get(schoolId).get(yrId).findTermById(tId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ TERM, tId });
        }
        if(!sections.containsKey(tId) || !sections.get(tId).containsKey(sId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION, sId });
        }
        if(null == sections.get(tId).get(sId).getSectionAssignments() || 
                !sections.get(tId).get(sId).getSectionAssignments().containsKey(sAssignId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION_ASSIGNMENT, sAssignId });
        }
        if(!studentAssignments.containsKey(sAssignId) || !studentAssignments.get(sAssignId).containsKey(studAssignId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT_ASSIGNMENT, studAssignId });
        }
        studentAssignment.setId(studAssignId);
        studentAssignment.setSectionAssignmentId(sAssignId);
        studentAssignment.mergePropertiesIfNull(studentAssignments.get(sAssignId).get(studAssignId));
        studentAssignments.get(sAssignId).put(studAssignId, studentAssignment);
        return respond(new EntityId(studAssignId));
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
            @ApiParam(name = "sAssignId", required = true, value = "Section assignment ID")
            @PathVariable(value="sAssignId") Long sAssignId,
            @ApiParam(name = "studAssignId", required = true, value = "Section assignment ID")
            @PathVariable(value="studAssignId") Long studAssignId) {
        if(null == schoolId || !schoolExists(schoolId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL, schoolId });
        }
        if(null == yrId || !schoolYears.containsKey(schoolId) || !schoolYears.get(schoolId).containsKey(yrId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { SCHOOL_YEAR, yrId });
        }
        if(null == schoolYears.get(schoolId).get(yrId).findTermById(tId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ TERM, tId });
        }
        if(!sections.containsKey(tId) || !sections.get(tId).containsKey(sId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION, sId });
        }
        if(null == sections.get(tId).get(sId).getSectionAssignments() || 
                !sections.get(tId).get(sId).getSectionAssignments().containsKey(sAssignId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ SECTION_ASSIGNMENT, sAssignId });
        } 
        if(!studentAssignments.containsKey(sAssignId) || !studentAssignments.get(sAssignId).containsKey(studAssignId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT_ASSIGNMENT, studAssignId });
        }
        studentAssignments.get(sAssignId).remove(studAssignId);
        return respond((StudentAssignment)null);
    }
}
