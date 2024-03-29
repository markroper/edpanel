package com.scholarscore.api.controller.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.user.User;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Defines the pattern for validating the auth end point
 *
 * Created by mgreenwood on 9/14/15.
 */
public class AuthValidatingExecutor {
    private final IntegrationBase serviceBase;

    public AuthValidatingExecutor(IntegrationBase serviceBase) {
        this.serviceBase = serviceBase;
    }

    public void getCurrentUser(String expectUsername) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getCurrentUserEndpoint(),
                null);
        assertNotNull(response, "Expected non-null response from getCurrentUser");
        User identity = serviceBase.validateResponse(response, new TypeReference<User>(){});
        assertNotNull(identity, "Expected non-null identity returned from getCurrentUser");
        assertNotNull(identity, "Expected non-null identity.getLogin() from getCurrentUser");
        assertEquals(identity.getUsername(), expectUsername, "Expected the username to equal the expected username");
    }
}
