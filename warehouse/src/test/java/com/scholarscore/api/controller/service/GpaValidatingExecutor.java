package com.scholarscore.api.controller.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.gpa.Gpa;
import com.scholarscore.models.gpa.GpaList;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

/**
 * Created by markroper on 11/24/15.
 */
public class GpaValidatingExecutor {
    private final IntegrationBase serviceBase;

    public GpaValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }

    public Gpa get(Long studentId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getGpaEndpoint(studentId),
                null);
        Gpa gpa = serviceBase.validateResponse(response, new TypeReference<Gpa>(){});
        Assert.assertNotNull(gpa, "Unexpected null section assignment returned for case: " + msg);
        return gpa;
    }

    public void getAll(Long studentId, int numberOfItems, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getGpaEndpoint(studentId) + "/historicals",
                null);
        GpaList gpas = serviceBase.validateResponse(response, new TypeReference<GpaList>(){});
        Assert.assertNotNull(gpas, "Unexpected null term returned for case: " + msg);
        Assert.assertTrue(gpas.size() >= numberOfItems, "Unexpected number of items returned for case: " + msg);
    }

    public void getNegative(Long studentId, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getGpaEndpoint(studentId),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected status code returned while retrieving term: " + msg);
    }

    public Gpa create(Long studentId, Gpa gpa, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.POST,
                serviceBase.getGpaEndpoint(studentId),
                null,
                gpa);
        EntityId sectionAssignmentId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        gpa.setId(sectionAssignmentId.getId());
        Assert.assertNotNull(sectionAssignmentId, "unexpected null section assignment returned from create call for case: " + msg);
        return retrieveAndValidateCreatedGpa(studentId, gpa, HttpMethod.POST, msg);
    }

    public void createNegative(Long studentId, Gpa gpa, HttpStatus expectedCode, String msg) {
        //Attempt to create the term
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.POST,
                serviceBase.getGpaEndpoint(studentId),
                null,
                gpa);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected status code returned for case: " + msg);
    }

    public void delete(Long studentId, Long gpaId, String msg) {
        //Delete the term
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.DELETE,
                serviceBase.getGpaEndpoint(studentId, gpaId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(studentId, HttpStatus.NOT_FOUND, msg);
    }

    public void deleteNegative(Long studentId, Long gpaId, HttpStatus expectedCode, String msg) {
        //Attempt to delete the term
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.DELETE,
                serviceBase.getGpaEndpoint(studentId, gpaId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }

    public Gpa replace(Long studentId, Long gpaId, Gpa gpa, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT,
                serviceBase.getGpaEndpoint(studentId, gpaId),
                null,
                gpa);
        EntityId entityId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(entityId, "unexpected null sction assignment ID returned from create call for case: " + msg);
        return retrieveAndValidateCreatedGpa(studentId, gpa, HttpMethod.PUT, msg);
    }

    public void replaceNegative(Long studentId, Long gpaId, Gpa gpa, HttpStatus expectedCode, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT,
                serviceBase.getGpaEndpoint(studentId, gpaId),
                null,
                gpa);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public Gpa update(Long studentId, Long gpaId, Gpa gpa, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH,
                serviceBase.getGpaEndpoint(studentId, gpaId),
                null,
                gpa);
        EntityId entityId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(entityId, "unexpected null section assignment ID returned from create call for case: " + msg);
        return retrieveAndValidateCreatedGpa(studentId, gpa, HttpMethod.PATCH, msg);
    }

    public void updateNegative(Long studentId, Long gpaId, Gpa gpa, HttpStatus expectedCode, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH,
                serviceBase.getGpaEndpoint(studentId, gpaId),
                null,
                gpa);
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
    protected Gpa retrieveAndValidateCreatedGpa(Long studentId, Gpa submittedGpa, HttpMethod method, String msg) {

        //Retrieve and validate the created term
        Gpa createdGpa = this.get(studentId, msg);
        Gpa expectedGpa = generateExpectationGpa(submittedGpa, createdGpa, method);
        Assert.assertEquals(createdGpa, expectedGpa, "Unexpected term created for case: " + msg);
        return createdGpa;
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
    protected Gpa generateExpectationGpa(Gpa submitted, Gpa created, HttpMethod method) {
        Gpa returnAssignment = submitted;
        returnAssignment.setId(created.getId());
        return returnAssignment;
    }
}
