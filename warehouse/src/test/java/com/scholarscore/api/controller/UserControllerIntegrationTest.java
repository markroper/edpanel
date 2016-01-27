package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.User;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.Assert.assertEquals;

/**
 * Integration tests for the CRUD operations on User instances and subclasses
 * User: jordan
 * Date: 10/8/15
 * Time: 2:35 PM
 */
@Test(groups = { "integration" })
public class UserControllerIntegrationTest extends IntegrationBase {

    @BeforeClass
    public void init() {
        authenticate();
    }

    @Test(dataProvider = "userProvider")
    public void createAndDeleteUserTest(String msg, User user) { 
        User createdUser = userValidatingExecutor.create(user, "error creating user: " + msg);
        userValidatingExecutor.delete(createdUser.getId(), "error deleting user: " + msg);
    }
    
    @Test(dataProvider = "userProvider")
    public void replaceUserTest(String msg, User user) { 
        User createdUser = userValidatingExecutor.create(user, "error creating user: " + msg);

        createdUser.setName("newName");
        createdUser.setUsername(user.getUsername()+"updated");
        createdUser.setEmail("new_email_address@something.com");
        
        userValidatingExecutor.replace(createdUser.getId(), createdUser, "error replacing user: " + msg);
        userValidatingExecutor.delete(createdUser.getId(), "error deleting user: " + msg);
    }

    @Test(dataProvider = "userProvider")
    public void replaceUserShouldNotChangeCertainFields(String msg, User user) {
        User createdUser = userValidatingExecutor.create(user, "error creating user: " + msg);

        assertEquals(createdUser.getPassword(), null);
        assertEquals(createdUser.getOneTimePass(), null);
        assertEquals(createdUser.getOneTimePassCreated(), null);

        createdUser.setPassword("password_changed");
        createdUser.setOneTimePass("one time pass");
        createdUser.setOneTimePassCreated(new Date());

        // note the 'false' on the next line actually skips the validation -- the user that is returned does
        // not actually match the user that was sent, because certain fields are not set on the server.
        // we could also consider returning an error in these situations.
        User returnedUser = userValidatingExecutor.replace(createdUser.getId(), createdUser, "error replacing user: " + msg, false);
        assertEquals(returnedUser.getPassword(), null);
        assertEquals(returnedUser.getOneTimePass(), null);
        assertEquals(returnedUser.getOneTimePassCreated(), null);
        userValidatingExecutor.delete(createdUser.getId(), "error deleting user: " + msg);
    }
    
    // TODO Jordan: a bunch of tests here...
    // - a bunch of testing specific to the email/phone weirdness facade
    // - do an 'update' test (and 'negative' test for internals) 
    // (e.g. enabled, password + verification fields... should these just be JSON ignored entirely except in special requests?)
    // - test more role stuff (like with user role CHANGE_PASSWORD_PERMISSION)
    // - test new stuff -- validate, confirmvalidate, request reset password, reset password
    
    @DataProvider
    public Object[][] userProvider() {
        User studentUser = new Student();
        studentUser.setUsername(localeServiceUtil.generateName(12));
        studentUser.setName("student user");
        studentUser.setEmail("bluemarker@gmail.com");
        
        User teacherUser = new Staff();
        teacherUser.setName(localeServiceUtil.generateName(12));
        teacherUser.setUsername(localeServiceUtil.generateName(12));
        
        User adminUser = new Staff();
        adminUser.setName(localeServiceUtil.generateName(12));
        adminUser.setUsername(localeServiceUtil.generateName(12));

        return new Object[][] { 
                { "Student User", studentUser },
                { "Teacher User", teacherUser },
                { "Administrator User", adminUser },
        };
    }
    
    
    
}
