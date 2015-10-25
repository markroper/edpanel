package com.scholarscore.api.controller.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.assignment.StudentAssignment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import java.util.ArrayList;

public class StudentAssignmentValidatingExecutor {
    private final IntegrationBase serviceBase;
    
    public StudentAssignmentValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }
    
    public StudentAssignment get(Long schoolId, Long schoolYearId, Long termId, Long sectionId, 
            Long sectionAssignmentId, Long id, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getStudentAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, sectionAssignmentId, id), 
                null);
        StudentAssignment section = serviceBase.validateResponse(response, new TypeReference<StudentAssignment>(){});
        Assert.assertNotNull(section, "Unexpected null section assignment returned for case: " + msg);
        
        return section;
    }
    
    public void getAll(Long schoolId, Long schoolYearId, Long termId, Long sectionId, 
            Long sectionAssignmentId, String msg, int numberOfItems) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getStudentAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, sectionAssignmentId), 
                null);
        ArrayList<StudentAssignment> terms = serviceBase.validateResponse(response, new TypeReference<ArrayList<StudentAssignment>>(){});
        Assert.assertNotNull(terms, "Unexpected null term returned for case: " + msg);
        Assert.assertTrue(terms.size() >= numberOfItems, "Unexpected number of items returned for case: " + msg);
    }
    
    public void getNegative(Long schoolId, Long schoolYearId, Long termId, Long sectionId, 
            Long sectionAssignmentId, Long id, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getStudentAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, sectionAssignmentId, id),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned while retrieving term: " + msg);
    }
    
    public StudentAssignment create(Long schoolId, Long schoolYearId, Long termId, Long sectionId, 
            Long sectionAssignmentId, StudentAssignment studentAssignment, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getStudentAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, sectionAssignmentId), null, studentAssignment);
        EntityId studentAssignmentId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(studentAssignmentId, "unexpected null section assignment returned from create call for case: " + msg);
        return retrieveAndValidateCreatedStudentAssignment(schoolId, schoolYearId, termId, sectionId, sectionAssignmentId, studentAssignmentId, studentAssignment, HttpMethod.POST, msg);
    }
    
    public void createNegative(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long sectionAssignmentId,
            StudentAssignment studentAssignment, HttpStatus expectedCode, String msg) {
        //Attempt to create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getStudentAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, sectionAssignmentId), null, studentAssignment);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned for case: " + msg);
    }
    
    public void delete(Long schoolId, Long schoolYearId, Long termId, Long sectionId, 
            Long sectionAssignmentId, Long id, String msg) {
        //Delete the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getStudentAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, sectionAssignmentId, id));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(schoolId, schoolYearId, termId, sectionId, sectionAssignmentId, id, HttpStatus.NOT_FOUND, msg);
    }
    
    public void deleteNegative(Long schoolId, Long schoolYearId, Long termId, Long sectionId, 
            Long sectionAssignmentId, Long id, HttpStatus expectedCode, String msg) {
        //Attempt to delete the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE, 
                serviceBase.getStudentAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, sectionAssignmentId, id));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }

    public StudentAssignment replace(Long schoolId, Long schoolYearId, Long termId, Long sectionId, 
            Long sectionAssignmentId, Long id, StudentAssignment studentAssignment, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getStudentAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, sectionAssignmentId, id), 
                null, 
                studentAssignment);
        EntityId studentAssignmentId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(studentAssignmentId, "unexpected null sction assignment ID returned from create call for case: " + msg);
        return retrieveAndValidateCreatedStudentAssignment(schoolId, schoolYearId, termId, sectionId, sectionAssignmentId, 
                studentAssignmentId, studentAssignment, HttpMethod.PUT, msg);
    }

    public void replaceNegative(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long sectionAssignmentId, 
            Long id, StudentAssignment studentAssignment, HttpStatus expectedCode, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getStudentAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, sectionAssignmentId, id), 
                null, 
                studentAssignment);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public StudentAssignment update(Long schoolId, Long schoolYearId, Long termId, Long sectionId, 
            Long sectionAssignmentId, Long id, StudentAssignment studentAssignment, String msg) {
      //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getStudentAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, sectionAssignmentId, id), 
                null, 
                studentAssignment);
        EntityId studentAssignmentId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(studentAssignmentId, "unexpected null section assignment ID returned from create call for case: " + msg);
        return retrieveAndValidateCreatedStudentAssignment(schoolId, schoolYearId, termId, sectionId, sectionAssignmentId, 
                studentAssignmentId, studentAssignment, HttpMethod.PATCH, msg);
    }

    public void updateNegative(Long schoolId, Long schoolYearId, Long termId, Long sectionId, 
            Long sectionAssignmentId, Long id, StudentAssignment studentAssignment, HttpStatus expectedCode, String msg) {
      //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getStudentAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, sectionAssignmentId, id), 
                null, 
                studentAssignment);
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
     * @param submittedStudentAssignment
     * @param msg
     * @return
     */
    protected StudentAssignment retrieveAndValidateCreatedStudentAssignment( Long schoolId, Long schoolYearId, Long termId,
            Long sectionId, Long sectionAssignmentId, EntityId id, StudentAssignment submittedStudentAssignment, 
            HttpMethod method, String msg) {
        
        //Retrieve and validate the created term
        StudentAssignment createdStudentAssignment = this.get(schoolId, schoolYearId, termId, sectionId, sectionAssignmentId, id.getId(), msg);
        StudentAssignment expectedStudentAssignment = generateExpectationStudentAssignment(submittedStudentAssignment, createdStudentAssignment, method);
        Assert.assertEquals(createdStudentAssignment, expectedStudentAssignment, "Unexpected term created for case: " + msg);
        
        return createdStudentAssignment;
    }
    /**
     * Given a submitted section assignment object and a section assignment instance returned by the API after creation,
     * this method returns a new StudentAssignment instance that represents the expected state of the submitted 
     * StudentAssignment after creation.  The reason that there are differences in the submitted and expected 
     * instances is that there may be system assigned values not in the initially submitted object, for 
     * example, the id property.
     * 
     * @param submitted
     * @param created
     * @return
     */
    protected StudentAssignment generateExpectationStudentAssignment(StudentAssignment submitted, 
            StudentAssignment created, HttpMethod method) {
        StudentAssignment returnStudentAssignment = new StudentAssignment(submitted);
        if(method == HttpMethod.PATCH) {
            returnStudentAssignment.mergePropertiesIfNull(created);
        } 
        returnStudentAssignment.setId(created.getId());
        return returnStudentAssignment;
    }
}
