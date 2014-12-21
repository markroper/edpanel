package com.scholarscore.api.controller;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Term;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.School;

@Test(groups = { "integration" })
public class TermControllerIntegrationTest extends IntegrationBase {
    private int numberOfItemsCreated = 0;
    private School school;
    private SchoolYear schoolYear;
    
    @BeforeClass
    public void init() {
        numberOfItemsCreated = 0;
        school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolValidatingExecutor.create(school, "Create base school");
        
        schoolYear = new SchoolYear();
        schoolYear.setName(localeServiceUtil.generateName());
        schoolYear = schoolYearValidatingExecutor.create(school.getId(), schoolYear, "create base schoolYear");
    }
    
    //Positive test cases
    @DataProvider
    public Object[][] createTermProvider() {
        Term emptyTerm = new Term();
        
        Term namedTerm = new Term();
        namedTerm.setName(localeServiceUtil.generateName());
        namedTerm.setStartDate(new Date(1234567L));
        namedTerm.setEndDate(new Date(123456L));
        
        return new Object[][] {
                { "Empty term", emptyTerm },
                { "Populated term", namedTerm }
        };
    }
    
    @Test(dataProvider = "createTermProvider")
    public void createTermTest(String msg, Term term) {
        termValidatingExecutor.create(school.getId(), schoolYear.getId(), term, msg);
        numberOfItemsCreated++;
    }
    
    @Test(dataProvider = "createTermProvider")
    public void deleteTermTest(String msg, Term term) {
        Term createdTerm = termValidatingExecutor.create(school.getId(), schoolYear.getId(), term, msg);
        termValidatingExecutor.delete(school.getId(), schoolYear.getId(), createdTerm.getId(), msg);
    }
    
    @Test(dataProvider = "createTermProvider")
    public void replaceTermTest(String msg, Term term) {
        Term createdTerm = termValidatingExecutor.create(school.getId(), schoolYear.getId(), term, msg);
        termValidatingExecutor.replace(school.getId(), schoolYear.getId(), createdTerm.getId(), new Term(), msg);
        numberOfItemsCreated++;
    }
    
    @Test(dataProvider = "createTermProvider")
    public void updateTermTest(String msg, Term term) {
        Term createdTerm = termValidatingExecutor.create(school.getId(), schoolYear.getId(), term, msg);
        Term updatedTerm = new Term();
        updatedTerm.setName(localeServiceUtil.generateName());
        //PATCH the existing record with a new name.
        termValidatingExecutor.update(school.getId(), schoolYear.getId(), createdTerm.getId(), updatedTerm, msg);
        numberOfItemsCreated++;
    }
    
    @Test
    public void getAllItems() {
        termValidatingExecutor.getAll(school.getId(), schoolYear.getId(), "Get all records created so far", numberOfItemsCreated++);
    }
    
    //Negative test cases
    @DataProvider
    public Object[][] createTermNegativeProvider() {
        Term gradedTermNameTooLong = new Term();
        gradedTermNameTooLong.setName(localeServiceUtil.generateName(257));
        
        return new Object[][] {
                { "Term with name exceeding 256 char limit", gradedTermNameTooLong, HttpStatus.BAD_REQUEST }
        };
    }
    
    @Test(dataProvider = "createTermNegativeProvider")
    public void createTermNegativeTest(String msg, Term term, HttpStatus expectedStatus) {
        termValidatingExecutor.createNegative(school.getId(), schoolYear.getId(), term, expectedStatus, msg);
    }
    
    @Test(dataProvider = "createTermNegativeProvider")
    public void replaceTermNegativeTest(String msg, Term term, HttpStatus expectedStatus) {
        Term created = termValidatingExecutor.create(school.getId(), schoolYear.getId(), new Term(), msg);
        termValidatingExecutor.replaceNegative(school.getId(), schoolYear.getId(), created.getId(), term, expectedStatus, msg);
    }

}
