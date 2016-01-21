package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.user.Staff;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

@Test(groups = { "integration" })
public class TeacherControllerIntegrationTest extends IntegrationBase {
    @BeforeClass
    public void init() {
        authenticate();
    }
    
    //Positive test cases
    @DataProvider
    public Object[][] createTeacherProvider() {
        Staff emptyTeacher = new Staff();
        Staff namedTeacher = new Staff();
        namedTeacher.setName(localeServiceUtil.generateName());
        
        return new Object[][] {
//                { "Empty teacher", emptyTeacher },
                { "Named teacher", namedTeacher }
        };
    }
    
    @Test(dataProvider = "createTeacherProvider")
    public void createTeacherTest(String msg, Staff teacher) {
        teacherValidatingExecutor.create(teacher, msg);
    }
    
    @Test(dataProvider = "createTeacherProvider")
    public void deleteTeacherTest(String msg, Staff teacher) {
        Staff createdTeacher = teacherValidatingExecutor.create(teacher, msg);
        teacherValidatingExecutor.delete(createdTeacher.getId(), msg);
    }
    
    @Test(dataProvider = "createTeacherProvider")
    public void replaceTeacherTest(String msg, Staff teacher) {
        Staff createdTeacher = teacherValidatingExecutor.create(teacher, msg);
        Staff t = new Staff(teacher);
        teacherValidatingExecutor.replace(createdTeacher.getId(), t, msg);
    }
    
    @Test(dataProvider = "createTeacherProvider")
    public void updateTeacherTest(String msg, Staff teacher) {
        Staff createdTeacher = teacherValidatingExecutor.create(teacher, msg);
        Staff updatedTeacher = new Staff();
        updatedTeacher.setName(localeServiceUtil.generateName());
        //PATCH the existing record with a new name.
        teacherValidatingExecutor.update(createdTeacher.getId(), updatedTeacher, msg);
    }
    
    @Test
    public void getAllItems() {
        teacherValidatingExecutor.getAll("Get all records created so far");
    }
    
    //Negative test cases
    @DataProvider
    public Object[][] createTeacherNegativeProvider() {
        Staff gradedTeacherNameTooLong = new Staff();
        gradedTeacherNameTooLong.setName(localeServiceUtil.generateName(257));
        
        return new Object[][] {
                { "Teacher with name exceeding 256 char limit", gradedTeacherNameTooLong, HttpStatus.BAD_REQUEST }
        };
    }
    
    @Test(dataProvider = "createTeacherNegativeProvider")
    public void createTeacherNegativeTest(String msg, Staff teacher, HttpStatus expectedStatus) {
        teacherValidatingExecutor.createNegative(teacher, expectedStatus, msg);
    }
    
    @Test(dataProvider = "createTeacherNegativeProvider")
    public void replaceTeacherNegativeTest(String msg, Staff teacher, HttpStatus expectedStatus) {
        Staff t = new Staff();
        t.setName(UUID.randomUUID().toString());
        Staff created = teacherValidatingExecutor.create(t, msg);
        teacherValidatingExecutor.replaceNegative(created.getId(), teacher, expectedStatus, msg);
    }
}
