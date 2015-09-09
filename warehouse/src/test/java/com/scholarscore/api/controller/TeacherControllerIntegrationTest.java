package com.scholarscore.api.controller;

import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Teacher;

@Test(groups = { "integration" })
public class TeacherControllerIntegrationTest extends IntegrationBase {
    @BeforeClass
    public void init() {
        authenticate();
    }
    
    //Positive test cases
    @DataProvider
    public Object[][] createTeacherProvider() {
        Teacher emptyTeacher = new Teacher();
        Teacher namedTeacher = new Teacher();
        namedTeacher.setName(localeServiceUtil.generateName());
        
        return new Object[][] {
                { "Empty teacher", emptyTeacher },
                { "Named teacher", namedTeacher }
        };
    }
    
    @Test(dataProvider = "createTeacherProvider")
    public void createTeacherTest(String msg, Teacher teacher) {
        teacherValidatingExecutor.create(teacher, msg);
    }
    
    @Test(dataProvider = "createTeacherProvider")
    public void deleteTeacherTest(String msg, Teacher teacher) {
        Teacher createdTeacher = teacherValidatingExecutor.create(teacher, msg);
        teacherValidatingExecutor.delete(createdTeacher.getId(), msg);
    }
    
    @Test(dataProvider = "createTeacherProvider")
    public void replaceTeacherTest(String msg, Teacher teacher) {
        Teacher createdTeacher = teacherValidatingExecutor.create(teacher, msg);
        teacherValidatingExecutor.replace(createdTeacher.getId(), new Teacher(teacher), msg);
    }
    
    @Test(dataProvider = "createTeacherProvider")
    public void updateTeacherTest(String msg, Teacher teacher) {
        Teacher createdTeacher = teacherValidatingExecutor.create(teacher, msg);
        Teacher updatedTeacher = new Teacher();
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
        Teacher gradedTeacherNameTooLong = new Teacher();
        gradedTeacherNameTooLong.setName(localeServiceUtil.generateName(257));
        
        return new Object[][] {
                { "Teacher with name exceeding 256 char limit", gradedTeacherNameTooLong, HttpStatus.BAD_REQUEST }
        };
    }
    
    @Test(dataProvider = "createTeacherNegativeProvider")
    public void createTeacherNegativeTest(String msg, Teacher teacher, HttpStatus expectedStatus) {
        teacherValidatingExecutor.createNegative(teacher, expectedStatus, msg);
    }
    
    @Test(dataProvider = "createTeacherNegativeProvider")
    public void replaceTeacherNegativeTest(String msg, Teacher teacher, HttpStatus expectedStatus) {
        Teacher created = teacherValidatingExecutor.create(new Teacher(), msg);
        teacherValidatingExecutor.replaceNegative(created.getId(), teacher, expectedStatus, msg);
    }
}
