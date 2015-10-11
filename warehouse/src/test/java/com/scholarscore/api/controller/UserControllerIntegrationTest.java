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
//        createdUser.setName("newName");
        user.setName("newName");
        user.setUsername(user.getUsername()+"updated");
        user.setPassword("newPassword");
//        userValidatingExecutor
        
    }
    
    // TODO Jordan: finish replace and update test
    
    // TODO Jordan: test validate, confirmvalidate, request reset password, reset password
    // Also test user roles with CHANGE_PASSWORD_PERMISSION
    
    @DataProvider
    public Object[][] userProvider() {
        User studentUser = new Student();
        studentUser.setUsername("somestudentuser");
        studentUser.setPassword("abcdef");
        studentUser.setName("student user");
        
        User teacherUser = new Teacher();
        teacherUser.setName("teacher user");
        teacherUser.setPassword("abcdef");
        teacherUser.setUsername("someteacheruser");
        
        User adminUser = new Administrator();
        adminUser.setName("admin user");
        adminUser.setPassword("abcdef");
        adminUser.setUsername("someadminuser");

        return new Object[][] { 
                { "Student User", studentUser },
                { "Teacher User", teacherUser },
                { "Administrator User", adminUser },
        };
    }
    
}
