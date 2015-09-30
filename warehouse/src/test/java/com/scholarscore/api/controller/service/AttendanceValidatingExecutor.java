package com.scholarscore.api.controller.service;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.attendance.Attendance;

public class AttendanceValidatingExecutor {
    private final IntegrationBase serviceBase;
    
    public AttendanceValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }
    
    public List<Attendance> getAllInTerm(Long schoolId, Long studentId, Long termId, String msg) {
        return null;
    }
    
    public Attendance get(Long schoolId, Long studentId, Long attendanceId, String msg) {
        return null;
    }
    
    public Attendance getNegative(Long schoolId, Long studentId, Long attendanceId, HttpStatus expectedCode, String msg) {
        return null;
    }
    
    public Attendance create(Long schoolId, Long studentId, Attendance attendance, String msg) {
        return null;
    }
    
    public Attendance createNegative(Long schoolId, Long studentId, Attendance attendance, HttpStatus expectedCode, String msg) {
        return null;
    }
    
    public void delete(Long schoolId, Long studentId, Attendance attendance, String msg) {
    }
    
    public void deleteNegative(Long schoolId, Long studentId, Attendance attendance, HttpStatus expectedCode, String msg) {
    }
    
    protected Attendance retrieveAndValidateCreatedAttendance(
            Long schoolId, Long studentId, Long assignmentId, Attendance submitted, String msg) {
        return null;
    }
}
