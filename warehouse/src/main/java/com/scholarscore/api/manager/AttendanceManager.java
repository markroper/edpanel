package com.scholarscore.api.manager;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.attendance.Attendance;

import java.util.Collection;
import java.util.List;

public interface AttendanceManager {
    /**
     * Returns all attendance entries for a student at a school over all time
     * @param schoolId
     * @param studentId
     * @return
     */
    public ServiceResponse<Collection<Attendance>> getAllStudentAttendance(long schoolId, long studentId);
    
    /**
     * Returns all attendance entries for a student at a school in a single term
     * @param schoolId
     * @param studentId
     * @param schoolYearId
     * @param termId
     * @return
     */
    public ServiceResponse<Collection<Attendance>> getAllStudentAttendanceInTerm(
            long schoolId, long studentId, long schoolYearId, long termId);
    
    /**
     * Returns a single attendance entry for a student at a school by the attendance ID
     * @param schoolId
     * @param studentId
     * @param attendanceId
     * @return
     */
    public ServiceResponse<Attendance> getStudentAttendance(long schoolId, long studentId, long attendanceId);
    
    /**
     * Creates an attendance entry for a student at a school and returns a service response containing the created ID
     * @param schoolId
     * @param studentId
     * @param attendance
     * @return
     */
    public ServiceResponse<Long> createAttendance(long schoolId, long studentId, Attendance attendance);

    /**
     * Creates attendance entries for a student at a school and returns a service response
     * @param schoolId
     * @param studentId
     * @param attendances
     * @return
     */
    public ServiceResponse<Void> createAttendances(long schoolId, long studentId, List<Attendance> attendances);

    /**
     * Deletes an attendance entry for a student at a school and returns a service response
     * containing the id of the attendance entry that was deleted
     * @param schoolId
     * @param studentId
     * @param attendanceId
     * @return
     */
    public ServiceResponse<Long> deleteAttendance(long schoolId, long studentId, long attendanceId);

    /**
     * Replaces an attendance entry for a student at a school and returns a service response
     * @param schoolId
     * @param studentId
     * @param attendanceId
     * @return
     */
    public ServiceResponse<Void> replaceAttendance(long schoolId, long studentId, long attendanceId, Attendance a);
}
