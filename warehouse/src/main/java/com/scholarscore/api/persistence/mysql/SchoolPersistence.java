package com.scholarscore.api.persistence.mysql;

import java.util.Collection;

import com.scholarscore.models.School;

public interface SchoolPersistence {

    public Collection<School> selectAllSchools();

    public School selectSchool(long schoolId);

    public Long createSchool(School school);

    public Long replaceSchool(long schoolId, School school);

    public Long deleteSchool(long schoolId);

}