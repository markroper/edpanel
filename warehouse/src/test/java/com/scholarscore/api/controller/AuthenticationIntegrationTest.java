package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Student;
import com.scholarscore.models.User;
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
        
        
        User user = new User();
        user.setName("Test student");
        user.setPassword("password");
        user.setEnabled(true);
        user.setUsername("studentUser");
        User studentUser = userValidatingExecutor.create(user, "Creating test student user");

        Student student = new Student();
        student.setUser(studentUser);
        student.setName("Billy Student");
        // TODO Jordan: username shouldn't be set in two places probably
//        student.setUsername("billys");
//        student.setUsername("studentUser");
        Student result = studentValidatingExecutor.create(student, "Create student for authentication");
        assertNotNull(result);
    }

}