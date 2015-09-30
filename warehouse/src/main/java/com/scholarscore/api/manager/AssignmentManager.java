package com.scholarscore.api.manager;

import java.util.Collection;

import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.Assignment;

public interface AssignmentManager {
    /**
     * Return all section assignments within a section
     * @param schoolId ID of parent school
     * @param yearId ID of parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section
     * @return
     */
    public ServiceResponse<Collection<Assignment>> getAllAssignments(long schoolId, long yearId, 
            long termId, long sectionId);

    /**
     * Returns a StatusCode with code StatusCodes.OK_ERROR_CODE if a section assignment exists and otherwise returns a descriptive error code
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section 
     * @param sectionAssignmentId the ID of the section assignment
     * @return
     */
    public StatusCode assignmentExists(long schoolId, long yearId, long termId, 
            long sectionId, long sectionAssignmentId);
    
    /**
     * Returns the section assignment with the ID provided, or in degenerate cases, a descriptive error 
     * code will be populated on the returned ServiceResponse instance.
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section
     * @param sectionAssignmentId the ID of the section assignment
     * @return
     */
    public ServiceResponse<Assignment> getAssignment(long schoolId, long yearId, long termId, 
            long sectionId, long sectionAssignmentId);

    /**
     * Stores a new section assignment instance in the system and returns the assigned ID populated on
     * the ServiceResponse. In degenerate cases, the returned ServiceResponse
     * is populated with a descriptive error code.
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section
     * @param sectionAssignment the section assignment to create on the section
     * @return
     */
    public ServiceResponse<Long> createAssignment(long schoolId, long yearId, long termId, 
            long sectionId, Assignment sectionAssignment);

    /**
     * Replaces an existing instance with the ID provided with the section assignment instance
     * provided as a parameter, returning the section assignment ID on the response for positive cases
     * and a descriptive error code in degenerate cases.
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section
     * @param sectionAssignmentId the ID of the section assignment
     * @param sectionAssignment The section assignment to replace the previously stored assignment with
     * @return
     */
    public ServiceResponse<Long> replaceAssignment(long schoolId, long yearId, long termId, 
            long sectionId, long sectionAssignmentId, Assignment sectionAssignment);
    
    /**
     * Performs a partial update on an existing section assignment with the ID provided using the partially
     * populated instance partialSection provided as a parameter. In successful cases, the termId
     * is populated on the return object and in degenerate cases a descriptive error code is populated
     * on the response instance.
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section
     * @param sectionAssignmentId the ID of the section assignment
     * @param partialAssignment the section assignment instance to merge with the previously stored instance
     * @return
     */
    public ServiceResponse<Long> updateAssignment(long schoolId, long yearId, long termId, long sectionId, 
            long sectionAssignmentId, Assignment partialAssignment);

    /**
     * Deletes a section assignment with the id provided and returns an empty ServiceResponse instance 
     * in successful cases or a ServiceResponse with a descriptive error code in 
     * degenerate cases.
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section
     * @param sectionAssignmentId the ID of the section assignment
     * @return
     */
    public ServiceResponse<Long> deleteAssignment(long schoolId, long yearId, long termId, 
            long sectionId, long sectionAssignmentId);
}
