package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.user.Administrator;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.models.user.User;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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
    
//    public void replaceUserShouldNotAllow
    
    // TODO Jordan: a bunch of tests here...
    // - a bunch of testing specific to the email/phone weirdness facade
    // - finish 'update' test
    // - do 'negative' update/replace testing on anything specific to 'user' that should be internal 
    // (e.g. enabled, password + verification fields... should these just be JSON ignored entirely except in special requests?)
    // - test more role stuff (like with user role CHANGE_PASSWORD_PERMISSION)
    // - test new stuff -- validate, confirmvalidate, request reset password, reset password
    
    @DataProvider
    public Object[][] userProvider() {
        User studentUser = new Student();
        studentUser.setUsername(localeServiceUtil.generateName(12));
        studentUser.setPassword("abcdef");
        studentUser.setName("student user");
        
        User teacherUser = new Teacher();
        teacherUser.setName(localeServiceUtil.generateName(12));
        teacherUser.setPassword("abcdef");
        teacherUser.setUsername("someteacheruser");
        
        User adminUser = new Administrator();
        adminUser.setName(localeServiceUtil.generateName(12));
        adminUser.setPassword("abcdef");
        adminUser.setUsername("someadminuser");

        return new Object[][] { 
                { "Student User", studentUser },
                { "Teacher User", teacherUser },
                { "Administrator User", adminUser },
        };
    }
    
    
    
}
