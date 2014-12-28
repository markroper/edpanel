package com.scholarscore.api.controller;

import java.util.Collection;
import java.util.HashMap;
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
@RequestMapping("/api/v1/schools/{schoolId}/years/{yearId}/terms/{termId}/sections/{sectId}/students/{studId}/grades")
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
            @PathVariable(value="sectId") Long sectId,
            @ApiParam(name = "studId", required = true, value = "Student ID")
            @PathVariable(value="studId") Long studId) {
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
        if(!students.containsKey(studId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT, studId });
        }
        Collection<StudentSectionGrade> returnGrades = null;
        if(studentSectionGrades.containsKey(studId) && studentSectionGrades.get(studId).containsKey(sectId)) {
            returnGrades = studentSectionGrades.get(studId).get(sectId).values();
        }
        return respond(returnGrades);
    }
    
    @ApiOperation(
            value = "Get a student's grade in a section", 
            notes = "Get a student's grade in a section", 
            response = StudentSectionGrade.class)
    @RequestMapping(
            value = "/{gradeId}", 
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
            @PathVariable(value="studId") Long studId,
            @ApiParam(name = "gradeId", required = true, value = "Grade ID")
            @PathVariable(value="gradeId") Long gradeId) {
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
        if(!students.containsKey(studId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT, studId });
        }
        if(!studentSectionGrades.containsKey(studId) || !studentSectionGrades.get(studId).containsKey(sectId) ||
                null != studentSectionGrades.get(studId).get(sectId).get(gradeId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT_SECTION_GRADE, gradeId });
        }
        return respond(studentSectionGrades.get(studId).get(sectId).get(gradeId));
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
            @ApiParam(name = "studId", required = true, value = "Student ID")
            @PathVariable(value="studId") Long studId,
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
        if(!students.containsKey(studId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT, studId });
        }
        if(null == sections.get(termId).get(sectId).getEnrolledStudents() ||
                !sections.get(termId).get(sectId).getEnrolledStudents().containsKey(studId)) {
            return respond(ErrorCodes.ENTITY_INVALID_IN_CONTEXT, new Object[]{ 
                    STUDENT_SECTION_GRADE, studId, SECTION, studId 
                    });
        }
        if(null == studentSectionGrades.get(studId)) {
            studentSectionGrades.put(studId, new HashMap<Long, Map<Long, StudentSectionGrade>>());
        } 
        if(null == studentSectionGrades.get(studId).get(sectId)) {
            studentSectionGrades.get(studId).put(sectId, new HashMap<Long, StudentSectionGrade>());
        }
        studentSectionGrade.setId(studentSectGradeCounter.incrementAndGet());
        studentSectionGrades.get(studId).get(sectId).put(studentSectionGrade.getId(), studentSectionGrade);
        return respond(new EntityId(studentSectionGrade.getId()));
    }

    @ApiOperation(
            value = "Overwrite an existing student section grade", 
            notes = "Overwrites an existing student section grade",
            response = EntityId.class)
    @RequestMapping(
            value = "/{gradeId}",
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
            @ApiParam(name = "gradeId", required = true, value = "Grade ID")
            @PathVariable(value="gradeId") Long gradeId,
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
        if(!students.containsKey(studId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT, studId });
        }
        if(!studentSectionGrades.containsKey(studId) || !studentSectionGrades.get(studId).containsKey(sectId) ||
                !studentSectionGrades.get(studId).get(sectId).containsKey(gradeId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT_SECTION_GRADE, gradeId });
        }
        studentSectionGrade.setId(gradeId);
        studentSectionGrades.get(studId).get(sectId).put(gradeId, studentSectionGrade);
        return respond(new EntityId(gradeId));
    }
    
    @ApiOperation(
            value = "Update an existing student section grade", 
            notes = "Updates an existing student section grade. Will not overwrite existing values with null.",
            response = EntityId.class)
    @RequestMapping(
            value = "/{gradeId}", 
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
            @ApiParam(name = "gradeId", required = true, value = "Grade ID")
            @PathVariable(value="gradeId") Long gradeId,
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
        if(!students.containsKey(studId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT, studId });
        }
        if(!studentSectionGrades.containsKey(studId) || !studentSectionGrades.get(studId).containsKey(sectId) ||
                !studentSectionGrades.get(studId).get(sectId).containsKey(gradeId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT_SECTION_GRADE, gradeId });
        }
        
        studentSectionGrade.setId(gradeId);
        studentSectionGrade.mergePropertiesIfNull(studentSectionGrades.get(studId).get(sectId).get(gradeId));
        studentSectionGrades.get(studId).get(sectId).put(gradeId, studentSectionGrade);
        return respond(new EntityId(gradeId));
    }

    @ApiOperation(
            value = "Delete a student section grade", 
            notes = "Deletes the student section grade with the ID provided",
            response = Void.class)
    @RequestMapping(
            value = "/{gradeId}", 
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
            @PathVariable(value="studId") Long studId,
            @ApiParam(name = "gradeId", required = true, value = "Grade ID")
            @PathVariable(value="gradeId") Long gradeId) {
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
        if(!students.containsKey(studId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT, studId });
        }
        if(!studentSectionGrades.containsKey(studId) || !studentSectionGrades.get(studId).containsKey(sectId) ||
                !studentSectionGrades.get(studId).get(sectId).containsKey(gradeId)) {
            return respond(ErrorCodes.MODEL_NOT_FOUND, new Object[]{ STUDENT_SECTION_GRADE, gradeId });
        }
        studentSectionGrades.get(studId).get(sectId).remove(gradeId);
        return respond((StudentSectionGrade) null);
    }
}
