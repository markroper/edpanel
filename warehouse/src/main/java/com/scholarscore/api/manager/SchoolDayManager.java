package com.scholarscore.api.manager;

import java.util.Collection;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.models.attendance.SchoolDay;

public interface SchoolDayManager {
    /**
     * Get all school days in a school from all time.
     * 
     * @param schoolId The school ID
     * @return
     */
    public ServiceResponse<Collection<SchoolDay>> getAllDays(long schoolId);
    
    /**
     * Get all school days in a particular school year
     * 
     * @param schoolId The ID of the school
     * @param schoolYearId The ID of the school year
     * @return
     */
    public ServiceResponse<Collection<SchoolDay>> getAllDaysInYear(long schoolId, long schoolYearId);
    
    /**
     * Get a single school day by ID
     * 
     * @param schoolId The school ID
     * @param schoolDayId The ID of the school day
     * @return
     */
    public ServiceResponse<SchoolDay> getDay(long schoolId, long schoolDayId);
    
    /**
     * Creates a school day associated with a school
     * 
     * @param schoolId The school ID
     * @param schoolDay The school day instance
     * @return
     */
    public ServiceResponse<Long> createSchoolDay(long schoolId, SchoolDay schoolDay);
    
    /**
     * Deleted a school day by school ID and day ID
     * @param schoolId The containing school ID
     * @param schoolDayId The school day ID
     * @return
     */
    public ServiceResponse<Long> deleteSchoolDay(long schoolId, long schoolDayId);
    
    /**
     * Returns StatusCode.OK if the school day exists and otherwise a not found.
     * @param schoolId
     * @param schoolDayId
     * @return
     */
    public StatusCode schoolDayExists(long schoolId, long schoolDayId);
}
