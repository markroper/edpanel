package com.scholarscore.api.persistence;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.scholarscore.api.persistence.mysql.AttendancePersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.attendance.Attendance;

public class AttendanceManagerImpl implements AttendanceManager {
    private static final String ATTENDANCE = "attendance";
    
    @Autowired
    private PersistenceManager pm;
    
    @Autowired 
    private AttendancePersistence attendancePersistence;
    
    @Override
    public ServiceResponse<Collection<Attendance>> getAllStudentAttendance(
            long schoolId, long studentId) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if (!code.isOK()) {
            return new ServiceResponse<Collection<Attendance>>(code);
        }
        code = pm.getStudentManager().studentExists(studentId);
        if (!code.isOK()) {
            return new ServiceResponse<Collection<Attendance>>(code);
        }
        Collection<Attendance> attendance = attendancePersistence.selectAllAttendance(schoolId, studentId);
        return new ServiceResponse<Collection<Attendance>>(attendance);
    }

    @Override
    public ServiceResponse<Collection<Attendance>> getAllStudentAttendanceInTerm(
            long schoolId, long studentId, long schoolYearId, long termId) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if (!code.isOK()) {
            return new ServiceResponse<Collection<Attendance>>(code);
        }
        code = pm.getStudentManager().studentExists(studentId);
        if (!code.isOK()) {
            return new ServiceResponse<Collection<Attendance>>(code);
        }
        code = pm.getTermManager().termExists(schoolId, schoolYearId, termId);
        if (!code.isOK()) {
            return new ServiceResponse<Collection<Attendance>>(code);
        }
        Collection<Attendance> attendance = attendancePersistence.selectAllAttendanceForTerm(
                schoolId, studentId, schoolYearId, termId);
        return new ServiceResponse<Collection<Attendance>>(attendance);
    }

    @Override
    public ServiceResponse<Attendance> getStudentAttendance(long schoolId,
            long studentId, long attendanceId) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if (!code.isOK()) {
            return new ServiceResponse<Attendance>(code);
        }
        code = pm.getStudentManager().studentExists(studentId);
        if (!code.isOK()) {
            return new ServiceResponse<Attendance>(code);
        }
        Attendance attendance = attendancePersistence.select(schoolId, studentId, attendanceId);
        if(null == attendance) {
            code = StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{ATTENDANCE, attendanceId});
            return new ServiceResponse<Attendance>(code);
        };
        return new ServiceResponse<Attendance>(attendancePersistence.select(schoolId, studentId, attendanceId));
    }

    @Override
    public ServiceResponse<Long> createAttendance(long schoolId,
            long studentId, Attendance attendance) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if (!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        code = pm.getStudentManager().studentExists(studentId);
        if (!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        return new ServiceResponse<Long>(attendancePersistence.insertAttendance(schoolId, studentId, attendance));
    }

    @Override
    public ServiceResponse<Long> deleteAttendance(long schoolId,
            long studentId, long attendanceId) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if (!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        code = pm.getStudentManager().studentExists(studentId);
        if (!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        return new ServiceResponse<Long>(attendancePersistence.delete(schoolId, studentId, attendanceId));
    }

    public PersistenceManager getPm() {
        return pm;
    }

    public void setPm(PersistenceManager pm) {
        this.pm = pm;
    }

    public AttendancePersistence getAttendancePersistence() {
        return attendancePersistence;
    }

    public void setAttendancePersistence(AttendancePersistence attendancePersistance) {
        this.attendancePersistence = attendancePersistance;
    }
}
