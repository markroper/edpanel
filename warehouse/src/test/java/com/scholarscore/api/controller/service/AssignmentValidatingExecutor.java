package com.scholarscore.api.controller.service;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.AssignmentList;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.Assignment;
import com.scholarscore.models.factory.AssignmentFactory;

public class AssignmentValidatingExecutor {
    private final IntegrationBase serviceBase;
    
    public AssignmentValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }
    
    public Assignment get(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long id, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getSectionAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, id), 
                null);
        Assignment section = serviceBase.validateResponse(response, new TypeReference<Assignment>(){});
        Assert.assertNotNull(section, "Unexpected null section assignment returned for case: " + msg);
        
        return section;
    }
    
    public void getAll(Long schoolId, Long schoolYearId, Long termId, Long sectionId, String msg, int numberOfItems) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getSectionAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId), 
                null);
        AssignmentList terms = serviceBase.validateResponse(response, new TypeReference<AssignmentList>(){});
        Assert.assertNotNull(terms, "Unexpected null term returned for case: " + msg);
        Assert.assertTrue(terms.size() >= numberOfItems, "Unexpected number of items returned for case: " + msg);
    }
    
    public void getNegative(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long id, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getSectionAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, id),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned while retrieving term: " + msg);
    }
    
    public Assignment create(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Assignment sectionAssignment, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getSectionAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId), null, sectionAssignment);
        EntityId sectionAssignmentId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(sectionAssignmentId, "unexpected null section assignment returned from create call for case: " + msg);
        return retrieveAndValidateCreatedAssignment(schoolId, schoolYearId, termId, sectionId, sectionAssignmentId, sectionAssignment, HttpMethod.POST, msg);
    }
    
    public void createNegative(Long schoolId, Long schoolYearId, Long termId, Long sectionId, 
            Assignment sectionAssignment, HttpStatus expectedCode, String msg) {
        //Attempt to create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getSectionAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId), null, sectionAssignment);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned for case: " + msg);
    }
    
    public void delete(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long id, String msg) {
        //Delete the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getSectionAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, id));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(schoolId, schoolYearId, termId, sectionId, id, HttpStatus.NOT_FOUND, msg);
    }
    
    public void deleteNegative(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long id, HttpStatus expectedCode, String msg) {
        //Attempt to delete the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getSectionAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, id));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }

    public Assignment replace(Long schoolId, Long schoolYearId, Long termId, Long sectionId, 
            Long id, Assignment sectionAssignment, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getSectionAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, id), 
                null, 
                sectionAssignment);
        EntityId sectionAssignmentId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(sectionAssignmentId, "unexpected null sction assignment ID returned from create call for case: " + msg);
        return retrieveAndValidateCreatedAssignment(schoolId, schoolYearId, termId, sectionId, 
                sectionAssignmentId, sectionAssignment, HttpMethod.PUT, msg);
    }

    public void replaceNegative(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long id, 
            Assignment sectionAssignment, HttpStatus expectedCode, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getSectionAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, id), 
                null, 
                sectionAssignment);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public Assignment update(Long schoolId, Long schoolYearId, Long termId, Long sectionId, 
            Long id, Assignment sectionAssignment, String msg) {
      //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getSectionAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, id), 
                null, 
                sectionAssignment);
        EntityId sectionAssignmentId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(sectionAssignmentId, "unexpected null section assignment ID returned from create call for case: " + msg);
        return retrieveAndValidateCreatedAssignment(schoolId, schoolYearId, termId, sectionId, 
                sectionAssignmentId, sectionAssignment, HttpMethod.PATCH, msg);
    }

    public void updateNegative(Long schoolId, Long schoolYearId, Long termId, Long sectionId, 
            Long id, Assignment sectionAssignment, HttpStatus expectedCode, String msg) {
      //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getSectionAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, id), 
                null, 
                sectionAssignment);
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
     * @param submittedAssignment
     * @param msg
     * @return
     */
    protected Assignment retrieveAndValidateCreatedAssignment( Long schoolId, Long schoolYearId, Long termId,
            Long sectionId, EntityId id, Assignment submittedAssignment, HttpMethod method, String msg) {
        
        //Retrieve and validate the created term
        Assignment createdAssignment = this.get(schoolId, schoolYearId, termId, sectionId, id.getId(), msg);
        Assignment expectedAssignment = generateExpectationAssignment(submittedAssignment, createdAssignment, method);
        Assert.assertEquals(createdAssignment.getName(), expectedAssignment.getName(), "Unexpected term created for case: " + msg);
        Assert.assertEquals(createdAssignment.getAvailablePoints(), expectedAssignment.getAvailablePoints(), "Unexpected term created for case: " + msg);
        Assert.assertEquals(createdAssignment.getId(), expectedAssignment.getId(), "Unexpected term created for case: " + msg);
        Assert.assertEquals(createdAssignment.getType(), expectedAssignment.getType(), "Unexpected term created for case: " + msg);

        
        return createdAssignment;
    }
    /**
     * Given a submitted section assignment object and a section assignment instance returned by the API after creation,
     * this method returns a new Assignment instance that represents the expected state of the submitted 
     * Assignment after creation.  The reason that there are differences in the submitted and expected 
     * instances is that there may be system assigned values not in the initially submitted object, for 
     * example, the id property.
     * 
     * @param submitted
     * @param created
     * @return
     */
    protected Assignment generateExpectationAssignment(Assignment submitted, Assignment created, HttpMethod method) {
        Assignment returnAssignment = AssignmentFactory.cloneAssignment(submitted);
        if(method == HttpMethod.PATCH) {
            returnAssignment.mergePropertiesIfNull(created);
        } else if(null != returnAssignment && null == returnAssignment.getId()) {
            returnAssignment.setId(created.getId());
        }
        return returnAssignment;
    }
}
