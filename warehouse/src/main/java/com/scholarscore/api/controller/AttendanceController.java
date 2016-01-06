package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.attendance.Attendance;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/schools/{schoolId}/students/{studentId}/attendance")
public class AttendanceController extends BaseController {
    @ApiOperation(
            value = "Get all student attendance entries", 
            notes = "Retrieve all student attendance entries within a school", 
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAll(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId) {
        return respond(pm.getAttendanceManager().getAllStudentAttendance(schoolId, studentId));
    }
    
    @ApiOperation(
            value = "Get all student attendance entries in a term", 
            notes = "Retrieve all student attendance entries within a term", 
            response = List.class)
    @RequestMapping(
            value = "/years/{schoolYearId}/terms/{termId}",
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAll(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "schoolYearId", required = true, value = "School Year ID")
            @PathVariable(value="schoolYearId") Long schoolYearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId) {
        return respond(pm.getAttendanceManager().getAllStudentAttendanceInTerm(schoolId, studentId, schoolYearId, termId));
    }

    @ApiOperation(
            value = "Get all student attendance entries in a given section",
            notes = "Retrieve all student attendance entries within a section",
            response = List.class)
    @RequestMapping(
            value = "/sections/{sectionId}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAll(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "sectionId", required = true, value = "Section ID")
            @PathVariable(value="sectionId") Long sectionId) {
        return respond(pm.getAttendanceManager().getAllStudentSectionAttendance(schoolId, studentId, sectionId));
    }
    
    @ApiOperation(
            value = "Get a student attendance object by ID", 
            notes = "Retrieve a student attendance object its ID", 
            response = Attendance.class)
    @RequestMapping(
            value = "/{attendanceId}", 
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getASchoolDay(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "attendanceId", required = true, value = "Attendance ID")
            @PathVariable(value="attendanceId") Long attendanceId) {
        return respond(pm.getAttendanceManager().getStudentAttendance(schoolId, studentId, attendanceId));
    }
    
    @ApiOperation(
            value = "Create an attendance entry for a student", 
            notes = "Creates, assigns an ID, persists and returns a an attendance ID",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST, 
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity createAttendance(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @RequestBody @Valid Attendance attendance) {
        return respond(pm.getAttendanceManager().createAttendance(schoolId, studentId, attendance));
    }

    @ApiOperation(
            value = "Create attendance entries for a single student",
            notes = "Creates, assigns an ID, and persists student attendance entries",
            response = Void.class)
    @RequestMapping(
            value = "/bulk",
            method = RequestMethod.POST,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity createAttendances(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @RequestBody @Valid List<Attendance> attendances) {
        return respond(pm.getAttendanceManager().createAttendances(schoolId, studentId, attendances));
    }
    
    @ApiOperation(
            value = "Delete an attendance entry", 
            response = Void.class)
    @RequestMapping(
            value = "/{attendanceId}", 
            method = RequestMethod.DELETE, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteAttendance(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "attendanceId", required = true, value = "Attendance ID")
            @PathVariable(value="attendanceId") Long attendanceId) {
        return respond(pm.getAttendanceManager().deleteAttendance(schoolId, studentId, attendanceId));
    }

    @ApiOperation(
            value = "Replace an attendance entry",
            response = Void.class)
    @RequestMapping(
            value = "/{attendanceId}",
            method = RequestMethod.PUT,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replaceAttendance(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "studentId", required = true, value = "Student ID")
            @PathVariable(value="studentId") Long studentId,
            @ApiParam(name = "attendanceId", required = true, value = "Attendance ID")
            @PathVariable(value="attendanceId") Long attendanceId,
            @RequestBody @Valid Attendance attendance) {
        return respond(pm.getAttendanceManager().replaceAttendance(schoolId, studentId, attendanceId, attendance));
    }
}
