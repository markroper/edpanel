package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.annotation.StudentAccessible;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.user.Student;
import com.scholarscore.util.EdPanelDateUtil;
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
import java.time.LocalDate;
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
    @StudentAccessible(paramName = "studentId")
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
    //TODO: filter me for student accessibility
    @StudentAccessible
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
            value = "Get all sections for a student", 
            notes = "Retrieve all sections for a student within a term, school year, and school", 
            response = List.class)
    @RequestMapping(
            value = "/{studentId}/schools/{schoolId}/years/{schoolYearId}/terms/{termId}/sections",
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible(paramName = "studentId")
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
    @StudentAccessible(paramName = "studentId")
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
        return respond(pm.getStudentAssignmentManager().getOneSectionOneStudentsAssignments(studentId, sId));
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
    @StudentAccessible(paramName = "studentId")
    public @ResponseBody ResponseEntity getStudentPrepScore(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId, 
            @ApiParam(name = "startDate", value = "Start Date")
            @RequestParam(value="startDate", required = false) @DateTimeFormat(pattern = EdPanelDateUtil.EDPANEL_DATE_FORMAT) Date startDate,
            @ApiParam(name = "endDate", value = "End Date")
            @RequestParam(value="endDate", required = false) @DateTimeFormat(pattern = EdPanelDateUtil.EDPANEL_DATE_FORMAT) Date endDate
    ) {
        return respond(pm.getStudentManager().getStudentPrepScore(new Long[] { studentId }, startDate, endDate));
    }

    @ApiOperation(
            value = "Get one student's weekly HW completion percentage",
            notes = "Bucketed by week, Sunday - Saturday",
            response = List.class)
    @RequestMapping(
            value = "/{studentId}/homeworkrates",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible(paramName = "studentId")
    public @ResponseBody ResponseEntity getStudentHwCompletionRates(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "startDate", value = "Start Date")
            @RequestParam(value="startDate", required = false) @DateTimeFormat(pattern = EdPanelDateUtil.EDPANEL_DATE_FORMAT) LocalDate startDate,
            @ApiParam(name = "endDate", value = "End Date")
            @RequestParam(value="endDate", required = false) @DateTimeFormat(pattern = EdPanelDateUtil.EDPANEL_DATE_FORMAT) LocalDate endDate
    ) {
        return respond(pm.getStudentManager().getStudentHomeworkRates(studentId, startDate, endDate));
    }

    @ApiOperation(
            value = "Get one student's weekly HW completion percentage for a particular section",
            notes = "Bucketed by week, Sunday - Saturday",
            response = List.class)
    @RequestMapping(
            value = "/{studentId}/homeworkrates/sections/{sectionId}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible(paramName = "studentId")
    public @ResponseBody ResponseEntity getStudentHwCompletionRatesBySection(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "sectionId", required = true, value = "Section ID")
            @PathVariable(value="sectionId") Long sectionId
    ) {
        return respond(pm.getStudentManager().getStudentHomeworkRatesPerSection(
                studentId,sectionId));
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
    @StudentAccessible(paramName = "studentId")
    public @ResponseBody ResponseEntity getStudentPrepScores(
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @RequestParam(value="studentId") Long[] studentIds,
            @ApiParam(name = "startDate", value = "Start Date")
            @RequestParam(value="startDate", required = false) @DateTimeFormat(pattern = EdPanelDateUtil.EDPANEL_DATE_FORMAT) Date startDate,
            @ApiParam(name = "endDate", value = "End Date")
            @RequestParam(value="endDate", required = false) @DateTimeFormat(pattern = EdPanelDateUtil.EDPANEL_DATE_FORMAT) Date endDate
    ) {
        return respond(pm.getStudentManager().getStudentPrepScore(studentIds, startDate, endDate));
    }

}
