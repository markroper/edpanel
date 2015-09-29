package com.scholarscore.api.persistence;

import java.util.Collection;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.attendance.Attendance;

public class AttendanceManagerImpl implements AttendanceManager {

    @Override
    public ServiceResponse<Collection<Attendance>> getAllStudentAttendance(
            long schoolId, long studentId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServiceResponse<Collection<Attendance>> getAllStudentAttendanceInTerm(
            long schoolId, long studentId, long schoolYearId, long termId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServiceResponse<Attendance> getStudentAttendance(long schoolId,
            long studentId, long attendanceId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServiceResponse<Long> createAttendance(long schoolId,
            long studentId, Attendance attendance) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServiceResponse<Long> deleteAttendance(long schoolId,
            long studentId, long attendanceId) {
        // TODO Auto-generated method stub
        return null;
    }

}
