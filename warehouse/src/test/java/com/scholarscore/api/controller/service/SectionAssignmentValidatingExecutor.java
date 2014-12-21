package com.scholarscore.api.controller.service;

import java.util.ArrayList;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.SectionAssignment;

public class SectionAssignmentValidatingExecutor {
    private final IntegrationBase serviceBase;
    
    public SectionAssignmentValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }
    
    public SectionAssignment get(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long id, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getSectionAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, id), 
                null);
        SectionAssignment section = serviceBase.validateResponse(response, new TypeReference<SectionAssignment>(){});
        Assert.assertNotNull(section, "Unexpected null section assignment returned for case: " + msg);
        
        return section;
    }
    
    public void getAll(Long schoolId, Long schoolYearId, Long termId, Long sectionId, String msg, int numberOfItems) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getSectionAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId), 
                null);
        ArrayList<SectionAssignment> terms = serviceBase.validateResponse(response, new TypeReference<ArrayList<SectionAssignment>>(){});
        Assert.assertNotNull(terms, "Unexpected null term returned for case: " + msg);
        Assert.assertEquals(terms.size(), numberOfItems, "Unexpected number of items returned for case: " + msg);
    }
    
    public void getNegative(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long id, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET, 
                serviceBase.getSectionAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, id),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(), 
                "Unexpected status code returned while retreiving term: " + msg);
    }
    
    public SectionAssignment create(Long schoolId, Long schoolYearId, Long termId, Long sectionId, SectionAssignment sectionAssignment, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getSectionAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId), null, sectionAssignment);
        EntityId sectionAssignmentId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(sectionAssignmentId, "unexpected null section assignment returned from create call for case: " + msg);
        return retrieveAndValidateCreatedSectionAssignment(schoolId, schoolYearId, termId, sectionId, sectionAssignmentId, sectionAssignment, HttpMethod.POST, msg);
    }
    
    public void createNegative(Long schoolId, Long schoolYearId, Long termId, Long sectionId, 
            SectionAssignment sectionAssignment, HttpStatus expectedCode, String msg) {
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

    public SectionAssignment replace(Long schoolId, Long schoolYearId, Long termId, Long sectionId, 
            Long id, SectionAssignment sectionAssignment, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getSectionAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, id), 
                null, 
                sectionAssignment);
        EntityId sectionAssignmentId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(sectionAssignmentId, "unexpected null sction assignment ID returned from create call for case: " + msg);
        return retrieveAndValidateCreatedSectionAssignment(schoolId, schoolYearId, termId, sectionId, 
                sectionAssignmentId, sectionAssignment, HttpMethod.PUT, msg);
    }

    public void replaceNegative(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long id, 
            SectionAssignment sectionAssignment, HttpStatus expectedCode, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getSectionAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, id), 
                null, 
                sectionAssignment);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public SectionAssignment update(Long schoolId, Long schoolYearId, Long termId, Long sectionId, 
            Long id, SectionAssignment sectionAssignment, String msg) {
      //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH, 
                serviceBase.getSectionAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, id), 
                null, 
                sectionAssignment);
        EntityId sectionAssignmentId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(sectionAssignmentId, "unexpected null section assignment ID returned from create call for case: " + msg);
        return retrieveAndValidateCreatedSectionAssignment(schoolId, schoolYearId, termId, sectionId, 
                sectionAssignmentId, sectionAssignment, HttpMethod.PATCH, msg);
    }

    public void updateNegative(Long schoolId, Long schoolYearId, Long termId, Long sectionId, 
            Long id, SectionAssignment sectionAssignment, HttpStatus expectedCode, String msg) {
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
     * @param submittedSectionAssignment
     * @param msg
     * @return
     */
    protected SectionAssignment retrieveAndValidateCreatedSectionAssignment( Long schoolId, Long schoolYearId, Long termId,
            Long sectionId, EntityId id, SectionAssignment submittedSectionAssignment, HttpMethod method, String msg) {
        
        //Retrieve and validate the created term
        SectionAssignment createdSectionAssignment = this.get(schoolId, schoolYearId, termId, sectionId, id.getId(), msg);
        SectionAssignment expectedSectionAssignment = generateExpectationSectionAssignment(submittedSectionAssignment, createdSectionAssignment, method);
        Assert.assertEquals(createdSectionAssignment, expectedSectionAssignment, "Unexpected term created for case: " + msg);
        
        return createdSectionAssignment;
    }
    /**
     * Given a submitted section assignment object and a section assignment instance returned by the API after creation,
     * this method returns a new SectionAssignment instance that represents the expected state of the submitted 
     * SectionAssignment after creation.  The reason that there are differences in the submitted and expected 
     * instances is that there may be system assigned values not in the initially submitted object, for 
     * example, the id property.
     * 
     * @param submitted
     * @param created
     * @return
     */
    protected SectionAssignment generateExpectationSectionAssignment(SectionAssignment submitted, SectionAssignment created, HttpMethod method) {
        SectionAssignment returnSectionAssignment = new SectionAssignment(submitted);
        if(method == HttpMethod.PATCH) {
            returnSectionAssignment.mergePropertiesIfNull(created);
        } else if(null != returnSectionAssignment && null == returnSectionAssignment.getId()) {
            returnSectionAssignment.setId(created.getId());
        }
        returnSectionAssignment.setSectionId(created.getSectionId());
        return returnSectionAssignment;
    }
}
