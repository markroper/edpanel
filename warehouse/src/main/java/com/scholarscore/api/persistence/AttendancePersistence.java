package com.scholarscore.api.persistence;

import java.util.Collection;

import com.scholarscore.models.attendance.Attendance;

public interface AttendancePersistence {
    public Long insertAttendance(Long schoolId, Long studentId, Attendance attendance);
    public Attendance select(Long schoolId, Long studentId, Long attendanceId);
    public Collection<Attendance> selectAllAttendance(Long schoolId, Long studentId);
    public Collection<Attendance> selectAllAttendanceForTerm(Long schoolId, Long studentId, Long yearId, Long termId);
    public Long delete(Long schoolId, Long studentId, Long attendanceId);
}
