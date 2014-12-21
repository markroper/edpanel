package com.scholarscore.api.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.Student;
import com.scholarscore.models.Term;

@Test(groups = { "integration" })
public class SectionControllerIntegrationTest extends IntegrationBase {
    private int numberOfItemsCreated = 0;
    private School school;
    private SchoolYear schoolYear;
    private Term term;
    
    @BeforeClass
    public void init() {
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
    }
    
    //Positive test cases
    @DataProvider
    public Object[][] createSectionProvider() {
        Section emptySection = new Section();
        
        Section namedSection = new Section();
        namedSection.setName(localeServiceUtil.generateName());
        namedSection.setStartDate(new Date(1234567L));
        namedSection.setEndDate(new Date(123456L));
        
        
        Section fullSection = new Section(namedSection);
        fullSection.setRoom(localeServiceUtil.generateName());
        Map<Long, Student> students = new HashMap<>();
        Student s = new Student();
        s.setId(2L);
        s.setName(localeServiceUtil.generateName());
        students.put(s.getId(), s);
        fullSection.setEnrolledStudents(students);
        
        return new Object[][] {
                { "Empty section", emptySection },
                { "Populated section", namedSection },
                { "Students populated on section", namedSection },
        };
    }
    
    @Test(dataProvider = "createSectionProvider")
    public void createSectionTest(String msg, Section section) {
        sectionValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section, msg);
        numberOfItemsCreated++;
    }
    
    @Test(dataProvider = "createSectionProvider")
    public void deleteSectionTest(String msg, Section section) {
        Section createdSection = sectionValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section, msg);
        sectionValidatingExecutor.delete(school.getId(), schoolYear.getId(), term.getId(), createdSection.getId(), msg);
    }
    
    @Test(dataProvider = "createSectionProvider")
    public void replaceSectionTest(String msg, Section section) {
        Section createdSection = sectionValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section, msg);
        sectionValidatingExecutor.replace(school.getId(), schoolYear.getId(), term.getId(), createdSection.getId(), new Section(), msg);
        numberOfItemsCreated++;
    }
    
    @Test(dataProvider = "createSectionProvider")
    public void updateSectionTest(String msg, Section section) {
        Section createdSection = sectionValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section, msg);
        Section updatedSection = new Section();
        updatedSection.setName(localeServiceUtil.generateName());
        //PATCH the existing record with a new name.
        sectionValidatingExecutor.update(school.getId(), schoolYear.getId(), term.getId(), createdSection.getId(), updatedSection, msg);
        numberOfItemsCreated++;
    }
    
    @Test
    public void getAllItems() {
        sectionValidatingExecutor.getAll(school.getId(), schoolYear.getId(), term.getId(), "Get all records created so far", numberOfItemsCreated++);
    }
    
    //Negative test cases
    @DataProvider
    public Object[][] createSectionNegativeProvider() {
        Section gradedSectionNameTooLong = new Section();
        gradedSectionNameTooLong.setName(localeServiceUtil.generateName(257));
        
        return new Object[][] {
                { "Section with name exceeding 256 char limit", gradedSectionNameTooLong, HttpStatus.BAD_REQUEST }
        };
    }
    
    @Test(dataProvider = "createSectionNegativeProvider")
    public void createSectionNegativeTest(String msg, Section section, HttpStatus expectedStatus) {
        sectionValidatingExecutor.createNegative(school.getId(), schoolYear.getId(), term.getId(), section, expectedStatus, msg);
    }
    
    @Test(dataProvider = "createSectionNegativeProvider")
    public void replaceSectionNegativeTest(String msg, Section section, HttpStatus expectedStatus) {
        Section created = sectionValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), new Section(), msg);
        sectionValidatingExecutor.replaceNegative(school.getId(), schoolYear.getId(), term.getId(), created.getId(), section, expectedStatus, msg);
    }
}
