package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Term;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.Date;

@Test(groups = { "integration" })
public class TermControllerIntegrationTest extends IntegrationBase {
    private int numberOfItemsCreated = 0;
    private School school;
    private SchoolYear schoolYear;
    
    @BeforeClass
    public void init() {
        authenticate();
        numberOfItemsCreated = 0;
        school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolValidatingExecutor.create(school, "Create base school");
        
        schoolYear = new SchoolYear();
        schoolYear.setName(localeServiceUtil.generateName());
        schoolYear.setSchool(school);
        schoolYear = schoolYearValidatingExecutor.create(school.getId(), schoolYear, "create base schoolYear");
    }
    
    //Positive test cases
    @DataProvider
    public Object[][] createTermProvider() {
        Term emptyTerm = new Term();
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();
        cal.add(Calendar.YEAR, 1); // to get previous year add -1
        Date nextYear = cal.getTime();
        
        Term namedTerm = new Term();
        namedTerm.setName(localeServiceUtil.generateName());
        namedTerm.setStartDate(today);
        namedTerm.setEndDate(nextYear);
        
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
        termValidatingExecutor.replace(school.getId(), schoolYear.getId(), createdTerm.getId(), new Term(createdTerm), msg);
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
