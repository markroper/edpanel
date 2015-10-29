package com.scholarscore.api.controller.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.School;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import java.util.ArrayList;

public class SchoolValidatingExecutor {
    private final IntegrationBase serviceBase;
    
    public SchoolValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }
    
    public School get(Long schoolId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getSchoolEndpoint(schoolId),
                null);
        School school = serviceBase.validateResponse(response, new TypeReference<School>(){});
        Assert.assertNotNull(school, "Unexpected null school returned for case: " + msg);
        
        return school;
    }
    
    public void getAll(String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getSchoolEndpoint(), 
                null);
        ArrayList<School> schools = serviceBase.validateResponse(response, new TypeReference<ArrayList<School>>(){});
        Assert.assertNotNull(schools, "Unexpected null school returned for case: " + msg);
    }

    public void getAllNegative(HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getSchoolEndpoint(),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected status code returned while retrieving all schools: " + msg);
    }

    public void getAllOptions(String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.OPTIONS,
                serviceBase.getSchoolEndpoint(),
                null);
        serviceBase.validateResponse(response, new TypeReference<ArrayList<School>>(){});
    }
    
    public void getNegative(Long schoolId, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getSchoolEndpoint(schoolId),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned while retrieving school: " + msg);
    }
    
    
    public School create(School school, String msg) {
        //Create the school
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getSchoolEndpoint(), null, school);
        EntityId schoolId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(schoolId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedSchool(schoolId.getId(), school, HttpMethod.POST, msg);
    }
    
    public void createNegative(School school, HttpStatus expectedCode, String msg) {
        //Attempt to create the school
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getSchoolEndpoint(), null, school);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned for case: " + msg);
    }
    
    public void delete(Long schoolId, String msg) {
        //Delete the school
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getSchoolEndpoint(schoolId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(schoolId, HttpStatus.NOT_FOUND, msg);
    }
    
    public void deleteNegative(Long schoolId, HttpStatus expectedCode, String msg) {
        //Attempt to delete the school
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getSchoolEndpoint(schoolId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }

    public School replace(Long schoolId, School school, String msg) {
        //Create the school
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getSchoolEndpoint(schoolId), 
                null, 
                school);
        EntityId returnedSchoolId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedSchoolId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedSchool(schoolId, school, HttpMethod.PUT, msg);
    }

    public void replaceNegative(Long schoolId, School school, HttpStatus expectedCode, String msg) {
        //Create the school
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getSchoolEndpoint(schoolId), 
                null, 
                school);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public School update(Long schoolId, School school, String msg) {
      //Create the school
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getSchoolEndpoint(schoolId), 
                null, 
                school);
        EntityId returnedSchoolId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedSchoolId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedSchool(schoolId, school, HttpMethod.PATCH, msg);
    }

    public void updateNegative(Long schoolId, Long courseId, Long id, School school, HttpStatus expectedCode, String msg) {
      //Create the school
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getSchoolEndpoint(schoolId), 
                null, 
                school);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public IntegrationBase getServiceBase() {
        return serviceBase;
    }
    
    /**
     * Given an ID and the value submitted to a GET/PUT endpoint, retrieve the assignment via GET, validate it, and 
     * return it to the caller.
     * 
     * @param schoolId
     * @param submittedSchool
     * @param msg
     * @return
     */
    protected School retrieveAndValidateCreatedSchool( Long schoolId, School submittedSchool, HttpMethod method, String msg) {
        //Retrieve and validate the created assignment
        School createdSchool = this.get(schoolId, msg);
        //Keep a reference to the created assignment for later cleanup
        serviceBase.schoolsCreated.add(createdSchool);
        School expectedSchool = generateExpectationSchool(submittedSchool, createdSchool, method);
        Assert.assertEquals(createdSchool, expectedSchool, "Unexpected assignment created for case: " + msg);
        
        return createdSchool;
    }
    /**
     * Given a submitted assignment object and an assignment instance returned by the API after creation,
     * this method returns a new School instance that represents the expected state of the submitted 
     * School after creation.  The reason that there are differences in the submitted and expected 
     * instances is that there may be system assigned values not in the initially submitted object, for 
     * example, the id property.
     * 
     * @param submitted
     * @param created
     * @return
     */
    protected School generateExpectationSchool(School submitted, School created, HttpMethod method) {
        School returnSchool = new School(submitted);
        if(method == HttpMethod.PATCH) {
            returnSchool.mergePropertiesIfNull(created);
        } else if(null != returnSchool && null == returnSchool.getId()) {
            returnSchool.setId(created.getId());
        }
        return returnSchool;
    }
}
