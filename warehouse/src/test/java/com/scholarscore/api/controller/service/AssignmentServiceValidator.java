package com.scholarscore.api.controller.service;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Assignment;
import com.scholarscore.models.EntityId;

import factory.AssignmentFactory;

public class AssignmentServiceValidator implements IServiceValidator<Assignment>{

    private final IntegrationBase serviceBase;
    
    public AssignmentServiceValidator(IntegrationBase sb) {
        this.serviceBase = sb;
    }
    
    @Override
    public Assignment get(Long id, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getAssignmentEndpoint() + Long.toString(id), 
                null);
        Assignment assignment = serviceBase.validateResponse(response, new TypeReference<Assignment>(){});
        Assert.assertNotNull(assignment, "Unexpected null assignment returned for case: " + msg);
        
        return assignment;
    }
    
    @Override
    public Assignment create(Assignment assignment, String msg) {
        //Create the assignment
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getAssignmentEndpoint(), null, assignment);
        EntityId assignmentId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(assignmentId, "unexpected null app returned from create call for case: " + msg);
      
        //Retrieve and validate the created assignment
        Assignment createdAssignment = this.get(assignmentId.getId(), msg);
        //Keep a reference to the created assignment for later cleanup
        serviceBase.assignmentsCreated.add(createdAssignment);
        Assignment expectedAssignment = generateExpectationAssignment(assignment, createdAssignment);
        Assert.assertEquals(createdAssignment, expectedAssignment, "Unexpected assignment created for case: " + msg);
        
        return createdAssignment;
    }
    
    @Override
    public void createNegative(Assignment assignment, HttpStatus expectedCode, String msg) {
        
    }
    
    @Override
    public void delete(Long assignment, String msg) {
        
    }
    
    @Override
    public void deleteNegative(Long assignmentId, HttpStatus expectedCode, String msg) {
        
    }

    @Override
    public Assignment replace(Long id, Assignment entity, String msg) {
        return null;
    }

    @Override
    public void replaceNegative(Long id, Assignment entity,
            HttpStatus expectedCode, String msg) {
        
    }

    @Override
    public Assignment update(Long id, Assignment entity, String msg) {
        return null;
    }

    @Override
    public void updateNegative(Long id, Assignment entity,
            HttpStatus expectedCode, String msg) {
    }

    public IntegrationBase getServiceBase() {
        return serviceBase;
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
    protected Assignment generateExpectationAssignment(Assignment submitted, Assignment created) {
        Assignment returnAssignment = AssignmentFactory.cloneAssignment(submitted);
        if(null != returnAssignment && null == returnAssignment.getId()) {
            returnAssignment.setId(created.getId());
        }
        return returnAssignment;
    }
}
