package com.scholarscore.api.persistence.mysql;

import java.util.Collection;

import com.scholarscore.models.SchoolYear;

public interface SchoolYearPersistence {

    public Collection<SchoolYear> selectAllSchoolYears(long schoolId);

    public SchoolYear selectSchoolYear(
            long schoolId, 
            long schoolYearId);

    public Long insertSchoolYear(
            long schoolId, 
            SchoolYear schoolYear);

    public Long updateSchoolYear(
            long schoolId, 
            long schoolYearId,
            SchoolYear schoolYear);

    public Long deleteSchoolYear(long schoolYearId);

}