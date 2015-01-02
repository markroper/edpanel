package com.scholarscore.api.persistence;

import com.scholarscore.models.SchoolYear;

import java.util.Collection;

/**
 * User: jordan
 * Date: 12/26/14
 * Time: 5:08 PM
 */
public interface SchoolYearManager {

    public Collection<SchoolYear> getAllSchoolYears(long schoolId);

    public boolean schoolYearExists(long schoolId, long schoolYearId);
    public SchoolYear getSchoolYear(long schoolId, long schoolYearId);

    public long createSchoolYear(long schoolId, SchoolYear schoolYear);

    public void saveSchoolYear(long schoolId, SchoolYear schoolYear);

    public void deleteSchoolYear(long schoolId, long schoolYearId);
}
