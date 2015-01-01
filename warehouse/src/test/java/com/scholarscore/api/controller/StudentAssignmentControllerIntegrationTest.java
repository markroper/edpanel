package com.scholarscore.api.controller;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.GradedAssignment;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.SectionAssignment;
import com.scholarscore.models.Student;
import com.scholarscore.models.StudentAssignment;
import com.scholarscore.models.Term;

@Test(groups = { "integration" })
public class StudentAssignmentControllerIntegrationTest  extends IntegrationBase {
    private int numberOfItemsCreated = 0;
    private School school;
    private SchoolYear schoolYear;
    private Term term;
    private Section section;
    private SectionAssignment sectionAssignment;
    private Student student;
    
    @BeforeClass
    public void init() {
        numberOfItemsCreated = 0;
        school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolValidatingExecutor.create(school, "Create base school");
        
        student = new Student();
        student.setName(localeServiceUtil.generateName());
        student = studentValidatingExecutor.create(student, "create base student");
        
        schoolYear = new SchoolYear();
        schoolYear.setName(localeServiceUtil.generateName());
        schoolYear = schoolYearValidatingExecutor.create(school.getId(), schoolYear, "create base schoolYear");
        
        term = new Term();
        term.setName(localeServiceUtil.generateName());
        term = termValidatingExecutor.create(school.getId(), schoolYear.getId(), term, "create test base term");
        
        section = new Section();
        section.setName(localeServiceUtil.generateName());
        section.setEnrolledStudents(new ArrayList<Student>());
        section.getEnrolledStudents().add(student);
        section = sectionValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section, "create test base term");
        
        sectionAssignment = new SectionAssignment();
        sectionAssignment.setName(localeServiceUtil.generateName());
        sectionAssignment.setAssignedDate(new Date(1234567L));
        sectionAssignment.setDueDate(new Date(123456L));
        GradedAssignment assignment = new GradedAssignment();
        assignment.setId(2l);
        sectionAssignment.setAssignment(assignment);
        sectionAssignment = sectionAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), 
                term.getId(), section.getId(), sectionAssignment, "create test base term");
    }
    
    //Positive test cases
    @DataProvider
    public Object[][] createStudentAssignmentProvider() {
        //StudentAssignment emptyStudentAssignment = new StudentAssignment();
        
        StudentAssignment namedStudentAssignment = new StudentAssignment();
        namedStudentAssignment.setAssignment(sectionAssignment);
        namedStudentAssignment.setStudent(student);
        
        return new Object[][] {
                //{ "Empty section assignment", emptyStudentAssignment },
                { "Named student assignment", namedStudentAssignment },
        };
    }
    
    @Test(dataProvider = "createStudentAssignmentProvider")
    public void createStudentAssignmentTest(String msg, StudentAssignment studentAssignment) {
        studentAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), 
                section.getId(), sectionAssignment.getId(), studentAssignment, msg);
        numberOfItemsCreated++;
    }
    
    @Test(dataProvider = "createStudentAssignmentProvider")
    public void deleteStudentAssignmentTest(String msg, StudentAssignment studentAssignment) {
        StudentAssignment createdSection = studentAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section.getId(), sectionAssignment.getId(), studentAssignment, msg);
        studentAssignmentValidatingExecutor.delete(school.getId(), schoolYear.getId(), term.getId(), section.getId(), sectionAssignment.getId(), createdSection.getId(), msg);
    }
    
    @Test(dataProvider = "createStudentAssignmentProvider")
    public void replaceStudentAssignmentTest(String msg, StudentAssignment studentAssignment) {
        StudentAssignment createdSection = studentAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), 
                section.getId(), sectionAssignment.getId(), studentAssignment, msg);
        studentAssignmentValidatingExecutor.replace(school.getId(), schoolYear.getId(), term.getId(), 
                section.getId(), sectionAssignment.getId(), createdSection.getId(), new StudentAssignment(), msg);
        numberOfItemsCreated++;
    }
    
    @Test(dataProvider = "createStudentAssignmentProvider")
    public void updateStudentAssignmentTest(String msg, StudentAssignment studentAssignment) {
        StudentAssignment createdSection = studentAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), 
                section.getId(), sectionAssignment.getId(), studentAssignment, msg);
        StudentAssignment updatedStudent = new StudentAssignment();
        updatedStudent.setStudent(createdSection.getStudent());
        updatedStudent.setName(localeServiceUtil.generateName());
        //PATCH the existing record with a new name.
        studentAssignmentValidatingExecutor.update(school.getId(), schoolYear.getId(), term.getId(), 
                section.getId(), sectionAssignment.getId(), createdSection.getId(), updatedStudent, msg);
        numberOfItemsCreated++;
    }
    
    @Test
    public void getAllItems() {
        studentAssignmentValidatingExecutor.getAll(school.getId(), schoolYear.getId(), term.getId(), 
                section.getId(), sectionAssignment.getId(), "Get all records created so far", numberOfItemsCreated++);
    }
    
    //Negative test cases
    @DataProvider
    public Object[][] createStudentAssignmentNegativeProvider() {
        StudentAssignment gradedSectionNameTooLong = new StudentAssignment();
        gradedSectionNameTooLong.setName(localeServiceUtil.generateName(257));
        
        return new Object[][] {
                { "Section with name exceeding 256 char limit", gradedSectionNameTooLong, HttpStatus.BAD_REQUEST }
        };
    }
    
    @Test(dataProvider = "createStudentAssignmentNegativeProvider")
    public void createSectionNegativeTest(String msg, StudentAssignment studentAssignment, HttpStatus expectedStatus) {
        studentAssignmentValidatingExecutor.createNegative(school.getId(), schoolYear.getId(), term.getId(), 
                section.getId(), sectionAssignment.getId(), studentAssignment, expectedStatus, msg);
    }
    
    @Test(dataProvider = "createStudentAssignmentNegativeProvider")
    public void replaceSectionNegativeTest(String msg, StudentAssignment studentAssignment, HttpStatus expectedStatus) {
        StudentAssignment st = new StudentAssignment();
        st.setStudent(student);
        StudentAssignment created = studentAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), 
                section.getId(), sectionAssignment.getId(), st, msg);
        studentAssignmentValidatingExecutor.replaceNegative(school.getId(), schoolYear.getId(), term.getId(), 
                section.getId(), sectionAssignment.getId(), created.getId(), studentAssignment, expectedStatus, msg);
    }
}
