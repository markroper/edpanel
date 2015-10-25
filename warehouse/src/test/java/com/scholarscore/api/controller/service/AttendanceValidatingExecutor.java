package com.scholarscore.api.controller.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.attendance.AttendanceList;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import java.util.List;

public class AttendanceValidatingExecutor {
    private final IntegrationBase serviceBase;
    
    public AttendanceValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }
    
    public List<Attendance> getAllInTerm(Long schoolId, Long studentId, Long schoolyearId, Long termId, int numItems, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getAttendanceEndpoint(schoolId, studentId) + "/years/" + schoolyearId + "/terms/" + termId, 
                null);
        AttendanceList days = serviceBase.validateResponse(response, new TypeReference<AttendanceList>(){});
        Assert.assertNotNull(days, "Unexpected null list of days returned for case: " + msg);
        Assert.assertEquals(days.size(), numItems, "Unexpected number of results returned for case: " + msg);
        return days;
    }
    
    public List<Attendance> getAll(Long schoolId, Long studentId, int numItems, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getAttendanceEndpoint(schoolId, studentId), 
                null);
        AttendanceList days = serviceBase.validateResponse(response, new TypeReference<AttendanceList>(){});
        Assert.assertNotNull(days, "Unexpected null list of days returned for case: " + msg);
        Assert.assertEquals(days.size(), numItems, "Unexpected number of results returned for case: " + msg);
        return days;
    }
    
    public Attendance get(Long schoolId, Long studentId, Long attendanceId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getAttendanceEndpoint(schoolId, studentId, attendanceId), 
                null);
        Attendance attendance = serviceBase.validateResponse(response, new TypeReference<Attendance>(){});
        Assert.assertNotNull(attendance, "Unexpected null day for case: " + msg);
        return attendance;
    }
    
    public void getNegative(Long schoolId, Long studentId, Long attendanceId, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getAttendanceEndpoint(schoolId, studentId, attendanceId), 
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned while retrieving day: " + msg);
    }
    
    public Attendance create(Long schoolId, Long studentId, Attendance attendance, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.POST, 
                serviceBase.getAttendanceEndpoint(schoolId, studentId), 
                null, 
                attendance);
        EntityId dayId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(dayId, "unexpected null day ID returned from create call for case: " + msg);
        return retrieveAndValidateCreatedAttendance(schoolId, studentId, attendance, dayId, HttpMethod.POST, msg);
    }
    
    public void createNegative(Long schoolId, Long studentId, Attendance attendance, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.POST, 
                serviceBase.getAttendanceEndpoint(schoolId, studentId), 
                null, 
                attendance);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned for case: " + msg);
    }
    
    public void delete(Long schoolId, Long studentId, Attendance attendance, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.DELETE, 
                serviceBase.getAttendanceEndpoint(schoolId, studentId, attendance.getId()));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(schoolId, studentId, attendance.getId(), HttpStatus.NOT_FOUND, msg);
    }
    
    public void deleteNegative(
            Long schoolId, Long studentId, Attendance attendance, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getAttendanceEndpoint(schoolId, studentId, attendance.getId()));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }
    
    protected Attendance retrieveAndValidateCreatedAttendance(
            Long schoolId, Long studentId, Attendance submitted, EntityId id, HttpMethod method, String msg) {
        submitted.setId(id.getId());
        Attendance created = this.get(schoolId, studentId, id.getId(), msg);
        Assert.assertEquals(created, submitted, msg);
        return created;
    }
}
