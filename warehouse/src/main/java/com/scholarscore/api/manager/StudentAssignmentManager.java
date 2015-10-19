package com.scholarscore.api.manager;

import java.util.Collection;

import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.assignment.StudentAssignment;

public interface StudentAssignmentManager {
    /**
     * Return all student assignments within a section
     * @param schoolId ID of parent school
     * @param yearId ID of parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section
     * @param sectAssignmentId the ID of the section assignment
     * @return
     */
    public ServiceResponse<Collection<StudentAssignment>> getAllStudentAssignments(long schoolId, long yearId, 
            long termId, long sectionId, long sectAssignmentId);

    /**
     * Return all student assignments for a single student in a single section
     * @param studentId
     * @param schoolId
     * @param yearId
     * @param termId
     * @param sectionId
     * @return
     */
    public ServiceResponse<Collection<StudentAssignment>> getOneSectionOneStudentsAssignments(
            long studentId, long schoolId, long yearId, long termId, long sectionId);
    
    /**
     * Returns ErrorCodes.OK if a student assignment exists and otherwise returns a descriptive error code
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section 
     * @param sectionAssignmentId the ID of the section assignment
     * @param studentAssignmentId the ID of the student assignment
     * @return
     */
    public StatusCode studentAssignmentExists(long schoolId, long yearId, long termId, 
            long sectionId, long sectionAssignmentId, long studentAssignmentId);
    
    /**
     * Returns the student assignment with the ID provided, or in degenerate cases, a descriptive error 
     * code will be populated on the returned ServiceResponse instance.
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section
     * @param sectionAssignmentId the ID of the section assignment
     * @param studentAssignmentId the ID of the student assignment
     * @return
     */
    public ServiceResponse<StudentAssignment> getStudentAssignment(long schoolId, long yearId, long termId, 
            long sectionId, long sectionAssignmentId, long studentAssignmentId);

    /**
     * Stores a new student assignment instance in the system and returns the assigned ID populated on
     * the ServiceResponse. In degenerate cases, the returned ServiceResponse
     * is populated with a descriptive error code.
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section
     * @param sectionAssignmentId the ID of the section assignment
     * @param studentAssigment the student assignment to create on the section
     * @return
     */
    public ServiceResponse<Long> createStudentAssignment(long schoolId, long yearId, long termId, 
            long sectionId, long sectionAssignmentId, StudentAssignment studentAssignment);

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
     * @param studentAssignmentId the ID of the student assignment
     * @param studentAssignment the student assignment instance to merge with the previously stored instance
     * @return
     */
    public ServiceResponse<Long> replaceStudentAssignment(long schoolId, long yearId, long termId, 
            long sectionId, long sectionAssignmentId, long studentAssignmentId, StudentAssignment studentAssignment );
    
    /**
     * Performs a partial update on an existing student assignment with the ID provided using the partially
     * populated instance studentAssignment provided as a parameter. In successful cases, the termId
     * is populated on the return object and in degenerate cases a descriptive error code is populated
     * on the response instance.
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section
     * @param sectionAssignmentId the ID of the section assignment
     * @param studentAssignmentId the ID of the student assignment
     * @param studentAssignment the student assignment instance to merge with the previously stored instance
     * @return
     */
    public ServiceResponse<Long> updateStudentAssignment(long schoolId, long yearId, long termId, long sectionId, 
            long sectionAssignmentId, long studentAssignmentId, StudentAssignment studentAssignment );

    /**
     * Deletes a student assignment with the id provided and returns an empty ServiceResponse instance 
     * in successful cases or a ServiceResponse with a descriptive error code in 
     * degenerate cases.
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section
     * @param sectionAssignmentId the ID of the section assignment
     * @param studentAssignmentId the ID of the student assignment
     * @return
     */
    public ServiceResponse<Long> deleteStudentAssignment(long schoolId, long yearId, long termId, 
            long sectionId, long sectionAssignmentId, long studentAssignmentId);
}
