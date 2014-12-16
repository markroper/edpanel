package com.scholarscore.api.controller.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
public class AssignmentServiceValidatingExecutor {//implements IServiceValidatingExecutor<Assignment>{

    private final IntegrationBase serviceBase;
    
    public AssignmentServiceValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }
    
    public Assignment get(Long schoolId, Long courseId, Long id, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getAssignmentEndpoint(schoolId, courseId, id),// + "/" + Long.toString(id), 
                null);
        Assignment assignment = serviceBase.validateResponse(response, new TypeReference<Assignment>(){});
        Assert.assertNotNull(assignment, "Unexpected null assignment returned for case: " + msg);
        
        return assignment;
    }
    
    public void getAll(Long schoolId, Long courseId, String msg, int numberOfItems) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getAssignmentEndpoint(schoolId, courseId), 
                null);
        ArrayList<Assignment> assignments = serviceBase.validateResponse(response, new TypeReference<ArrayList<Assignment>>(){});
        Assert.assertNotNull(assignments, "Unexpected null assignment returned for case: " + msg);
        Assert.assertEquals(assignments.size(), numberOfItems, "Unexpected number of items returned for case: " + msg);
    }
    
    public void getNegative(Long schoolId, Long courseId, Long id, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getAssignmentEndpoint(schoolId, courseId, id),// + "/" + Long.toString(id), 
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned while retreiving assignment: " + msg);
    }
    
    public Assignment create(Long schoolId, Long courseId, Assignment assignment, String msg) {
        //Create the assignment
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getAssignmentEndpoint(schoolId, courseId), null, assignment);
        EntityId assignmentId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(assignmentId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedAssignment(schoolId, courseId, assignmentId, assignment, HttpMethod.POST, msg);
    }
    
    public void createNegative(Long schoolId, Long courseId, Assignment assignment, HttpStatus expectedCode, String msg) {
        //Attempt to create the assignment
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getAssignmentEndpoint(schoolId, courseId), null, assignment);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned for case: " + msg);
    }
    
    public void delete(Long schoolId, Long courseId, Long assignmentId, String msg) {
        //Delete the assignment
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getAssignmentEndpoint(schoolId, courseId, assignmentId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(schoolId, courseId, assignmentId, HttpStatus.NOT_FOUND, msg);
    }
    
    public void deleteNegative(Long schoolId, Long courseId, Long assignmentId, HttpStatus expectedCode, String msg) {
        //Attempt to delete the assignment
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getAssignmentEndpoint(schoolId, courseId, assignmentId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }

    public Assignment replace(Long schoolId, Long courseId, Long id, Assignment assignment, String msg) {
        //Create the assignment
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getAssignmentEndpoint(schoolId, courseId, id), 
                null, 
                assignment);
        EntityId assignmentId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(assignmentId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedAssignment(schoolId, courseId, assignmentId, assignment, HttpMethod.PUT, msg);
    }

    public void replaceNegative(Long schoolId, Long courseId, Long id, Assignment assignment, HttpStatus expectedCode, String msg) {
        //Create the assignment
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getAssignmentEndpoint(schoolId, courseId, id), 
                null, 
                assignment);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public Assignment update(Long schoolId, Long courseId, Long id, Assignment assignment, String msg) {
      //Create the assignment
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getAssignmentEndpoint(schoolId, courseId, id), 
                null, 
                assignment);
        EntityId assignmentId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(assignmentId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedAssignment(schoolId, courseId, assignmentId, assignment, HttpMethod.PATCH, msg);
    }

    public void updateNegative(Long schoolId, Long courseId, Long id, Assignment assignment, HttpStatus expectedCode, String msg) {
      //Create the assignment
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getAssignmentEndpoint(schoolId, courseId, id), 
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
    protected Assignment retrieveAndValidateCreatedAssignment( Long schoolId, Long courseId,
            EntityId assignmentId, Assignment submittedAssignment, HttpMethod method, String msg) {
        //Retrieve and validate the created assignment
        Assignment createdAssignment = this.get(schoolId, courseId, assignmentId.getId(), msg);
        //Keep a reference to the created assignment for later cleanup
        if(!serviceBase.assignmentsCreated.containsKey(schoolId)) {
            serviceBase.assignmentsCreated.put(schoolId, new HashMap<Long, List<Assignment>>());
        }
        if(!serviceBase.assignmentsCreated.get(schoolId).containsKey(courseId)) {
            serviceBase.assignmentsCreated.get(schoolId).put(courseId, new ArrayList<Assignment>());
        }
        serviceBase.assignmentsCreated.get(schoolId).get(courseId).add(createdAssignment);
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
