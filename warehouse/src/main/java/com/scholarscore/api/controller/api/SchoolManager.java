package com.scholarscore.api.controller.api;

import com.scholarscore.models.School;

import java.util.Collection;

/**
 * User: jordan
 * Date: 12/26/14
 * Time: 3:54 PM
 */
public interface SchoolManager {

    public Collection<School> getAllSchools();

    public boolean schoolExists(long schoolId);
    public School getSchool(long schoolId);

    public long createSchool(School school);

    public void saveSchool(School school);

    public void deleteSchool(long schoolId);

}
