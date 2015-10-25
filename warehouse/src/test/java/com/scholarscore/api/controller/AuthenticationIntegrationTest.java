package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.School;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.User;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.testng.Assert.assertNotNull;

/**
 * Created by mgreenwood on 9/14/15.
 */
@Test(groups = {"integration"})
public class AuthenticationIntegrationTest extends IntegrationBase {

    // break this out into resource file (or something)0
    private static final String USER_PERMISSIONS_LOGIN = "student_user";
    private static final String USER_PERMISSIONS_PASS = "student_user";
    
    private static final String ADMIN_PERMISSIONS_LOGIN = "mroper";
    private static final String ADMIN_PERMISSIONS_PASS = "admin";
    
    @BeforeMethod
    public void init() {
        authenticate();
    }

    @Test
    public void testUserControllerGetCurrentUser() {
        this.authValidatingExecutor.getCurrentUser("mroper");
    }

    @Test
    public void testStudentLogin() {
        Student user = new Student();
        user.setPassword("password");
        user.setEnabled(true);
        user.setUsername(UUID.randomUUID().toString());
        User studentUser = userValidatingExecutor.create(user, "Creating test student user");
        assertNotNull(studentUser);
    }
    
    @Test
    public void testRoles() {

        // clear this to be anonymous
        invalidateCookie();

        // positive anon - do something that anyone can do when anon
        schoolValidatingExecutor.getAllOptions("anon user can options any endpoint (is this a good idea?)");
        // negative anon - do something that anon is not allowed to do and confirm they can't
        // (e.g. get all students)
        schoolValidatingExecutor.getAllNegative(HttpStatus.UNAUTHORIZED, "anon user shouldn't be able to get all schools");

        invalidateCookie();
        authenticate(USER_PERMISSIONS_LOGIN, USER_PERMISSIONS_PASS);
        // negative user test
        // (e.g. create school) 
        School school = new School();
        school.setName("school");

        schoolValidatingExecutor.createNegative(school, HttpStatus.FORBIDDEN, "regular user shouldn't be able to create school");

        authenticate(ADMIN_PERMISSIONS_LOGIN, ADMIN_PERMISSIONS_PASS);
        // positive admin
        // (e.g. create school)
        schoolValidatingExecutor.create(school, "admin can create school");
        // negative admin -- none of these??
        invalidateCookie();
    }

}