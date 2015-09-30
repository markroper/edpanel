package com.scholarscore.api.controller.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Student;
import com.scholarscore.models.Term;
import com.scholarscore.models.EntityId;

public class TermValidatingExecutor {

    private final IntegrationBase serviceBase;
    
    public TermValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }
    
    public Term get(Long schoolId, Long schoolYearId, Long id, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getTermEndpoint(schoolId, schoolYearId, id),// + "/" + Long.toString(id), 
                null);
        Term term = serviceBase.validateResponse(response, new TypeReference<Term>(){});
        Assert.assertNotNull(term, "Unexpected null term returned for case: " + msg);
        
        return term;
    }
    
    public Collection<Student> getStudentsInTermTaughtByTeacher(
            Long schoolId, Long schoolYearId, Long termId, Long teacherId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getTermEndpoint(schoolId, schoolYearId, termId) + "/teachers/" + teacherId + "/students",
                null);
        ArrayList<Student> students = serviceBase.validateResponse(response, new TypeReference<ArrayList<Student>>(){});
        Assert.assertNotNull(students, "Unexpected students returned " + msg);
        return students;
    }
    
    public void getAll(Long schoolId, Long schoolYearId, String msg, int numberOfItems) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getTermEndpoint(schoolId, schoolYearId), 
                null);
        ArrayList<Term> terms = serviceBase.validateResponse(response, new TypeReference<ArrayList<Term>>(){});
        Assert.assertNotNull(terms, "Unexpected null term returned for case: " + msg);
        Assert.assertTrue(terms.size() >= numberOfItems, "Unexpected number of items returned for case: " + msg);
    }
    
    public void getNegative(Long schoolId, Long schoolYearId, Long id, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getTermEndpoint(schoolId, schoolYearId, id),// + "/" + Long.toString(id), 
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned while retrieving term: " + msg);
    }
    
    public Term create(Long schoolId, Long schoolYearId, Term term, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getTermEndpoint(schoolId, schoolYearId), null, term);
        EntityId termId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(termId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedTerm(schoolId, schoolYearId, termId, term, HttpMethod.POST, msg);
    }
    
    public void createNegative(Long schoolId, Long schoolYearId, Term term, HttpStatus expectedCode, String msg) {
        //Attempt to create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getTermEndpoint(schoolId, schoolYearId), null, term);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned for case: " + msg);
    }
    
    public void delete(Long schoolId, Long schoolYearId, Long termId, String msg) {
        //Delete the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getTermEndpoint(schoolId, schoolYearId, termId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(schoolId, schoolYearId, termId, HttpStatus.NOT_FOUND, msg);
    }
    
    public void deleteNegative(Long schoolId, Long schoolYearId, Long termId, HttpStatus expectedCode, String msg) {
        //Attempt to delete the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getTermEndpoint(schoolId, schoolYearId, termId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }

    public Term replace(Long schoolId, Long schoolYearId, Long id, Term term, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getTermEndpoint(schoolId, schoolYearId, id), 
                null, 
                term);
        EntityId termId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(termId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedTerm(schoolId, schoolYearId, termId, term, HttpMethod.PUT, msg);
    }

    public void replaceNegative(Long schoolId, Long schoolYearId, Long id, Term term, HttpStatus expectedCode, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getTermEndpoint(schoolId, schoolYearId, id), 
                null, 
                term);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public Term update(Long schoolId, Long schoolYearId, Long id, Term term, String msg) {
      //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getTermEndpoint(schoolId, schoolYearId, id), 
                null, 
                term);
        EntityId termId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(termId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedTerm(schoolId, schoolYearId, termId, term, HttpMethod.PATCH, msg);
    }

    public void updateNegative(Long schoolId, Long schoolYearId, Long id, Term term, HttpStatus expectedCode, String msg) {
      //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getTermEndpoint(schoolId, schoolYearId, id), 
                null, 
                term);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public IntegrationBase getServiceBase() {
        return serviceBase;
    }
    
    /**
     * Given an ID and the value submitted to a GET/PUT endpoint, retrieve the term via GET, validate it, and 
     * return it to the caller.
     * 
     * @param termId
     * @param submittedTerm
     * @param msg
     * @return
     */
    protected Term retrieveAndValidateCreatedTerm( Long schoolId, Long schoolYearId,
            EntityId termId, Term submittedTerm, HttpMethod method, String msg) {
        //Retrieve and validate the created term
        Term createdTerm = this.get(schoolId, schoolYearId, termId.getId(), msg);
        Term expectedTerm = generateExpectationTerm(submittedTerm, createdTerm, method);
        Assert.assertEquals(createdTerm, expectedTerm, "Unexpected term created for case: " + msg);
        
        return createdTerm;
    }
    /**
     * Given a submitted term object and an term instance returned by the API after creation,
     * this method returns a new Term instance that represents the expected state of the submitted 
     * Term after creation.  The reason that there are differences in the submitted and expected 
     * instances is that there may be system assigned values not in the initially submitted object, for 
     * example, the id property.
     * 
     * @param submitted
     * @param created
     * @return
     */
    protected Term generateExpectationTerm(Term submitted, Term created, HttpMethod method) {
        Term returnTerm = new Term(submitted);
        if(method == HttpMethod.PATCH) {
            returnTerm.mergePropertiesIfNull(created);
        } else if(null != returnTerm && null == returnTerm.getId()) {
            returnTerm.setId(created.getId());
        }
        return returnTerm;
    }
}
