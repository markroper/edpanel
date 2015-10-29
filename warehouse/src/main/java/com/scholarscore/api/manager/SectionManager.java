package com.scholarscore.api.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.models.Section;

import java.util.Collection;

public interface SectionManager {
    /**
     * Return all sections within a school
     * @param schoolId ID of parent school
     * @return
     */
    public ServiceResponse<Collection<Section>> getAllSectionsInSchool(long schoolId);

    /**
     * Return all sections within a term
     * @param schoolId ID of parent school
     * @param yearId ID of parent year
     * @param termId the ID of the term
     * @return
     */
    public ServiceResponse<Collection<Section>> getAllSections(long schoolId, long yearId, long termId);
    
    /**
     * Return all sections within a term for a given student
     * @param studentId ID of student
     * @param schoolId ID of parent school
     * @param yearId ID of parent year
     * @param termId the ID of the term
     * @return
     */
    public ServiceResponse<Collection<Section>> getAllSections(long studentId, long schoolId, long yearId, long termId);

    /**
     * Returns ErrorCodes.OK if a section exists and otherwise returns a descriptive error code
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section 
     * @return
     */
    public StatusCode sectionExists(long schoolId, long yearId, long termId, long sectionId);
    
    /**
     * Returns the section with the ID provided, or in degenerate cases, a descriptive error 
     * code will be populated on the returned ServiceResponse instance.
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section
     * @return
     */
    public ServiceResponse<Section> getSection(long schoolId, long yearId, long termId, long sectionId);

    /**
     * Returns a collection of Section instances in a given school, year, term and taught by a specific teacher.
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param teacherId the ID of a teacher associated with the sections
     * @return
     */
    public ServiceResponse<Collection<Section>> getAllSectionsByTeacher(long schoolId, long yearId, long termId, long teacherId);
    
    /**
     * Stores a new term instance in the system and returns the assigned ID populated on
     * the ServiceResponse. In degenerate cases, the returned ServiceResponse
     * is populated with a descriptive error code.
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param section the Section to create
     * @return
     * @throws JsonProcessingException 
     */
    public ServiceResponse<Long> createSection(long schoolId, long yearId, long termId, Section section) throws JsonProcessingException;

    /**
     * Replaces an existing instance with the ID provided with the Section instance
     * provided as a parameter, returning the sectionId on the response for positive cases
     * and a descriptive error code in degenerate cases.
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section
     * @param section The section to replace the existing section with
     * @return
     * @throws JsonProcessingException 
     */
    public ServiceResponse<Long> replaceSection(long schoolId, long yearId, long termId, long sectionId, Section section) throws JsonProcessingException;
    
    /**
     * Performs a partial update on an existing section with the ID provided using the partially
     * populated instance partialSection provided as a parameter. In successful cases, the termId
     * is populated on the return object and in degenerate cases a descriptive error code is populated
     * on the response instance.
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param partialTerm the partially populated term to update the existing term with
     * @return
     * @throws JsonProcessingException 
     */
    public ServiceResponse<Long> updateSection(long schoolId, long yearId, long termId, long sectionId, Section partialSection) throws JsonProcessingException;

    /**
     * Deletes a section with the id provided and returns an empty ServiceResponse instance 
     * in successful cases or a ServiceResponse with a descriptive error code in 
     * degenerate cases.
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param sectionId the ID of the section
     * @return
     */
    public ServiceResponse<Long> deleteSection(long schoolId, long yearId, long termId, long sectionId);
}
