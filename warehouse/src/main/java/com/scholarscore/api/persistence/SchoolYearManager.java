package com.scholarscore.api.persistence;

import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.SchoolYear;

import java.util.Collection;

/**
 * User: jordan
 * Date: 12/26/14
 * Time: 5:08 PM
 */
public interface SchoolYearManager {

    public ServiceResponse<Collection<SchoolYear>> getAllSchoolYears(long schoolId);

    public StatusCode schoolYearExists(long schoolId, long schoolYearId);
    
    public ServiceResponse<SchoolYear> getSchoolYear(long schoolId, long schoolYearId);

    public ServiceResponse<Long> createSchoolYear(long schoolId, SchoolYear schoolYear);

    public ServiceResponse<Long> replaceSchoolYear(long schoolId, long schoolYearId, SchoolYear schoolYear);
    
    public ServiceResponse<Long> updateSchoolYear(long schoolId, long schoolYearId, SchoolYear schoolYear);

    public ServiceResponse<Long> deleteSchoolYear(long schoolId, long schoolYearId);
}
