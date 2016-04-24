package com.scholarscore.api.controller.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.StudentWatch;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import java.util.ArrayList;

/**
 * Created by cwallace on 4/19/16.
 */
public class WatchValidatingExecutor {
    private final IntegrationBase serviceBase;

    public WatchValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }

    public StudentWatch create(StudentWatch watch, String msg) {
        //Create the behavior
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getWatchEndpoint(), null, watch);
        EntityId returnedWatchId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedWatchId, "unexpected null ID returned from create call for case: " + msg);
        return retrieveAndValidateCreatedWatch(returnedWatchId.getId(), watch, HttpMethod.POST, msg);
    }

    public StudentWatch get(Long watchId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getWatchEndpoint(watchId),
                null);
        StudentWatch watch = serviceBase.validateResponse(response, new TypeReference<StudentWatch>() {
        });
        Assert.assertNotNull(watch, "Unexpected null behavior returned for case: " + msg);

        return watch;
    }

    public void delete(Long watchId, String msg) {
        //Delete the behavior
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE,
                serviceBase.getWatchEndpoint(watchId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
    }

    public void getAllStaff(Long staffId, String msg, int numberOfItems) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getStaffWatchEndpoint(staffId),
                null);
        ArrayList<StudentWatch> watches = serviceBase.validateResponse(response, new TypeReference<ArrayList<StudentWatch>>() {
        });
        Assert.assertNotNull(watches, "Unexpected null behavior returned for case: " + msg);
        Assert.assertEquals(watches.size(), numberOfItems, "Unexpected number of items returned for case: " + msg);
    }

    /**
     * Given an ID and the value submitted to a GET/PUT endpoint, retrieve the behavior via GET, validate it, and
     * return it to the caller.
     * @param msg
     * @return
     */
    protected StudentWatch retrieveAndValidateCreatedWatch(Long watchId, StudentWatch submittedWatch, HttpMethod method, String msg) {
        //Retrieve and validate the created behavior
        StudentWatch createdWatch = this.get(watchId, msg);
        StudentWatch expectedWatch = generateExpectationWatch(submittedWatch, createdWatch, method);
        Assert.assertEquals(createdWatch, expectedWatch, "Unexpected behavior created for case: " + msg);

        return createdWatch;

    }

    /**
     * Given a submitted watch object and an watch instance returned by the API after creation,
     * this method returns a new StudentWatch instance that represents the expected state of the submitted
     * Behavior after creation.  The reason that there are differences in the submitted and expected
     * instances is that there may be system assigned values not in the initially submitted object, for
     * example, the id property.
     *
     * @param submitted
     * @param created
     * @return
     */
    protected StudentWatch generateExpectationWatch(StudentWatch submitted, StudentWatch created, HttpMethod method) {

        submitted.setId(created.getId());
        return submitted;
    }



}