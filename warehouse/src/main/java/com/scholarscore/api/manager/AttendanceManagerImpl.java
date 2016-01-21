package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.AttendancePersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.attendance.Attendance;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public class AttendanceManagerImpl implements AttendanceManager {
    private static final String ATTENDANCE = "attendance";
    
    @Autowired
    private OrchestrationManager pm;
    
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
    public ServiceResponse<Collection<Attendance>> getAllStudentAttendanceInRange(long schoolId, List<Long> studentIds, LocalDate start, LocalDate end) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if (!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        Collection<Attendance> attendance = attendancePersistence.selectAllAttendance(schoolId, studentIds, start, end);
        return new ServiceResponse<>(attendance);
    }

    @Override
    public ServiceResponse<Collection<Attendance>> getAllStudentSectionAttendance(
            long schoolId, long studentId, long sectionId) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if (!code.isOK()) {
            return new ServiceResponse<Collection<Attendance>>(code);
        }
        code = pm.getStudentManager().studentExists(studentId);
        if (!code.isOK()) {
            return new ServiceResponse<Collection<Attendance>>(code);
        }
        Collection<Attendance> attendance = attendancePersistence.selectAttendanceForSection(schoolId, studentId, sectionId);
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
    public ServiceResponse<Void> createAttendances(long schoolId,
                                                   long studentId,
                                                   List<Attendance> attendances) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if (!code.isOK()) {
            return new ServiceResponse<Void>(code);
        }
        code = pm.getStudentManager().studentExists(studentId);
        if (!code.isOK()) {
            return new ServiceResponse<Void>(code);
        }
        attendancePersistence.insertAttendances(schoolId, studentId, attendances);
        return new ServiceResponse<Void>((Void) null);
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

    @Override
    public ServiceResponse<Void> replaceAttendance(long schoolId, long studentId, long attendanceId, Attendance a) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if (!code.isOK()) {
            return new ServiceResponse<Void>(code);
        }
        code = pm.getStudentManager().studentExists(studentId);
        if (!code.isOK()) {
            return new ServiceResponse<Void>(code);
        }
        attendancePersistence.update(schoolId, studentId, attendanceId, a);
        return new ServiceResponse<Void>((Void) null);
    }

    public OrchestrationManager getPm() {
        return pm;
    }

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }

    public AttendancePersistence getAttendancePersistence() {
        return attendancePersistence;
    }

    public void setAttendancePersistence(AttendancePersistence attendancePersistance) {
        this.attendancePersistence = attendancePersistance;
    }
}
