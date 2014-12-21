package com.scholarscore.api.controller;

import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Student;

@Test(groups = { "integration" })
public class StudentControllerIntegrationTest extends IntegrationBase {
    private int numberOfItemsCreated = 0;
    
    @BeforeClass
    public void init() {
        numberOfItemsCreated = 0;
    }
    
    //Positive test cases
    @DataProvider
    public Object[][] createStudentProvider() {
        Student emptyStudent = new Student();
        Student namedStudent = new Student();
        namedStudent.setName(localeServiceUtil.generateName());
        
        return new Object[][] {
                { "Empty student", emptyStudent },
                { "Named student", namedStudent }
        };
    }
    
    @Test(dataProvider = "createStudentProvider")
    public void createStudentTest(String msg, Student student) {
        studentValidatingExecutor.create(student, msg);
        numberOfItemsCreated++;
    }
    
    @Test(dataProvider = "createStudentProvider")
    public void deleteStudentTest(String msg, Student student) {
        Student createdStudent = studentValidatingExecutor.create(student, msg);
        studentValidatingExecutor.delete(createdStudent.getId(), msg);
    }
    
    @Test(dataProvider = "createStudentProvider")
    public void replaceStudentTest(String msg, Student student) {
        Student createdStudent = studentValidatingExecutor.create(student, msg);
        studentValidatingExecutor.replace(createdStudent.getId(), new Student(), msg);
        numberOfItemsCreated++;
    }
    
    @Test(dataProvider = "createStudentProvider")
    public void updateStudentTest(String msg, Student student) {
        Student createdStudent = studentValidatingExecutor.create(student, msg);
        Student updatedStudent = new Student();
        updatedStudent.setName(localeServiceUtil.generateName());
        //PATCH the existing record with a new name.
        studentValidatingExecutor.update(createdStudent.getId(), updatedStudent, msg);
        numberOfItemsCreated++;
    }
    
    @Test
    public void getAllItems() {
        studentValidatingExecutor.getAll("Get all records created so far");
    }
    
    //Negative test cases
    @DataProvider
    public Object[][] createStudentNegativeProvider() {
        Student gradedStudentNameTooLong = new Student();
        gradedStudentNameTooLong.setName(localeServiceUtil.generateName(257));
        
        return new Object[][] {
                { "Student with name exceeding 256 char limit", gradedStudentNameTooLong, HttpStatus.BAD_REQUEST }
        };
    }
    
    @Test(dataProvider = "createStudentNegativeProvider")
    public void createStudentNegativeTest(String msg, Student student, HttpStatus expectedStatus) {
        studentValidatingExecutor.createNegative(student, expectedStatus, msg);
    }
    
    @Test(dataProvider = "createStudentNegativeProvider")
    public void replaceStudentNegativeTest(String msg, Student student, HttpStatus expectedStatus) {
        Student created = studentValidatingExecutor.create(new Student(), msg);
        studentValidatingExecutor.replaceNegative(created.getId(), student, expectedStatus, msg);
    }
}
