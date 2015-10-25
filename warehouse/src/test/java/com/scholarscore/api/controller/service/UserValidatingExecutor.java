package com.scholarscore.api.controller.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.School;
import com.scholarscore.models.user.Administrator;
import com.scholarscore.models.user.Person;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.models.user.User;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import static org.testng.Assert.fail;

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

    private User retrieveAndValidateCreatedUser(EntityId id, User submittedUser, HttpMethod method, String msg) {
        //Retrieve and validate the created assignment
        User createdUser = this.get(id.getId(), msg);
        User expectedUser = generateExpectationUser(submittedUser, createdUser, method);
        //Keep a reference to the created assignment for later cleanup
        serviceBase.usersCreated.add(createdUser);
        Assert.assertEquals(createdUser, expectedUser, "Unexpected assignment created for case: " + msg);
        return createdUser;
    }

    public void delete(Long userId, String msg) {
        //Delete the user
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE,
                serviceBase.getUsersEndpoint(userId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
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
    
    public User replace(Long userId, User user, String msg) { 
        return replace(userId, user, msg, true);
    }
    
    public User replace(Long userId, User user, String msg, boolean shouldValidate) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.PUT,
                serviceBase.getUsersEndpoint(userId),
                null,
                user);
        EntityId returnedUserId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedUserId, "unexpected null app returned from create call for case: " + msg);
        User userToReturn;
        if (shouldValidate) {
            userToReturn = retrieveAndValidateCreatedUser(returnedUserId, user, HttpMethod.PUT, msg);
        } else {
            userToReturn = get(returnedUserId.getId(), msg); 
        }
        return userToReturn;
    }
    
    
     public void replaceNegative(Long schoolId, School school, HttpStatus expectedCode, String msg) {
        //Create the school
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT, 
                serviceBase.getSchoolEndpoint(schoolId), 
                null, 
                school);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for PUT: " + msg);
    }
    
    protected User generateExpectationUser(User submitted, User created, HttpMethod method) {
        if (submitted == null || created == null) { return null; }

        User returnUser = null;
        if (submitted instanceof Administrator) {
            returnUser = new Administrator((Administrator)submitted);
        } else if (submitted instanceof Student) {
            returnUser = new Student((Student)submitted);
        } else if (submitted instanceof Teacher) {
            returnUser = new Teacher((Teacher)submitted);
        } else {
            fail("Failure - unknown user type");
        }
        
        ((Person)returnUser).setUserId(created.getId());

        if(method == HttpMethod.PATCH) {
            returnUser.mergePropertiesIfNull(created);
        } 
        if(null == returnUser.getId()) {
            returnUser.setId(created.getId());
        }

        return returnUser;
    }
}
