package com.scholarscore.api.persistence;

import com.scholarscore.models.attendance.Attendance;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface AttendancePersistence {
    Long insertAttendance(Long schoolId, Long studentId, Attendance attendance);
    void insertAttendances(Long schoolId, Long studentId, List<Attendance> attendances);
    Attendance select(Long schoolId, Long studentId, Long attendanceId);
    Collection<Attendance> selectAllAttendance(Long schoolId, Long studentId);
    Collection<Attendance> selectAllAttendance(Long schoolId, List<Long> studentId, LocalDate start, LocalDate end);
    Collection<Attendance> selectAllAttendanceForTerm(Long schoolId, Long studentId, Long yearId, Long termId);
    Collection<Attendance> selectAllDailyAttendance(Long studentId);
    Collection<Attendance> selectAttendanceForSection(Long studentId, Long sectionId);
    Long delete(Long schoolId, Long studentId, Long attendanceId);
    void update(Long schoolId, Long studentId, Long attendanceId, Attendance a);
}
