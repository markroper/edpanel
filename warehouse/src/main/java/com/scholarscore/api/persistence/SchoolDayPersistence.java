package com.scholarscore.api.persistence;

import com.scholarscore.models.attendance.SchoolDay;

import java.util.Collection;

public interface SchoolDayPersistence {
    public Long insertSchoolDay(long schoolId, SchoolDay schoolDay);
    public SchoolDay select(long schoolId, long schoolDayId);
    public Collection<SchoolDay> selectAllSchoolDays(long schoolId);
    public Collection<SchoolDay> selectAllSchoolDaysInYear(long schoolId, long schoolYearId);
    public Long delete(long schoolId, long schoolDayId);
}
