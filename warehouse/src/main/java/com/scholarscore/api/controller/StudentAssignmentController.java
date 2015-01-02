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

import com.scholarscore.api.persistence.PersistenceManager;
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
        if(null == schoolId || !PM.schoolExists(schoolId).equals(ErrorCodes.OK)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { PersistenceManager.SCHOOL, schoolId });
        }
        if(null == yrId || !PersistenceManager.schoolYears.containsKey(schoolId) || 
                !PersistenceManager.schoolYears.get(schoolId).containsKey(yrId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { PersistenceManager.SCHOOL_YEAR, yrId });
        }
        if(null == PersistenceManager.schoolYears.get(schoolId).get(yrId).findTermById(tId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.TERM, tId });
        }
        if(!PersistenceManager.sections.containsKey(tId) || 
                !PersistenceManager.sections.get(tId).containsKey(sId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.SECTION, sId });
        }
        if(null == PersistenceManager.sections.get(tId).get(sId).getSectionAssignments() || 
                null == PersistenceManager.sections.get(tId).get(sId).findAssignmentById(sAssignId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.SECTION_ASSIGNMENT, sAssignId });
        }
        Collection<StudentAssignment> returnSections = new ArrayList<>();
        if(null != PersistenceManager.studentAssignments.get(sAssignId)) {
            returnSections = PersistenceManager.studentAssignments.get(sAssignId).values();
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
        if(null == schoolId || !PM.schoolExists(schoolId).equals(ErrorCodes.OK)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { PersistenceManager.SCHOOL, schoolId });
        }
        if(null == yrId || !PersistenceManager.schoolYears.containsKey(schoolId) || !PersistenceManager.schoolYears.get(schoolId).containsKey(yrId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { PersistenceManager.SCHOOL_YEAR, yrId });
        }
        if(null == PersistenceManager.schoolYears.get(schoolId).get(yrId).findTermById(tId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.TERM, tId });
        }
        if(!PersistenceManager.sections.containsKey(tId) || !PersistenceManager.sections.get(tId).containsKey(sId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.SECTION, sId });
        }     
        if(null == PersistenceManager.sections.get(tId).get(sId).getSectionAssignments() || 
                null == PersistenceManager.sections.get(tId).get(sId).findAssignmentById(sAssignId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.SECTION_ASSIGNMENT, sAssignId });
        } 
        if(!PersistenceManager.studentAssignments.containsKey(sAssignId) || !PersistenceManager.studentAssignments.get(sAssignId).containsKey(studAssignId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.STUDENT_ASSIGNMENT, studAssignId });
        }
        return respond(PersistenceManager.studentAssignments.get(sAssignId).get(studAssignId));
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
        if(null == schoolId || !PM.schoolExists(schoolId).equals(ErrorCodes.OK)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { PersistenceManager.SCHOOL, schoolId });
        }
        if(null == yrId || !PersistenceManager.schoolYears.containsKey(schoolId) || !PersistenceManager.schoolYears.get(schoolId).containsKey(yrId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { PersistenceManager.SCHOOL_YEAR, yrId });
        }
        if(null == PersistenceManager.schoolYears.get(schoolId).get(yrId).findTermById(tId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.TERM, tId });
        }
        if(!PersistenceManager.sections.containsKey(tId) || !PersistenceManager.sections.get(tId).containsKey(sId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.SECTION, sId });
        }  
        if(null == PersistenceManager.sections.get(tId).get(sId).getSectionAssignments() || 
                null == PersistenceManager.sections.get(tId).get(sId).findAssignmentById(sAssignId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.SECTION_ASSIGNMENT, sAssignId });
        }
        if(null == PersistenceManager.sections.get(tId).get(sId).getEnrolledStudents() || 
                null == PersistenceManager.sections.get(tId).get(sId).findEnrolledStudentById(studentAssignment.getStudent().getId())) {
            return respond(ErrorCodes.ENTITY_INVALID_IN_CONTEXT, new Object[]{ PersistenceManager.STUDENT, studentAssignment.getStudent().getId(), PersistenceManager.SECTION, sId });
        }
        if(null == PersistenceManager.studentAssignments.get(sAssignId)) {
            PersistenceManager.studentAssignments.put(sAssignId, new HashMap<Long, StudentAssignment>());
        } 
        studentAssignment.setId(PersistenceManager.studentAssignmentCounter.getAndIncrement());
        PersistenceManager.studentAssignments.get(sAssignId).put(studentAssignment.getId(), studentAssignment);
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
        if(null == schoolId || !PM.schoolExists(schoolId).equals(ErrorCodes.OK)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { PersistenceManager.SCHOOL, schoolId });
        }
        if(null == yrId || !PersistenceManager.schoolYears.containsKey(schoolId) || !PersistenceManager.schoolYears.get(schoolId).containsKey(yrId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { PersistenceManager.SCHOOL_YEAR, yrId });
        }
        if(null == PersistenceManager.schoolYears.get(schoolId).get(yrId).findTermById(tId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.TERM, tId });
        }
        if(!PersistenceManager.sections.containsKey(tId) || !PersistenceManager.sections.get(tId).containsKey(sId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.SECTION, sId });
        } 
        if(null == PersistenceManager.sections.get(tId).get(sId).getSectionAssignments() || 
                null == PersistenceManager.sections.get(tId).get(sId).findAssignmentById(sAssignId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.SECTION_ASSIGNMENT, studAssignId });
        }
        if(!PersistenceManager.studentAssignments.containsKey(sAssignId) || !PersistenceManager.studentAssignments.get(sAssignId).containsKey(studAssignId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.STUDENT_ASSIGNMENT, studAssignId });
        }
        studentAssignment.setId(studAssignId);
        PersistenceManager.studentAssignments.get(sAssignId).put(studAssignId, studentAssignment);
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
        if(null == schoolId || !PM.schoolExists(schoolId).equals(ErrorCodes.OK)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { PersistenceManager.SCHOOL, schoolId });
        }
        if(null == yrId || !PersistenceManager.schoolYears.containsKey(schoolId) || !PersistenceManager.schoolYears.get(schoolId).containsKey(yrId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { PersistenceManager.SCHOOL_YEAR, yrId });
        }
        if(null == PersistenceManager.schoolYears.get(schoolId).get(yrId).findTermById(tId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.TERM, tId });
        }
        if(!PersistenceManager.sections.containsKey(tId) || !PersistenceManager.sections.get(tId).containsKey(sId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.SECTION, sId });
        }
        if(null == PersistenceManager.sections.get(tId).get(sId).getSectionAssignments() || 
                null == PersistenceManager.sections.get(tId).get(sId).findAssignmentById(sAssignId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.SECTION_ASSIGNMENT, sAssignId });
        }
        if(!PersistenceManager.studentAssignments.containsKey(sAssignId) || !PersistenceManager.studentAssignments.get(sAssignId).containsKey(studAssignId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.STUDENT_ASSIGNMENT, studAssignId });
        }
        studentAssignment.setId(studAssignId);
        studentAssignment.mergePropertiesIfNull(PersistenceManager.studentAssignments.get(sAssignId).get(studAssignId));
        PersistenceManager.studentAssignments.get(sAssignId).put(studAssignId, studentAssignment);
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
        if(null == schoolId || !PM.schoolExists(schoolId).equals(ErrorCodes.OK)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { PersistenceManager.SCHOOL, schoolId });
        }
        if(null == yrId || !PersistenceManager.schoolYears.containsKey(schoolId) || !PersistenceManager.schoolYears.get(schoolId).containsKey(yrId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[] { PersistenceManager.SCHOOL_YEAR, yrId });
        }
        if(null == PersistenceManager.schoolYears.get(schoolId).get(yrId).findTermById(tId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.TERM, tId });
        }
        if(!PersistenceManager.sections.containsKey(tId) || !PersistenceManager.sections.get(tId).containsKey(sId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.SECTION, sId });
        }
        if(null == PersistenceManager.sections.get(tId).get(sId).getSectionAssignments() || 
                null == PersistenceManager.sections.get(tId).get(sId).findAssignmentById(sAssignId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.SECTION_ASSIGNMENT, sAssignId });
        } 
        if(!PersistenceManager.studentAssignments.containsKey(sAssignId) || !PersistenceManager.studentAssignments.get(sAssignId).containsKey(studAssignId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ PersistenceManager.STUDENT_ASSIGNMENT, studAssignId });
        }
        PersistenceManager.studentAssignments.get(sAssignId).remove(studAssignId);
        return respond((StudentAssignment)null);
    }
}
