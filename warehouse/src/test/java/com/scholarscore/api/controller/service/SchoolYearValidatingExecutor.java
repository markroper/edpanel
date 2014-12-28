package com.scholarscore.api.controller.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.Term;

public class SchoolYearValidatingExecutor {
    private final IntegrationBase serviceBase;
    
    public SchoolYearValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }
    
    public SchoolYear get(Long schoolId, Long schoolYearId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getSchoolYearEndpoint(schoolId, schoolYearId),
                null);
        SchoolYear schoolYear = serviceBase.validateResponse(response, new TypeReference<SchoolYear>(){});
        Assert.assertNotNull(schoolYear, "Unexpected null schoolYear returned for case: " + msg);
        
        return schoolYear;
    }
    
    public void getAll(Long schoolId, String msg, int numberOfItems) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getSchoolYearEndpoint(schoolId), 
                null);
        ArrayList<SchoolYear> schoolYears = serviceBase.validateResponse(response, new TypeReference<ArrayList<SchoolYear>>(){});
        Assert.assertNotNull(schoolYears, "Unexpected null schoolYear returned for case: " + msg);
        Assert.assertEquals(schoolYears.size(), numberOfItems, "Unexpected number of items returned for case: " + msg);
    }
    
    public void getNegative(Long schoolId, Long schoolYearId, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getSchoolYearEndpoint(schoolId, schoolYearId),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned while retreiving schoolYear: " + msg);
    }
    
    public SchoolYear create(Long schoolId, SchoolYear schoolYear, String msg) {
        //Create the schoolYear
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getSchoolYearEndpoint(schoolId), null, schoolYear);
        EntityId returnedSchoolYearId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedSchoolYearId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedSchoolYear(schoolId, returnedSchoolYearId.getId(), schoolYear, HttpMethod.POST, msg);
    }
    
    public void createNegative(Long schoolId, SchoolYear schoolYear, HttpStatus expectedCode, String msg) {
        //Attempt to create the schoolYear
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getSchoolYearEndpoint(schoolId), null, schoolYear);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned for case: " + msg);
    }
    
    public void delete(Long schoolId, Long schoolYearId, String msg) {
        //Delete the schoolYear
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getSchoolYearEndpoint(schoolId, schoolYearId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(schoolId, schoolYearId, HttpStatus.NOT_FOUND, msg);
    }
    
    public void deleteNegative(Long schoolId, Long schoolYearId, HttpStatus expectedCode, String msg) {
        //Attempt to delete the schoolYear
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getSchoolYearEndpoint(schoolId, schoolYearId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }

    public SchoolYear replace(Long schoolId, Long schoolYearId, SchoolYear schoolYear, String msg) {
        //Create the schoolYear
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getSchoolYearEndpoint(schoolId, schoolYearId), 
                null, 
                schoolYear);
        EntityId returnedSchoolYearId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedSchoolYearId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedSchoolYear(schoolId, schoolYearId, schoolYear, HttpMethod.PUT, msg);
    }

    public void replaceNegative(Long schoolId, Long schoolYearId, SchoolYear schoolYear, HttpStatus expectedCode, String msg) {
        //Create the schoolYear
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getSchoolYearEndpoint(schoolId, schoolYearId), 
                null, 
                schoolYear);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public SchoolYear update(Long schoolId, Long schoolYearId, SchoolYear schoolYear, String msg) {
      //Create the schoolYear
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getSchoolYearEndpoint(schoolId, schoolYearId), 
                null, 
                schoolYear);
        EntityId returnedSchoolYearId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedSchoolYearId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedSchoolYear(schoolId, returnedSchoolYearId.getId(), schoolYear, HttpMethod.PATCH, msg);
    }

    public void updateNegative(Long schoolId, Long schoolYearId, SchoolYear schoolYear, HttpStatus expectedCode, String msg) {
      //Create the schoolYear
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getSchoolYearEndpoint(schoolId, schoolYearId), 
                null, 
                schoolYear);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public IntegrationBase getServiceBase() {
        return serviceBase;
    }
    
    /**
     * Given an ID and the value submitted to a GET/PUT endpoint, retrieve the schoolYear via GET, validate it, and 
     * return it to the caller.
     * 
     * @param schoolYearId
     * @param submittedSchoolYear
     * @param msg
     * @return
     */
    protected SchoolYear retrieveAndValidateCreatedSchoolYear( Long schoolId, Long schoolYearId, SchoolYear submittedSchoolYear, HttpMethod method, String msg) {
        //Retrieve and validate the created schoolYear
        SchoolYear createdSchoolYear = this.get(schoolId, schoolYearId, msg);
        //Keep a reference to the created schoolYear for later cleanup
        if(!serviceBase.schoolYearsCreated.containsKey(schoolId)) {
            serviceBase.schoolYearsCreated.put(schoolId, new ArrayList<SchoolYear>());
        }
        serviceBase.schoolYearsCreated.get(schoolId).add(createdSchoolYear);
        SchoolYear expectedSchoolYear = generateExpectationSchoolYear(submittedSchoolYear, createdSchoolYear, method);
        Assert.assertEquals(createdSchoolYear, expectedSchoolYear, "Unexpected schoolYear created for case: " + msg);
        
        return createdSchoolYear;
    }
    /**
     * Given a submitted schoolYear object and an schoolYear instance returned by the API after creation,
     * this method returns a new SchoolYear instance that represents the expected state of the submitted 
     * SchoolYear after creation.  The reason that there are differences in the submitted and expected 
     * instances is that there may be system assigned values not in the initially submitted object, for 
     * example, the id property.
     * 
     * @param submitted
     * @param created
     * @return
     */
    protected SchoolYear generateExpectationSchoolYear(SchoolYear submitted, SchoolYear created, HttpMethod method) {
        SchoolYear returnSchoolYear = new SchoolYear(submitted);
        
        if(method == HttpMethod.PATCH) {
            returnSchoolYear.mergePropertiesIfNull(created);
        } else if(null != returnSchoolYear && null == returnSchoolYear.getId()) {
            returnSchoolYear.setId(created.getId());
        }
        
        if(null != returnSchoolYear.getTerms() && !returnSchoolYear.getTerms().isEmpty()) {
            Map<Long, Term> termSetToReturn = new HashMap<>();
            for (Term t : created.getTerms().values()) {
                Term copiedTerm = new Term(t);
                copiedTerm.setId(t.getId());
                termSetToReturn.put(copiedTerm.getId(), copiedTerm);
            }
            returnSchoolYear.setTerms(termSetToReturn);
        }
        return returnSchoolYear;
    }
}
