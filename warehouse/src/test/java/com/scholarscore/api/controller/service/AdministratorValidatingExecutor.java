package com.scholarscore.api.controller.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.user.Administrator;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import java.util.ArrayList;

/**
 * Created by cwallace on 1/19/16.
 */
public class AdministratorValidatingExecutor {
    private final IntegrationBase serviceBase;

    public AdministratorValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }

    public Administrator get(Long adminId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getAdministratorEndpoint(adminId),
                null);
        Administrator admin = serviceBase.validateResponse(response, new TypeReference<Administrator>(){});
        Assert.assertNotNull(admin, "Unexpected null teacher returned for case: " + msg);

        return admin;
    }

    public void getAll(String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getAdministratorEndpoint(),
                null);
        ArrayList<Administrator> admins = serviceBase.validateResponse(response, new TypeReference<ArrayList<Administrator>>(){});
        Assert.assertNotNull(admins, "Unexpected null teacher returned for case: " + msg);
    }

    public void getNegative(Long teacherId, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getAdministratorEndpoint(teacherId),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected status code returned while retrieving teacher: " + msg);
    }


    public Administrator create(Administrator admin, String msg) {
        //Create the teacher
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getAdministratorEndpoint(), null, admin);
        EntityId adminId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(adminId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedAdmin(adminId.getId(), admin, HttpMethod.POST, msg);
    }

    public void createNegative(Administrator admin, HttpStatus expectedCode, String msg) {
        //Attempt to create the teacher
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getAdministratorEndpoint(), null, admin);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected status code returned for case: " + msg);
    }

    public void delete(Long adminId, String msg) {
        //Delete the teacher
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE,
                serviceBase.getAdministratorEndpoint(adminId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(adminId, HttpStatus.NOT_FOUND, msg);
    }

    public void deleteNegative(Long teacherId, HttpStatus expectedCode, String msg) {
        //Attempt to delete the teacher
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE,
                serviceBase.getAdministratorEndpoint(teacherId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }

    public Administrator replace(Long adminId, Administrator admin, String msg) {
        //Create the teacher
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT,
                serviceBase.getAdministratorEndpoint(adminId),
                null,
                admin);
        EntityId returnedTeacherId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedTeacherId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedAdmin(adminId, admin, HttpMethod.PUT, msg);
    }

    public void replaceNegative(Long adminId, Administrator admin, HttpStatus expectedCode, String msg) {
        //Create the teacher
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT,
                serviceBase.getAdministratorEndpoint(adminId),
                null,
                admin);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }

    public Administrator update(Long adminId, Administrator admin, String msg) {
        //Create the teacher
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH,
                serviceBase.getAdministratorEndpoint(adminId),
                null,
                admin);
        EntityId returnedTeacherId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedTeacherId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedAdmin(adminId, admin, HttpMethod.PATCH, msg);
    }

    public void updateNegative(Long teacherId, Long courseId, Long id, Administrator admin, HttpStatus expectedCode, String msg) {
        //Create the teacher
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH,
                serviceBase.getAdministratorEndpoint(teacherId),
                null,
                admin);
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
     * @param msg
     * @return
     */
    protected Administrator retrieveAndValidateCreatedAdmin( Long adminId, Administrator submittedAdmin, HttpMethod method, String msg) {
        //Retrieve and validate the created assignment
        Administrator createdAdmin = this.get(adminId, msg);
        //Keep a reference to the created assignment for later cleanup
        serviceBase.adminsCreated.add(createdAdmin);
        Administrator expectedAdmin = generateExpectationAdmin(submittedAdmin, createdAdmin, method);
        Assert.assertEquals(createdAdmin, expectedAdmin, "Unexpected assignment created for case: " + msg);

        return createdAdmin;
    }
    /**
     * Given a submitted assignment object and an assignment instance returned by the API after creation,
     * this method returns a new Teacher instance that represents the expected state of the submitted
     * Teacher after creation.  The reason that there are differences in the submitted and expected
     * instances is that there may be system assigned values not in the initially submitted object, for
     * example, the id property.
     *
     * @param submitted
     * @param created
     * @return
     */
    protected Administrator generateExpectationAdmin(Administrator submitted, Administrator created, HttpMethod method) {
        Administrator returnAdmin = new Administrator(submitted);
        if(method == HttpMethod.PATCH) {
            returnAdmin.mergePropertiesIfNull(created);
        } else if(null != returnAdmin && null == returnAdmin.getId()) {
            returnAdmin.setId(created.getId());
        }
        returnAdmin.setEnabled(created.getEnabled());
        returnAdmin.setPassword(null);
        if(null == returnAdmin.getUsername()) {
            returnAdmin.setUsername(created.getUsername());
        }
        returnAdmin.setUserId(created.getUserId());
        returnAdmin.setId(created.getId());
        return returnAdmin;
    }
}