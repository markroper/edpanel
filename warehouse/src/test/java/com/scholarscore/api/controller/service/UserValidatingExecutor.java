package com.scholarscore.api.controller.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.User;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

/**
 * Created by mgreenwood on 9/14/15.
 */
public class UserValidatingExecutor {
    private final IntegrationBase serviceBase;

    public UserValidatingExecutor(IntegrationBase serviceBase) {
        this.serviceBase = serviceBase;
    }

    public User create(User user, String msg) {
        //Create the user
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getUsersEndpoint(), null, user);
        String username = serviceBase.validateResponse(response, new TypeReference<String>(){});
        Assert.assertNotNull(username, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedUser(user.getUsername(), user, HttpMethod.POST, msg);
    }

    private User retrieveAndValidateCreatedUser(String username, User expectedUser, HttpMethod post, String msg) {
        //Retrieve and validate the created assignment
        User createdUser = this.get(username, msg);
        // when a user is created locally and submitted to server, ID isn't known until it is fetched again
        if (expectedUser.getId() == null && createdUser.getId() != null) {
            expectedUser.setId(createdUser.getId());
        }
        //Keep a reference to the created assignment for later cleanup
        serviceBase.usersCreated.add(createdUser);
        Assert.assertEquals(createdUser, expectedUser, "Unexpected assignment created for case: " + msg);
        return createdUser;
    }

    public void delete(String username, String msg) {
        //Delete the user
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE,
                serviceBase.getUsersEndpoint(username));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
    }


    private User get(String username, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getUsersEndpoint(username),
                null);
        User user = serviceBase.validateResponse(response, new TypeReference<User>(){});
        Assert.assertNotNull(user, "Unexpected null user returned for case: " + msg);
        return user;
    }
}
