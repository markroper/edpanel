package com.scholarscore.api.persistence.mysql;

import java.util.Collection;

import com.scholarscore.models.School;

public interface SchoolPersistence {

    public Collection<School> selectAll();

    public School selectSchool(long schoolId);

    public Long createSchool(School school);

    public Long replaceSchool(long schoolId, School school);

    /**
     * Only need to delete the parent row, FK cascades deletes
     */
    public Long delete(long schoolId);

}