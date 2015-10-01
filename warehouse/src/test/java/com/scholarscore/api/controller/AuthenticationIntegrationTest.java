package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.User;
import com.scholarscore.models.user.UserType;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

/**
 * Created by mgreenwood on 9/14/15.
 */
@Test(groups = {"integration"})
public class AuthenticationIntegrationTest extends IntegrationBase {

    @BeforeClass
    public void init() {
        authenticate();
    }

    public void testUserControllerGetCurrentUser() {
        authenticate();
        this.authValidatingExecutor.getCurrentUser("mroper");
    }

    public void testStudentLogin() {
        
        
        Student user = new Student();
        user.setPassword("password");
        user.setEnabled(true);
        user.setUsername("studentUser");
        User studentUser = userValidatingExecutor.create(user, "Creating test student user");
        assertNotNull(studentUser);
    }

}