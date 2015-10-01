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
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.attendance.SchoolDay;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

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
}
