package com.scholarscore.api.controller;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Assignment;
import com.scholarscore.models.AttendanceAssignment;
import com.scholarscore.models.GradedAssignment;

@Test(groups = { "integration" })
public class AssignmentControllerIntegrationTest extends IntegrationBase {

    @DataProvider
    public Object[][] createAssignmentProvider() {
        GradedAssignment emptyGradedAssignment = new GradedAssignment();
        
        GradedAssignment gradedAssignment = new GradedAssignment();
        gradedAssignment.setName(localeServiceValidator.generateName());
        gradedAssignment.setDueDate(new Date(1234567L));
        gradedAssignment.setAssignedDate(new Date(123456L));
        
        AttendanceAssignment emptyAttendanceAssignment = new AttendanceAssignment();
        
        AttendanceAssignment attendanceAssignment = new AttendanceAssignment();
        attendanceAssignment.setName(localeServiceValidator.generateName());
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
        assignmentServiceValidator.create(assignment, msg);
    }
    
    @Test(dataProvider = "createAssignmentProvider")
    public void deleteAssignmentTest(String msg, Assignment assignment) {
        Assignment createdAssignment = assignmentServiceValidator.create(assignment, msg);
        assignmentServiceValidator.delete(createdAssignment.getId(), msg);
    }
    
    @Test(dataProvider = "createAssignmentProvider")
    public void replaceAssignmentTest(String msg, Assignment assignment) {
        Assignment createdAssignment = assignmentServiceValidator.create(assignment, msg);
        assignmentServiceValidator.replace(createdAssignment.getId(), new GradedAssignment(), msg);
    }
    
    @DataProvider
    public Object[][] createAssignmentNegativeProvider() {
        GradedAssignment gradedAssignmentNameTooLong = new GradedAssignment();
        gradedAssignmentNameTooLong.setName(localeServiceValidator.generateName(257));
        
        return new Object[][] {
                { "Assignment with name exceeding 256 char limit", gradedAssignmentNameTooLong, HttpStatus.BAD_REQUEST }
        };
    }
    
    @Test(dataProvider = "createAssignmentNegativeProvider")
    public void createAssignmentNegativeTest(String msg, Assignment assignment, HttpStatus expectedStatus) {
        assignmentServiceValidator.createNegative(assignment, expectedStatus, msg);
    }
    
    @Test(dataProvider = "createAssignmentNegativeProvider")
    public void replaceAssignmentNegativeTest(String msg, Assignment assignment, HttpStatus expectedStatus) {
        Assignment created = assignmentServiceValidator.create(new GradedAssignment(), msg);
        assignmentServiceValidator.replaceNegative(created.getId(), assignment, expectedStatus, msg);
    }
}
