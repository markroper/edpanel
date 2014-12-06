package com.scholarscore.api.controller.service;

import java.util.ArrayList;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Assignment;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.factory.AssignmentFactory;

/**
 * Makes and validates API CRUD requests for Assignments (/warehouse/api/v1/assignment/{id})
 * 
 * @author markroper
 * @see IServiceValidatingExecutor
 */
public class AssignmentServiceValidatingExecutor implements IServiceValidatingExecutor<Assignment>{

    private final IntegrationBase serviceBase;
    
    public AssignmentServiceValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }
    
    @Override
    public Assignment get(Long id, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getAssignmentEndpoint() + "/" + Long.toString(id), 
                null);
        Assignment assignment = serviceBase.validateResponse(response, new TypeReference<Assignment>(){});
        Assert.assertNotNull(assignment, "Unexpected null assignment returned for case: " + msg);
        
        return assignment;
    }
    
    public void getAll(String msg, int numberOfItems) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getAssignmentEndpoint(), 
                null);
        ArrayList<Assignment> assignments = serviceBase.validateResponse(response, new TypeReference<ArrayList<Assignment>>(){});
        Assert.assertNotNull(assignments, "Unexpected null assignment returned for case: " + msg);
        Assert.assertEquals(assignments.size(), numberOfItems, "Unexpected number of items returned for case: " + msg);
    }
    
    @Override
    public void getNegative(Long id, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getAssignmentEndpoint() + "/" + Long.toString(id), 
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned while retreiving assignment: " + msg);
    }
    
    @Override
    public Assignment create(Assignment assignment, String msg) {
        //Create the assignment
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getAssignmentEndpoint(), null, assignment);
        EntityId assignmentId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(assignmentId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedAssignment(assignmentId, assignment, HttpMethod.POST, msg);
    }
    
    @Override
    public void createNegative(Assignment assignment, HttpStatus expectedCode, String msg) {
        //Attempt to create the assignment
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getAssignmentEndpoint(), null, assignment);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned for case: " + msg);
    }
    
    @Override
    public void delete(Long assignmentId, String msg) {
        //Delete the assignment
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getAssignmentEndpoint(assignmentId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(assignmentId, HttpStatus.NOT_FOUND, msg);
    }
    
    @Override
    public void deleteNegative(Long assignmentId, HttpStatus expectedCode, String msg) {
        //Attempt to delete the assignment
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getAssignmentEndpoint(assignmentId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }

    @Override
    public Assignment replace(Long id, Assignment assignment, String msg) {
        //Create the assignment
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getAssignmentEndpoint(id), 
                null, 
                assignment);
        EntityId assignmentId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(assignmentId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedAssignment(assignmentId, assignment, HttpMethod.PUT, msg);
    }

    @Override
    public void replaceNegative(Long id, Assignment assignment, HttpStatus expectedCode, String msg) {
        //Create the assignment
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getAssignmentEndpoint(id), 
                null, 
                assignment);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    @Override
    public Assignment update(Long id, Assignment assignment, String msg) {
      //Create the assignment
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getAssignmentEndpoint(id), 
                null, 
                assignment);
        EntityId assignmentId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(assignmentId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedAssignment(assignmentId, assignment, HttpMethod.PATCH, msg);
    }

    @Override
    public void updateNegative(Long id, Assignment assignment, HttpStatus expectedCode, String msg) {
      //Create the assignment
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getAssignmentEndpoint(id), 
                null, 
                assignment);
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
     * @param assignmentId
     * @param submittedAssignment
     * @param msg
     * @return
     */
    protected Assignment retrieveAndValidateCreatedAssignment(
            EntityId assignmentId, Assignment submittedAssignment, HttpMethod method, String msg) {
        //Retrieve and validate the created assignment
        Assignment createdAssignment = this.get(assignmentId.getId(), msg);
        //Keep a reference to the created assignment for later cleanup
        serviceBase.assignmentsCreated.add(createdAssignment);
        Assignment expectedAssignment = generateExpectationAssignment(submittedAssignment, createdAssignment, method);
        Assert.assertEquals(createdAssignment, expectedAssignment, "Unexpected assignment created for case: " + msg);
        
        return createdAssignment;
    }
    /**
     * Given a submitted assignment object and an assignment instance returned by the API after creation,
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
