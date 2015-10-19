package com.scholarscore.models;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * LoginRequestBuilderUnitTest tests that we can build equivalent login request objects with setters and with a builder
 * Created by cschneider on 10/11/15.
 */
@Test
public class LoginRequestBuilderUnitTest extends AbstractBuilderUnitTest<LoginRequest>{
    @DataProvider
    @Override
    public Object[][] builderProvider() {
        LoginRequest emptyLoginRequest = new LoginRequest();
        LoginRequest emptyLoginRequestByBuilder = new LoginRequest.LoginRequestBuilder().build();

        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphanumeric(14);

        LoginRequest fullLoginRequest = new LoginRequest();
        fullLoginRequest.setUsername(username);
        fullLoginRequest.setPassword(password);

        LoginRequest fullLoginRequestBuilder = new LoginRequest.LoginRequestBuilder().
                withUsername(username).
                withPassword(password).
                build();

        return new Object[][]{
                {"Empty login request", emptyLoginRequestByBuilder, emptyLoginRequest},
                {"Full login request", fullLoginRequestBuilder, fullLoginRequest},

        };

    }
}
