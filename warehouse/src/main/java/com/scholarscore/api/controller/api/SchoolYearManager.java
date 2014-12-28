package com.scholarscore.api.controller.api;

import com.scholarscore.models.SchoolYear;

import java.util.Collection;

/**
 * User: jordan
 * Date: 12/26/14
 * Time: 5:08 PM
 */
public interface SchoolYearManager {

    public SchoolYearManager buildSchoolYearManager(long schoolId);

    public Collection<SchoolYear> getAllSchoolYears(long schoolId, long schoolYearId);

    public SchoolYear getSchoolYear(long schoolYearId);

    /*
    *
    public Collection<School> getAllSchools();

    public boolean schoolExists(long schoolId);
    public School getSchool(long schoolId);

    public long createSchool(School school);

    public void saveSchool(School school);

    public void deleteSchool(long schoolId);
    * */
}
