package com.scholarscore.api.persistence;

import com.scholarscore.models.attendance.Attendance;

import java.util.Collection;
import java.util.List;

public interface AttendancePersistence {
    public Long insertAttendance(Long schoolId, Long studentId, Attendance attendance);
    public void insertAttendances(Long schoolId, Long studentId, List<Attendance> attendances);
    public Attendance select(Long schoolId, Long studentId, Long attendanceId);
    public Collection<Attendance> selectAllAttendance(Long schoolId, Long studentId);
    public Collection<Attendance> selectAllAttendanceForTerm(Long schoolId, Long studentId, Long yearId, Long termId);
    public Collection<Attendance> selectAttendanceForSection(Long schoolId, Long studentId, Long sectionId);
    public Long delete(Long schoolId, Long studentId, Long attendanceId);
    public void update(Long schoolId, Long studentId, Long attendanceId, Attendance a);
}
