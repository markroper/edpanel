package com.scholarscore.api.manager;

import java.util.Collection;

import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.StudentSectionGrade;

public interface StudentSectionGradeManager {
    /**
     * Return all student section grades within a section
     * @param schoolId ID of parent school
     * @param yearId ID of parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section
     * @param studentId the ID of the student
     * @return
     */
    public ServiceResponse<Collection<StudentSectionGrade>> getAllStudentSectionGrades(
            long schoolId, long yearId, long termId, long sectionId);

    /**
     * Return all section grades completed by a student
     * @param studentId the ID of the student
     * @return
     */
    public ServiceResponse<Collection<StudentSectionGrade>> getSectionGradesForStudent(
        long studentId);
    
    /**
     * Returns ErrorCodes.OK if a section student grade exists and otherwise returns a descriptive error code
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section
     * @param studentId the ID of the student
     * @return
     */
    public StatusCode studentSectionGradeExists(
            long schoolId, long yearId, long termId, long sectionId, long studentId);
    
    /**
     * Returns the student section grade with the ID provided, or in degenerate cases, a descriptive error 
     * code will be populated on the returned ServiceResponse instance.
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section
     * @param studentId the ID of the student
     * @return
     */
    public ServiceResponse<StudentSectionGrade> getStudentSectionGrade(
            long schoolId, long yearId, long termId, long sectionId, long studentId);

    /**
     * Stores a new student section grade instance in the system and returns the assigned ID populated on
     * the ServiceResponse. In degenerate cases, the returned ServiceResponse
     * is populated with a descriptive error code.
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section
     * @param studentId the ID of the student
     * @param grade the Section to create
     * @return
     */
    public ServiceResponse<Long> createStudentSectionGrade(
            long schoolId, long yearId, long termId, long sectionId, long studentId, StudentSectionGrade grade);

    /**
     * Replaces an existing instance with the ID provided with the StudentSectionGrade instance
     * provided as a parameter, returning the sectionId on the response for positive cases
     * and a descriptive error code in degenerate cases.
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section
     * @param studentId the ID of the student
     * @param grade The student section grade to replace the existing section with
     * @return
     */
    public ServiceResponse<Long> replaceStudentSectionGrade(long schoolId, long yearId, long termId, 
            long sectionId, long studentId, StudentSectionGrade grade);
    
    /**
     * Performs a partial update on an existing StudentSectionGrade with the ID provided using the partially
     * populated instance grade provided as a parameter. In successful cases, the termId
     * is populated on the return object and in degenerate cases a descriptive error code is populated
     * on the response instance.
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param studentId the ID of the student
     * @param grade the partially populated StudentSectionGrade to update the existing term with
     * @return
     */
    public ServiceResponse<Long> updateStudentSectionGrade(long schoolId, long yearId, long termId, 
            long sectionId, long studentId, StudentSectionGrade grade);

    /**
     * Deletes a StudentSectionGrade with the id provided and returns an empty ServiceResponse instance 
     * in successful cases or a ServiceResponse with a descriptive error code in 
     * degenerate cases.
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section
     * @param studentId the ID of the student
     * @return
     */
    public ServiceResponse<Long> deleteStudentSectionGrade(
            long schoolId, long yearId, long termId, long sectionId, long studentId);
}
