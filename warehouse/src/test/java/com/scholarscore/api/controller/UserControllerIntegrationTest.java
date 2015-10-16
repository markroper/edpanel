package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.user.Administrator;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.models.user.User;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * User: jordan
 * Date: 10/8/15
 * Time: 2:35 PM
 */
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

        createdUser.setPassword("password_changed");
        createdUser.setOneTimePass("one time pass");

        // note the 'false' on the next line actually skips the validation -- the user that is returned does
        // not actually match the user that was sent, because certain fields are not set on the server.
        // we could also consider returning an error in these situations.
        User returnedUser = userValidatingExecutor.replace(createdUser.getId(), createdUser, "error replacing user: " + msg, false);
        assertEquals(returnedUser.getPassword(), "abcdef");
        assertEquals(returnedUser.getOneTimePass(), "1time");
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
        studentUser.setPassword("abcdef");
        studentUser.setOneTimePass("1time");
        studentUser.setName("student user");
        studentUser.setEmail("bluemarker@gmail.com");
        
        User teacherUser = new Teacher();
        teacherUser.setName(localeServiceUtil.generateName(12));
        teacherUser.setPassword("abcdef");
        teacherUser.setOneTimePass("1time");
        teacherUser.setUsername("someteacheruser");
        
        User adminUser = new Administrator();
        adminUser.setName(localeServiceUtil.generateName(12));
        adminUser.setPassword("abcdef");
        adminUser.setOneTimePass("1time");
        adminUser.setUsername("someadminuser");

        return new Object[][] { 
                { "Student User", studentUser },
//                { "Teacher User", teacherUser },
//                { "Administrator User", adminUser },
        };
    }
    
    
    
}
