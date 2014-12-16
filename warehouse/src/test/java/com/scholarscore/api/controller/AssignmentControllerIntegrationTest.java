package com.scholarscore.api.controller;
    
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Assignment;
import com.scholarscore.models.AttendanceAssignment;
import com.scholarscore.models.Course;
import com.scholarscore.models.GradedAssignment;
import com.scholarscore.models.School;

@Test(groups = { "integration" })
public class AssignmentControllerIntegrationTest extends IntegrationBase {
    private int numberOfItemsCreated = 0;
    private School school;
    private Course course;
    
    @BeforeClass
    public void init() {
        numberOfItemsCreated = 0;
        school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolServiceValidatingExecutor.create(school, "Create base school");
        
        course = new Course();
        course.setName(localeServiceUtil.generateName());
        course = courseServiceValidatingExecutor.create(school.getId(), course, "create base course");
    }
    
    //Positive test cases
    @DataProvider
    public Object[][] createAssignmentProvider() {
        GradedAssignment emptyGradedAssignment = new GradedAssignment();
        
        GradedAssignment gradedAssignment = new GradedAssignment();
        gradedAssignment.setName(localeServiceUtil.generateName());
        gradedAssignment.setDueDate(new Date(1234567L));
        gradedAssignment.setAssignedDate(new Date(123456L));
        
        AttendanceAssignment emptyAttendanceAssignment = new AttendanceAssignment();
        
        AttendanceAssignment attendanceAssignment = new AttendanceAssignment();
        attendanceAssignment.setName(localeServiceUtil.generateName());
        attendanceAssignment.setDate(new Date());
        
        return new Object[][] {
                { "Empty graded assignment", emptyGradedAssignment },
                { "Graded assignment", gradedAssignment },
                { "Empty attendance assignment", emptyAttendanceAssignment },
                { "Attendance assignment", attendanceAssignment }
        };
    }
    
    @Test(dataProvider = "createAssignmentProvider")
    public void createAssignmentTest(String msg, Assignment assignment) {
        assignmentServiceValidatingExecutor.create(school.getId(), course.getId(), assignment, msg);
        numberOfItemsCreated++;
    }
    
    @Test(dataProvider = "createAssignmentProvider")
    public void deleteAssignmentTest(String msg, Assignment assignment) {
        Assignment createdAssignment = assignmentServiceValidatingExecutor.create(school.getId(), course.getId(), assignment, msg);
        assignmentServiceValidatingExecutor.delete(school.getId(), course.getId(), createdAssignment.getId(), msg);
    }
    
    @Test(dataProvider = "createAssignmentProvider")
    public void replaceAssignmentTest(String msg, Assignment assignment) {
        Assignment createdAssignment = assignmentServiceValidatingExecutor.create(school.getId(), course.getId(), assignment, msg);
        assignmentServiceValidatingExecutor.replace(school.getId(), course.getId(), createdAssignment.getId(), new GradedAssignment(), msg);
        numberOfItemsCreated++;
    }
    
    @Test(dataProvider = "createAssignmentProvider")
    public void updateAssignmentTest(String msg, Assignment assignment) {
        Assignment createdAssignment = assignmentServiceValidatingExecutor.create(school.getId(), course.getId(), assignment, msg);
        GradedAssignment updatedAssignment = new GradedAssignment();
        updatedAssignment.setName(localeServiceUtil.generateName());
        //PATCH the existing record with a new name.
        assignmentServiceValidatingExecutor.update(school.getId(), course.getId(), createdAssignment.getId(), updatedAssignment, msg);
        numberOfItemsCreated++;
    }
    
    @Test
    public void getAllItems() {
        assignmentServiceValidatingExecutor.getAll(school.getId(), course.getId(), "Get all records created so far", numberOfItemsCreated++);
    }
    
    //Negative test cases
    @DataProvider
    public Object[][] createAssignmentNegativeProvider() {
        GradedAssignment gradedAssignmentNameTooLong = new GradedAssignment();
        gradedAssignmentNameTooLong.setName(localeServiceUtil.generateName(257));
        
        return new Object[][] {
                { "Assignment with name exceeding 256 char limit", gradedAssignmentNameTooLong, HttpStatus.BAD_REQUEST }
        };
    }
    
    @Test(dataProvider = "createAssignmentNegativeProvider")
    public void createAssignmentNegativeTest(String msg, Assignment assignment, HttpStatus expectedStatus) {
        assignmentServiceValidatingExecutor.createNegative(school.getId(), course.getId(), assignment, expectedStatus, msg);
    }
    
    @Test(dataProvider = "createAssignmentNegativeProvider")
    public void replaceAssignmentNegativeTest(String msg, Assignment assignment, HttpStatus expectedStatus) {
        Assignment created = assignmentServiceValidatingExecutor.create(school.getId(), course.getId(), new GradedAssignment(), msg);
        assignmentServiceValidatingExecutor.replaceNegative(school.getId(), course.getId(), created.getId(), assignment, expectedStatus, msg);
    }
}