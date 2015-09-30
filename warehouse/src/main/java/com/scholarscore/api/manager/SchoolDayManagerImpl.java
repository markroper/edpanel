package com.scholarscore.api.manager;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.scholarscore.api.persistence.SchoolDayPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.attendance.SchoolDay;

public class SchoolDayManagerImpl implements SchoolDayManager{
    private static final String SCHOOL_DAY = "school day";
    
    @Autowired
    private OrchestrationManager pm;

    @Autowired
    private SchoolDayPersistence dayPersistence;
    
    @Override
    public ServiceResponse<Collection<SchoolDay>> getAllDays(long schoolId) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if (!code.isOK()) {
            return new ServiceResponse<Collection<SchoolDay>>(code);
        }
        return new ServiceResponse<Collection<SchoolDay>>(dayPersistence.selectAllSchoolDays(schoolId));
    }

    @Override
    public ServiceResponse<Collection<SchoolDay>> getAllDaysInYear(
            long schoolId, long schoolYearId) {
        StatusCode code = pm.getSchoolYearManager().schoolYearExists(schoolId, schoolYearId);
        if (!code.isOK()) {
            return new ServiceResponse<Collection<SchoolDay>>(code);
        }
        return new ServiceResponse<Collection<SchoolDay>>(dayPersistence.selectAllSchoolDaysInYear(schoolId, schoolYearId));
    }

    @Override
    public ServiceResponse<SchoolDay> getDay(long schoolId, long schoolDayId) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if (!code.isOK()) {
            return new ServiceResponse<SchoolDay>(code);
        }
        SchoolDay day = dayPersistence.select(schoolId, schoolDayId);
        if(null == day) {
            code = StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{SCHOOL_DAY, schoolDayId});
            return new ServiceResponse<SchoolDay>(code);
        };
        return new ServiceResponse<SchoolDay>(day);
    }

    @Override
    public ServiceResponse<Long> createSchoolDay(long schoolId,
            SchoolDay schoolDay) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if (!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        return new ServiceResponse<Long>(dayPersistence.insertSchoolDay(schoolId, schoolDay));
    }

    @Override
    public ServiceResponse<Long> deleteSchoolDay(long schoolId, long schoolDayId) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if (!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        return new ServiceResponse<Long>(dayPersistence.delete(schoolId, schoolDayId));
    }

    @Override
    public StatusCode schoolDayExists(long schoolId, long schoolDayId) {
        SchoolDay day = dayPersistence.select(schoolId, schoolDayId);
        if(null == day) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{SCHOOL_DAY, schoolDayId});
        };
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    public OrchestrationManager getPm() {
        return pm;
    }

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }

    public SchoolDayPersistence getDayPersistence() {
        return dayPersistence;
    }

    public void setDayPersistence(SchoolDayPersistence dayPersistence) {
        this.dayPersistence = dayPersistence;
    }

}
