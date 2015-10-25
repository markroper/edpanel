package com.scholarscore.api.manager;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.models.School;

import java.util.Collection;

/**
 * The interface defines the operations supported by a persistence manager
 * for School instances.
 * 
 * User: jordan
 * Date: 12/26/14
 * Time: 3:54 PM
 */
public interface SchoolManager {

    /**
     * Return all schools within a district
     * @return
     */
    public Collection<School> getAllSchools();

    /**
     * Returns true if a school with the id provided exists and otherwise returns false
     * 
     * @param schoolId
     * @return
     */
    public StatusCode schoolExists(Long schoolId);
    
    /**
     * Returns the school with the ID provided, or in degenerate cases, a descriptive error 
     * code will be populated on the returned ServiceResponse instance.
     * 
     * @param schoolId
     * @return
     */
    public ServiceResponse<School> getSchool(long schoolId);

    /**
     * Stores a new school instance in the system and returns the assigned ID populated on
     * the ServiceResponse return instance. In degenerate cases, the returned ServiceResponse
     * is populated with a descriptive error code.
     * 
     * @param school
     * @return
     */
    public ServiceResponse<Long> createSchool(School school);

    /**
     * Replaces an existing instance with the ID provided with the School instance
     * provided as a parameter, returning the schoolId on the response for positive cases
     * and a descriptive error code in degenerate cases.
     * 
     * @param schoolId
     * @param school
     * @return
     */
    public ServiceResponse<Long> replaceSchool(long schoolId, School school);
    
    /**
     * Performs a partial update on an existing School with the ID provided using the partially
     * populated instance partialSchool provided as a parameter. In successful cases, the schoolId
     * is populated on the return object and in degenerate cases a descriptive error code is populated
     * on the response instance.
     * 
     * @param schoolId
     * @param partialSchool
     * @return
     */
    public ServiceResponse<Long> updateSchool(long schoolId, School partialSchool);

    /**
     * Deletes a school with the id provided and returns an empty ServiceResponse instance 
     * in successful cases or a ServiceResponse with a descriptive error code populated in 
     * degenerate cases.
     * 
     * @param schoolId
     * @return
     */
    public ServiceResponse<Long> deleteSchool(long schoolId);

}
