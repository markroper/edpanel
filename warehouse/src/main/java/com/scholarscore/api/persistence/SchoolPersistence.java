package com.scholarscore.api.persistence;

import com.scholarscore.models.School;

import java.util.Collection;

public interface SchoolPersistence {

    public Collection<School> selectAll();

    public School selectSchool(Long schoolId);

    public Long createSchool(School school);

    public Long replaceSchool(long schoolId, School school);

    /**
     * Only need to delete the parent row, FK cascades deletes
     */
    public Long delete(long schoolId);

}