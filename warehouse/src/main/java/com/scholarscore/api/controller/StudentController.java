package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.WeightedGradable;
import com.scholarscore.models.user.Student;
import com.scholarscore.util.EdPanelDateUtil;
import com.scholarscore.util.GradeUtil;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/students")
public class StudentController extends BaseController {


    @ApiOperation(
            value = "Get all students", 
            notes = "Retrieve all students within a district", 
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAll(@RequestParam(required = false, value = "schoolId") Long schoolId) {
        return respond(pm.getStudentManager().getAllStudents(schoolId));
    }
    
    @ApiOperation(
            value = "Get a student by ID", 
            notes = "Given a student ID, the endpoint returns the student", 
            response = Student.class)
    @RequestMapping(
            value = "/{studentId}", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity get(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId) {
        return respond(pm.getStudentManager().getStudent(studentId));
    }

    @ApiOperation(
            value = "Get a student by source system ID",
            notes = "Given a student source system ID, the endpoint returns the student",
            response = Student.class)
    @RequestMapping(
            value = "/sourcesystemids/{ssid}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getBySsid(
            @ApiParam(name = "ssid", required = true, value = "Student SSID")
            @PathVariable(value="ssid") Long ssid) {
        return respond(pm.getStudentManager().getStudentBySourceSystemId(ssid));
    }

    @ApiOperation(
            value = "Create a student", 
            notes = "Creates, assigns an ID to, persists and returns a student",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST, 
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity create(@RequestBody @Valid Student student) {
        return respond(pm.getStudentManager().createStudent(student));
    }

    @ApiOperation(
            value = "Overwrite an existing student", 
            notes = "Overwrites an existing student entity within a district with the ID provided",
            response = EntityId.class)
    @RequestMapping(
            value = "/{studentId}", 
            method = RequestMethod.PUT, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replace(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @RequestBody @Valid Student student) {
        return respond(pm.getStudentManager().replaceStudent(studentId, student));
    }
    
    @ApiOperation(
            value = "Update an existing student", 
            notes = "Updates an existing student properties. Will not overwrite existing values with null.",
            response = EntityId.class)
    @RequestMapping(
            value = "/{studentId}", 
            method = RequestMethod.PATCH, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity update(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @RequestBody @Valid Student student) {
        return respond(pm.getStudentManager().updateStudent(studentId, student));
    }

    @ApiOperation(
            value = "Delete a student from a district by ID", 
            response = Void.class)
    @RequestMapping(
            value = "/{studentId}", 
            method = RequestMethod.DELETE, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity delete(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId) {
        return respond(pm.getStudentManager().deleteStudent(studentId));
    }

    @ApiOperation(
            value = "Get a student's GPA",
            notes = "Given a student ID, the endpoint returns the student's Grade Point Average on a specified scale",
            response = List.class)
    @RequestMapping(
            value = "/{studentId}/gpa/{gpaScale}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getGpa(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value = "studentId") Long studentId,
            @ApiParam(name = "gpaScale", required = true)
            @PathVariable(value="gpaScale") Integer gpaScale)
    {
        Collection<? extends WeightedGradable> courseGrades =
                pm.getStudentSectionGradeManager().getSectionGradesForStudent(studentId).getValue();
        return respond(GradeUtil.calculateGPA(gpaScale, courseGrades));
    }
    
    @ApiOperation(
            value = "Get all sections for a student", 
            notes = "Retrieve all sections for a student within a term, school year, and school", 
            response = List.class)
    @RequestMapping(
            value = "/{studentId}/schools/{schoolId}/years/{schoolYearId}/terms/{termId}/sections",
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAllSections(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "schoolYearId", required = true, value = "School year long ID")
            @PathVariable(value="schoolYearId") Long schoolYearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId) {
        return respond(pm.getSectionManager().getAllSections(studentId, schoolId, schoolYearId, termId));
    }
    
    @ApiOperation(
            value = "Get all student's assignments in one section", 
            notes = "Retrieve all student assignments within a section. If the section grade is not final, one will be calculated", 
            response = List.class)
    @RequestMapping(
            value = "/{studentId}/schools/{schoolId}/years/{yrId}/terms/{tId}/sections/{sId}/studentassignments",
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAllSectionAssignments(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yrId", required = true, value = "School year ID")
            @PathVariable(value="yrId") Long yrId,
            @ApiParam(name = "tId", required = true, value = "Term ID")
            @PathVariable(value="tId") Long tId,
            @ApiParam(name = "sId", required = true, value = "Section ID")
            @PathVariable(value="sId") Long sId) {
        return respond(pm.getStudentAssignmentManager().getOneSectionOneStudentsAssignments(studentId, schoolId, yrId, tId, sId));
    }

    @ApiOperation(
            value = "Get one student's prep scores",
            notes = "Prep score is a measure of a student's weekly performance. It is initially 90 and is adjusted by all positive and negative behavioral events.",
            response = List.class)
    @RequestMapping(
            value = "/{studentId}/prepscores",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getStudentPrepScore(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId, 
            @ApiParam(name = "startDate", required = true, value = "Start Date")
            @RequestParam(value="startDate") @DateTimeFormat(pattern = EdPanelDateUtil.EDPANEL_DATE_FORMAT) Date startDate,
            @ApiParam(name = "endDate", required = true, value = "End Date")
            @RequestParam(value="endDate") @DateTimeFormat(pattern = EdPanelDateUtil.EDPANEL_DATE_FORMAT) Date endDate
    ) {
        return respond(pm.getStudentManager().getStudentPrepScore(new Long[] { studentId }, startDate, endDate));
    }
    
    @ApiOperation(
            value = "Get one or more of a student's prep scores",
            notes = "Prep score is a measure of a student's weekly performance. It is initially 90 and is adjusted by all positive and negative behavioral events.",
            response = List.class)
    @RequestMapping(
            value = "/prepscores",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getStudentPrepScores(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @RequestParam(value="studentId") Long[] studentIds,
            @ApiParam(name = "startDate", required = true, value = "Start Date")
            @RequestParam(value="startDate") @DateTimeFormat(pattern = EdPanelDateUtil.EDPANEL_DATE_FORMAT) Date startDate,
            @ApiParam(name = "endDate", required = true, value = "End Date")
            @RequestParam(value="endDate") @DateTimeFormat(pattern = EdPanelDateUtil.EDPANEL_DATE_FORMAT) Date endDate
    ) {
        return respond(pm.getStudentManager().getStudentPrepScore(studentIds, startDate, endDate));
    }

}
