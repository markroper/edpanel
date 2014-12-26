package com.scholarscore.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.scholarscore.models.StudentSectionGrade;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/api/v1/schools/{schoolId}/years/{yearId}/terms/{termId}/sections/{sectId}/studentgrades")
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
        
        Iterator<Map<Long, StudentSectionGrade>> it = studentSectionGrades.values().iterator();
        ArrayList<StudentSectionGrade> stSectionGrades = new ArrayList<>();
        while(it.hasNext()) {
            Map<Long, StudentSectionGrade> sectionGrade = it.next();
            if(sectionGrade.keySet().contains(sectId)) {
                stSectionGrades.add(sectionGrade.get(sectId));
            }
        }
        return respond(stSectionGrades);
    }
    
    @ApiOperation(
            value = "Get a student's grade in an section", 
            notes = "Get a student's grade in an section", 
            response = StudentSectionGrade.class)
    @RequestMapping(
            value = "/{studentId}", 
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
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId) {
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
        if(!studentSectionGrades.containsKey(studentId) || !studentSectionGrades.get(studentId).containsKey(sectId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT_SECTION_GRADE, studentId });
        }
        return respond(studentSectionGrades.get(studentId).get(sectId));
    }

    @ApiOperation(
            value = "Create a student grade in a section", 
            notes = "Creates, assigns and ID to, persists and returns a student section grade",
            response = EntityId.class)
    @RequestMapping(
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
            @RequestBody @Valid StudentSectionGrade studentSectionGrade) {
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
        if(!students.containsKey(studentSectionGrade.getStudentId())) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT, studentSectionGrade.getStudentId() });
        }
        if(null == sections.get(termId).get(sectId).getEnrolledStudents() ||
                !sections.get(termId).get(sectId).getEnrolledStudents().contains(studentSectionGrade.getStudentId())) {
            return respond(ErrorCodes.ENTITY_INVALID_IN_CONTEXT, new Object[]{ 
                    STUDENT_SECTION_GRADE, studentSectionGrade.getStudentId(), SECTION, studentSectionGrade.getSectionId() 
                    });
        }
        if(null == studentSectionGrades.get(studentSectionGrade.getStudentId())) {
            studentSectionGrades.put(studentSectionGrade.getStudentId(), new HashMap<Long, StudentSectionGrade>());
        } 
        //TODO: check for the student with id studentId
        studentSectionGrades.get(studentSectionGrade.getStudentId()).put(studentSectionGrade.getSectionId(), studentSectionGrade);
        return respond(new EntityId(studentSectionGrade.getStudentId()));
    }

    @ApiOperation(
            value = "Overwrite an existing student section grade", 
            notes = "Overwrites an existing student section grade",
            response = EntityId.class)
    @RequestMapping(
            value = "/{studentId}",
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
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @RequestBody @Valid StudentSectionGrade studentSectionGrade) {
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
        if(!students.containsKey(studentId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT, studentId });
        }
        if(null == studentSectionGrades.get(studentId) || 
                !studentSectionGrades.get(studentId).containsKey(sectId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT_SECTION_GRADE, studentId });
        } 
        studentSectionGrade.setStudentId(studentId);
        studentSectionGrade.setSectionId(sectId);
        studentSectionGrades.get(studentId).put(sectId, studentSectionGrade);
        return respond(new EntityId(studentId));
    }
    
    @ApiOperation(
            value = "Update an existing student section grade", 
            notes = "Updates an existing student section grade. Will not overwrite existing values with null.",
            response = EntityId.class)
    @RequestMapping(
            value = "/{studentId}", 
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
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @RequestBody @Valid StudentSectionGrade studentSectionGrade) {
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
        if(!students.containsKey(studentId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT, studentId });
        }
        if(null == studentSectionGrades.get(studentId) || 
                !studentSectionGrades.get(studentId).containsKey(sectId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT_SECTION_GRADE, studentId });
        } 
        studentSectionGrade.setStudentId(studentId);
        studentSectionGrade.setSectionId(sectId);
        studentSectionGrade.mergePropertiesIfNull(studentSectionGrades.get(studentId).get(sectId));
        studentSectionGrades.get(studentId).put(sectId, studentSectionGrade);
        return respond(new EntityId(studentId));
    }

    @ApiOperation(
            value = "Delete a student section grade", 
            notes = "Deletes the student section grade with the ID provided",
            response = Void.class)
    @RequestMapping(
            value = "/{studentId}", 
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
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId) {
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
        if(!students.containsKey(studentId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT, studentId });
        }
        if(null == studentSectionGrades.get(studentId) || 
                !studentSectionGrades.get(studentId).containsKey(sectId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT_SECTION_GRADE, studentId });
        } 
        studentSectionGrades.get(studentId).remove(sectId);
        return respond((StudentSectionGrade) null);
    }
}
