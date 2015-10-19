package com.scholarscore.api.controller;

import java.util.Calendar;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.assignment.AssignmentType;
import com.scholarscore.models.Course;
import com.scholarscore.models.assignment.GradedAssignment;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.Term;

@Test(groups = { "integration" })
public class AssignmentControllerIntegrationTest extends IntegrationBase {
    private int numberOfItemsCreated = 0;
    private School school;
    private SchoolYear schoolYear;
    private Term term;
    private Section section;
    private Course course; 
    
    @BeforeClass
    public void init() {
        authenticate();
        
        numberOfItemsCreated = 0;
        school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolValidatingExecutor.create(school, "Create base school");
        
        schoolYear = new SchoolYear();
        schoolYear.setName(localeServiceUtil.generateName());
        schoolYear = schoolYearValidatingExecutor.create(school.getId(), schoolYear, "create base schoolYear");
        
        term = new Term();
        term.setName(localeServiceUtil.generateName());
        term = termValidatingExecutor.create(school.getId(), schoolYear.getId(), term, "create test base term");
        
        course = new Course();
        course.setName(localeServiceUtil.generateName());
        course = courseValidatingExecutor.create(school.getId(), course, "create base course");
        
        section = new Section();
        section.setCourse(course);
        section.setName(localeServiceUtil.generateName());
        section = sectionValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section, "create test base term");
    }
    
    //Positive test cases
    @DataProvider
    public Object[][] createAssignmentProvider() {
        GradedAssignment emptyAssignment = new GradedAssignment();
        emptyAssignment.setType(AssignmentType.HOMEWORK);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();
        cal.add(Calendar.YEAR, 1); // to get previous year add -1
        Date nextYear = cal.getTime();
        
        GradedAssignment namedAssignment = new GradedAssignment();
        namedAssignment.setType(AssignmentType.QUIZ);
        namedAssignment.setName(localeServiceUtil.generateName());
        namedAssignment.setAssignedDate(today);
        namedAssignment.setDueDate(nextYear);
        
        return new Object[][] {
                { "Empty section assignment", emptyAssignment },
                { "Populated section assignment", namedAssignment },
        };
    }
    
    @Test(dataProvider = "createAssignmentProvider")
    public void createAssignmentTest(String msg, Assignment sectionAssignment) {
        sectionAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section.getId(), sectionAssignment, msg);
        numberOfItemsCreated++;
    }
    
    @Test(dataProvider = "createAssignmentProvider")
    public void deleteAssignmentTest(String msg, Assignment sectionAssignment) {
        Assignment createdSection = sectionAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section.getId(), sectionAssignment, msg);
        sectionAssignmentValidatingExecutor.delete(school.getId(), schoolYear.getId(), term.getId(), section.getId(), createdSection.getId(), msg);
    }
    
    @Test(dataProvider = "createAssignmentProvider")
    public void replaceAssignmentTest(String msg, Assignment sectionAssignment) {
        Assignment createdSection = sectionAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section.getId(), sectionAssignment, msg);
        GradedAssignment a = new GradedAssignment();
        a.setType(AssignmentType.LAB);
        sectionAssignmentValidatingExecutor.replace(school.getId(), schoolYear.getId(), term.getId(), section.getId(), createdSection.getId(), a, msg);
        numberOfItemsCreated++;
    }
    
    @Test(dataProvider = "createAssignmentProvider")
    public void updateAssignmentTest(String msg, Assignment sectionAssignment) {
        Assignment createdSection = sectionAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section.getId(), sectionAssignment, msg);
        Assignment updatedSection = new GradedAssignment();
        updatedSection.setName(localeServiceUtil.generateName());
        //PATCH the existing record with a new name.
        sectionAssignmentValidatingExecutor.update(school.getId(), schoolYear.getId(), term.getId(), section.getId(), createdSection.getId(), updatedSection, msg);
        numberOfItemsCreated++;
    }
    
    @Test
    public void getAllItems() {
        sectionAssignmentValidatingExecutor.getAll(school.getId(), schoolYear.getId(), term.getId(), section.getId(), "Get all records created so far", numberOfItemsCreated++);
    }
    
    //Negative test cases
    @DataProvider
    public Object[][] createSectionNegativeProvider() {
        Assignment gradedSectionNameTooLong = new GradedAssignment();
        gradedSectionNameTooLong.setType(AssignmentType.QUIZ);
        gradedSectionNameTooLong.setName(localeServiceUtil.generateName(257));
        
        return new Object[][] {
                { "Section with name exceeding 256 char limit", gradedSectionNameTooLong, HttpStatus.BAD_REQUEST }
        };
    }
    
    @Test(dataProvider = "createSectionNegativeProvider")
    public void createSectionNegativeTest(String msg, Assignment sectionAssignment, HttpStatus expectedStatus) {
        sectionAssignmentValidatingExecutor.createNegative(school.getId(), schoolYear.getId(), term.getId(), 
                section.getId(), sectionAssignment, expectedStatus, msg);
    }
    
    @Test(dataProvider = "createSectionNegativeProvider")
    public void replaceSectionNegativeTest(String msg, Assignment sectionAssignment, HttpStatus expectedStatus) {
        GradedAssignment a = new GradedAssignment();
        a.setType(AssignmentType.MIDTERM);
        Assignment created = sectionAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section.getId(), a, msg);
        sectionAssignmentValidatingExecutor.replaceNegative(school.getId(), schoolYear.getId(), term.getId(), 
                section.getId(), created.getId(), sectionAssignment, expectedStatus, msg);
    }
}
