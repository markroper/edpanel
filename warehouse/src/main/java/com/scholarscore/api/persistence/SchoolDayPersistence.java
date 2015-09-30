package com.scholarscore.api.persistence;

import java.util.Collection;

import com.scholarscore.models.attendance.SchoolDay;

public interface SchoolDayPersistence {
    public Long insertSchoolDay(long schoolId, SchoolDay schoolDay);
    public SchoolDay select(long schoolId, long schoolDayId);
    public Collection<SchoolDay> selectAllSchoolDays(long schoolId);
    public Collection<SchoolDay> selectAllSchoolDaysInYear(long schoolId, long schoolYearId);
    public Long delete(long schoolId, long schoolDayId);
}
