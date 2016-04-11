package com.scholarscore.api.controller.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.state.ma.McasResult;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import java.util.List;

/**
 * Created by markroper on 4/10/16.
 */
public class McasValidatingExecutor {
    private final IntegrationBase serviceBase;

    public McasValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }

    public McasResult get(Long schoolId, Long studentId, Long mcasId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getMcasEndpoint(schoolId, studentId, mcasId),
                null);
        McasResult mcas = serviceBase.validateResponse(response, new TypeReference<McasResult>(){});
        Assert.assertNotNull(mcas, "Unexpected null section assignment returned for case: " + msg);
        return mcas;
    }

    public void getAll(Long schoolId, Long studentId, int numberOfItems, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getMcasEndpoint(schoolId, studentId),
                null);
        List<McasResult> mcass = serviceBase.validateResponse(response, new TypeReference<List<McasResult>>(){});
        Assert.assertNotNull(mcass, "Unexpected null term returned for case: " + msg);
        Assert.assertTrue(mcass.size() >= numberOfItems, "Unexpected number of items returned for case: " + msg);
    }

    public void getNegative(Long schoolId, Long studentId, Long mcasId, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getMcasEndpoint(schoolId, studentId, mcasId),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected status code returned while retrieving term: " + msg);
    }

    public McasResult create(Long schoolId, Long studentId, McasResult mcas, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.POST,
                serviceBase.getMcasEndpoint(schoolId, studentId),
                null,
                mcas);
        EntityId sectionAssignmentId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        mcas.setId(sectionAssignmentId.getId());
        Assert.assertNotNull(sectionAssignmentId, "unexpected null section assignment returned from create call for case: " + msg);
        return retrieveAndValidateCreatedMcas(schoolId, studentId, sectionAssignmentId.getId(), mcas, HttpMethod.POST, msg);
    }

    public void createNegative(Long schoolId, Long studentId, McasResult mcas, HttpStatus expectedCode, String msg) {
        //Attempt to create the term
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.POST,
                serviceBase.getMcasEndpoint(schoolId, studentId),
                null,
                mcas);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected status code returned for case: " + msg);
    }

    public void delete(Long schoolId, Long studentId, Long mcasId, String msg) {
        //Delete the term
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.DELETE,
                serviceBase.getMcasEndpoint(schoolId, studentId, mcasId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(schoolId, studentId, mcasId, HttpStatus.NOT_FOUND, msg);
    }

    public void deleteNegative(Long schoolId, Long studentId, Long mcasId, HttpStatus expectedCode, String msg) {
        //Attempt to delete the term
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.DELETE,
                serviceBase.getMcasEndpoint(schoolId, studentId, mcasId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }

    public McasResult replace(Long schoolId, Long studentId, Long mcasId, McasResult mcas, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT,
                serviceBase.getMcasEndpoint(schoolId, studentId, mcasId),
                null,
                mcas);
        Void voidResp = serviceBase.validateResponse(response, new TypeReference<Void>(){});
        return retrieveAndValidateCreatedMcas(schoolId, studentId, mcasId, mcas, HttpMethod.PUT, msg);
    }

    public void replaceNegative(Long schoolId, Long studentId, Long mcasId, McasResult mcas, HttpStatus expectedCode, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT,
                serviceBase.getMcasEndpoint(schoolId, studentId, mcasId),
                null,
                mcas);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public McasResult update(Long schoolId, Long studentId, Long mcasId, McasResult mcas, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH,
                serviceBase.getMcasEndpoint(schoolId, studentId, mcasId),
                null,
                mcas);
        EntityId entityId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(entityId, "unexpected null section assignment ID returned from create call for case: " + msg);
        return retrieveAndValidateCreatedMcas(schoolId, studentId, mcasId, mcas, HttpMethod.PATCH, msg);
    }

    public void updateNegative(Long schoolId, Long studentId, Long mcasId, McasResult mcas, HttpStatus expectedCode, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH,
                serviceBase.getMcasEndpoint(schoolId, studentId, mcasId),
                null,
                mcas);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public IntegrationBase getServiceBase() {
        return serviceBase;
    }

    /**
     * Given an ID and the value submitted to a GET/PUT endpoint, retrieve the term via GET, validate it, and
     * return it to the caller.
     * @param msg
     * @return
     */
    protected McasResult retrieveAndValidateCreatedMcas(Long schoolId, Long studentId, Long mcasId,
                                                        McasResult submittedMcas, HttpMethod method, String msg) {

        //Retrieve and validate the created term
        McasResult createdMcas = this.get(schoolId, studentId, mcasId, msg);
        McasResult expectedMcas = generateExpectationMcas(submittedMcas, createdMcas, method);
        Assert.assertEquals(createdMcas, expectedMcas, "Unexpected term created for case: " + msg);
        return createdMcas;
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
    protected McasResult generateExpectationMcas(McasResult submitted, McasResult created, HttpMethod method) {
        McasResult returnAssignment = submitted;
        returnAssignment.setId(created.getId());
        if(null != submitted.getEnglishScore()) {
            submitted.getEnglishScore().setId(created.getEnglishScore().getId());
        }
        if(null != submitted.getMathScore()) {
            submitted.getMathScore().setId(created.getMathScore().getId());
        }
        if(null != submitted.getScienceScore()) {
            submitted.getScienceScore().setId(created.getScienceScore().getId());
        }
        return returnAssignment;
    }
}
