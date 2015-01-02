package com.scholarscore.api.persistence;

import java.util.Collection;

import com.scholarscore.api.util.ErrorCode;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.Term;

public interface TermManager {

    /**
     * Return all terms within a school year
     * @param schoolId ID of parent school
     * @param yearId ID of parent year
     * @return
     */
    public ServiceResponse<Collection<Term>> getAllTerms(long schoolId, long yearId);

    /**
     * Returns ErrorCodes.OK if a term exists and otherwise returns a descriptive error code
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @return
     */
    public ErrorCode termExists(long schoolId, long yearId, long termId);
    
    /**
     * Returns the term with the ID provided, or in degenerate cases, a descriptive error 
     * code will be populated on the returned ServiceResponse instance.
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @return
     */
    public ServiceResponse<Term> getTerm(long schoolId, long yearId, long termId);

    /**
     * Stores a new term instance in the system and returns the assigned ID populated on
     * the ServiceResponse. In degenerate cases, the returned ServiceResponse
     * is populated with a descriptive error code.
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param term the term to store
     * @return
     */
    public ServiceResponse<Long> createTerm(long schoolId, long yearId, Term term);

    /**
     * Replaces an existing instance with the ID provided with the Term instance
     * provided as a parameter, returning the termId on the response for positive cases
     * and a descriptive error code in degenerate cases.
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param term Term to replace the existing term with
     * @return
     */
    public ServiceResponse<Long> replaceTerm(long schoolId, long yearId, long termId, Term term);
    
    /**
     * Performs a partial update on an existing Term with the ID provided using the partially
     * populated instance partialTerm provided as a parameter. In successful cases, the termId
     * is populated on the return object and in degenerate cases a descriptive error code is populated
     * on the response instance.
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @param partialTerm the partially populated term to update the existing term with
     * @return
     */
    public ServiceResponse<Long> updateTerm(long schoolId, long yearId, long termId, Term partialTerm);

    /**
     * Deletes a term with the id provided and returns an empty ServiceResponse instance 
     * in successful cases or a ServiceResponse with a descriptive error code in 
     * degenerate cases.
     * 
     * @param schoolId ID of parent school
     * @param yearId the ID of the parent year
     * @param termId the ID of the term
     * @return
     */
    public ServiceResponse<Long> deleteTerm(long schoolId, long yearId, long termId);
}
