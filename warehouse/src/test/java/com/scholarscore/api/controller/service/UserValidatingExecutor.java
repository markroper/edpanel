package com.scholarscore.api.controller.service;

import com.scholarscore.models.EntityId;
import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.user.Administrator;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.models.user.User;

import org.springframework.http.HttpMethod;
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
        EntityId userId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(userId, "unexpected null app returned from create call for case: " + msg);
        return retrieveAndValidateCreatedUser(userId, user, HttpMethod.POST, msg);
    }

    private User retrieveAndValidateCreatedUser(EntityId id, User expectedUser, HttpMethod post, String msg) {
        //Retrieve and validate the created assignment
        User createdUser = this.get(id.getId(), msg);
        // when a user is created locally and submitted to server, ID isn't known until it is fetched again
        if (expectedUser.getId() == null && createdUser.getId() != null) {
            expectedUser.setId(createdUser.getId());
        }
        expectedUser.setId(createdUser.getId());
        expectedUser.setUsername(createdUser.getUsername());
        expectedUser.setPassword(createdUser.getPassword());
        if(expectedUser instanceof Student) 
            ((Student) expectedUser).setUserId(expectedUser.getId());
        if(expectedUser instanceof Administrator) 
            ((Administrator) expectedUser).setUserId(expectedUser.getId());
        if(expectedUser instanceof Teacher) 
            ((Teacher) expectedUser).setUserId(expectedUser.getId());
        //Keep a reference to the created assignment for later cleanup
        serviceBase.usersCreated.add(createdUser);
        Assert.assertEquals(createdUser, expectedUser, "Unexpected assignment created for case: " + msg);
        return createdUser;
    }

    private User get(Long userId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getUsersEndpoint(userId),
                null);
        User user = serviceBase.validateResponse(response, new TypeReference<User>(){});
        Assert.assertNotNull(user, "Unexpected null user returned for case: " + msg);
        return user;
    }
}
