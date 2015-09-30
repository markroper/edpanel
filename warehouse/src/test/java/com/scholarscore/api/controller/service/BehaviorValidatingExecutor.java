package com.scholarscore.api.controller.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.EntityId;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import java.util.ArrayList;

/**
 * User: jordan
 * Date: 8/28/15
 * Time: 11:52 PM
 */
public class BehaviorValidatingExecutor {
    private final IntegrationBase serviceBase;

    public BehaviorValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }

    public Behavior get(Long studentId, Long behaviorId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getBehaviorEndpoint(studentId, behaviorId),
                null);
        Behavior behavior = serviceBase.validateResponse(response, new TypeReference<Behavior>(){});
        Assert.assertNotNull(behavior, "Unexpected null behavior returned for case: " + msg);

        return behavior;
    }

    public void getAll(Long studentId, String msg, int numberOfItems) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getBehaviorEndpoint(studentId),
                null);
        ArrayList<Behavior> behaviors = serviceBase.validateResponse(response, new TypeReference<ArrayList<Behavior>>(){});
        Assert.assertNotNull(behaviors, "Unexpected null behavior returned for case: " + msg);
        Assert.assertTrue(behaviors.size() >= numberOfItems, "Unexpected number of items returned for case: " + msg);
    }

    public void getNegative(Long studentId, Long behaviorId, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getBehaviorEndpoint(studentId, behaviorId),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected status code returned while retrieving behavior: " + msg);
    }

    public Behavior create(Long studentId, Behavior behavior, String msg) {
        //Create the behavior
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getBehaviorEndpoint(studentId), null, behavior);
        EntityId returnedBehaviorId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedBehaviorId, "unexpected null ID returned from create call for case: " + msg);
        return retrieveAndValidateCreatedBehavior(studentId, returnedBehaviorId.getId(), behavior, HttpMethod.POST, msg);
    }

    public void createNegative(Long studentId, Behavior behavior, HttpStatus expectedCode, String msg) {
        //Attempt to create the behavior
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getBehaviorEndpoint(studentId), null, behavior);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected status code returned for case: " + msg);
    }

    public void delete(Long studentId, Long behaviorId, String msg) {
        //Delete the behavior
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE,
                serviceBase.getBehaviorEndpoint(studentId, behaviorId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(studentId, behaviorId, HttpStatus.NOT_FOUND, msg);
    }

    public void deleteNegative(Long studentId, Long behaviorId, HttpStatus expectedCode, String msg) {
        //Attempt to delete the behavior
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE,
                serviceBase.getBehaviorEndpoint(studentId, behaviorId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }

    public Behavior replace(Long studentId, Long behaviorId, Behavior behavior, String msg) {
        //Create the behavior
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT,
                serviceBase.getBehaviorEndpoint(studentId, behaviorId),
                null,
                behavior);
        EntityId returnedBehaviorId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedBehaviorId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedBehavior(studentId, behaviorId, behavior, HttpMethod.PUT, msg);
    }

    public void replaceNegative(Long studentId, Long behaviorId, Behavior behavior, HttpStatus expectedCode, String msg) {
        //Create the behavior
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT,
                serviceBase.getBehaviorEndpoint(studentId, behaviorId),
                null,
                behavior);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public Behavior update(Long studentId, Long behaviorId, Behavior behavior, String msg) {
        //Create the behavior
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH,
                serviceBase.getBehaviorEndpoint(studentId, behaviorId),
                null,
                behavior);
        EntityId returnedBehaviorId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedBehaviorId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedBehavior(studentId, returnedBehaviorId.getId(), behavior, HttpMethod.PATCH, msg);
    }

    public void updateNegative(Long studentId, Long behaviorId, Behavior behavior, HttpStatus expectedCode, String msg) {
        //Create the behavior
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH,
                serviceBase.getBehaviorEndpoint(studentId, behaviorId),
                null,
                behavior);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public IntegrationBase getServiceBase() {
        return serviceBase;
    }

    /**
     * Given an ID and the value submitted to a GET/PUT endpoint, retrieve the behavior via GET, validate it, and
     * return it to the caller.
     *
     * @param schoolYearId
     * @param submittedSchoolYear
     * @param msg
     * @return
     */
    protected Behavior retrieveAndValidateCreatedBehavior(Long studentId, Long behaviorId, Behavior submittedBehavior, HttpMethod method, String msg) {
        //Retrieve and validate the created behavior
        Behavior createdBehavior = this.get(studentId, behaviorId, msg);
        Behavior expectedBehavior = generateExpectationBehavior(submittedBehavior, createdBehavior, method);
        Assert.assertEquals(createdBehavior, expectedBehavior, "Unexpected behavior created for case: " + msg);

        return createdBehavior;
    }
    /**
     * Given a submitted behavior object and an behavior instance returned by the API after creation,
     * this method returns a new Behavior instance that represents the expected state of the submitted
     * Behavior after creation.  The reason that there are differences in the submitted and expected
     * instances is that there may be system assigned values not in the initially submitted object, for
     * example, the id property.
     *
     * @param submitted
     * @param created
     * @return
     */
    protected Behavior generateExpectationBehavior(Behavior submitted, Behavior created, HttpMethod method) {
        Behavior returnBehavior = new Behavior(submitted);

        if(method == HttpMethod.PATCH) {
            returnBehavior.mergePropertiesIfNull(created);
        } else if(null == returnBehavior.getId()) {
            returnBehavior.setId(created.getId());
        }
        return returnBehavior;
    }
}
