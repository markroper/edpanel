package com.scholarscore.api.controller;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.SectionAssignment;
import com.scholarscore.models.Term;

@Test(groups = { "integration" })
public class SectionAssignmentControllerIntegrationTest extends IntegrationBase {
    private int numberOfItemsCreated = 0;
    private School school;
    private SchoolYear schoolYear;
    private Term term;
    private Section section;
    
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
        
        section = new Section();
        section.setName(localeServiceUtil.generateName());
        section = sectionValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section, "create test base term");
    }
    
    //Positive test cases
    @DataProvider
    public Object[][] createSectionAssignmentProvider() {
        SectionAssignment emptySectionAssignment = new SectionAssignment();
        
        SectionAssignment namedSectionAssignment = new SectionAssignment();
        namedSectionAssignment.setName(localeServiceUtil.generateName());
        namedSectionAssignment.setAssignedDate(new Date(1234567L));
        namedSectionAssignment.setDueDate(new Date(123456L));
        namedSectionAssignment.setAssignmentId(2L);
        
        return new Object[][] {
                { "Empty section assignment", emptySectionAssignment },
                { "Populated section assignment", namedSectionAssignment },
        };
    }
    
    @Test(dataProvider = "createSectionAssignmentProvider")
    public void createSectionAssignmentTest(String msg, SectionAssignment sectionAssignment) {
        sectionAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section.getId(), sectionAssignment, msg);
        numberOfItemsCreated++;
    }
    
    @Test(dataProvider = "createSectionAssignmentProvider")
    public void deleteSectionAssignmentTest(String msg, SectionAssignment sectionAssignment) {
        SectionAssignment createdSection = sectionAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section.getId(), sectionAssignment, msg);
        sectionAssignmentValidatingExecutor.delete(school.getId(), schoolYear.getId(), term.getId(), section.getId(), createdSection.getId(), msg);
    }
    
    @Test(dataProvider = "createSectionAssignmentProvider")
    public void replaceSectionAssignmentTest(String msg, SectionAssignment sectionAssignment) {
        SectionAssignment createdSection = sectionAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section.getId(), sectionAssignment, msg);
        sectionAssignmentValidatingExecutor.replace(school.getId(), schoolYear.getId(), term.getId(), section.getId(), createdSection.getId(), new SectionAssignment(), msg);
        numberOfItemsCreated++;
    }
    
    @Test(dataProvider = "createSectionAssignmentProvider")
    public void updateSectionAssignmentTest(String msg, SectionAssignment sectionAssignment) {
        SectionAssignment createdSection = sectionAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section.getId(), sectionAssignment, msg);
        SectionAssignment updatedSection = new SectionAssignment();
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
        SectionAssignment gradedSectionNameTooLong = new SectionAssignment();
        gradedSectionNameTooLong.setName(localeServiceUtil.generateName(257));
        
        return new Object[][] {
                { "Section with name exceeding 256 char limit", gradedSectionNameTooLong, HttpStatus.BAD_REQUEST }
        };
    }
    
    @Test(dataProvider = "createSectionNegativeProvider")
    public void createSectionNegativeTest(String msg, SectionAssignment sectionAssignment, HttpStatus expectedStatus) {
        sectionAssignmentValidatingExecutor.createNegative(school.getId(), schoolYear.getId(), term.getId(), 
                section.getId(), sectionAssignment, expectedStatus, msg);
    }
    
    @Test(dataProvider = "createSectionNegativeProvider")
    public void replaceSectionNegativeTest(String msg, SectionAssignment sectionAssignment, HttpStatus expectedStatus) {
        SectionAssignment created = sectionAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section.getId(), new SectionAssignment(), msg);
        sectionAssignmentValidatingExecutor.replaceNegative(school.getId(), schoolYear.getId(), term.getId(), 
                section.getId(), created.getId(), sectionAssignment, expectedStatus, msg);
    }
}
