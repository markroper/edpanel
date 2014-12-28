package com.scholarscore.api.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.School;
import com.scholarscore.models.Term;

@Test(groups = { "integration" })
public class SchoolYearControllerIntegrationTest extends IntegrationBase {
    private int numberOfItemsCreated = 0;
    private School school;
    
    @BeforeClass
    public void init() {
        numberOfItemsCreated = 0;
        school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolValidatingExecutor.create(school, "Create base school");
    }
    
    //Positive test cases
    @DataProvider
    public Object[][] createSchoolYearProvider() {
        SchoolYear emptySchoolYear = new SchoolYear();
        
        SchoolYear namedSchoolYear = new SchoolYear();
        namedSchoolYear.setName(localeServiceUtil.generateName());
        
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.YEAR, 1); // to get previous year add -1
        Date nextYear = cal.getTime();
        SchoolYear schoolYearWithDates = new SchoolYear(namedSchoolYear);
        schoolYearWithDates.setStartDate(today);
        schoolYearWithDates.setEndDate(nextYear);
        
        SchoolYear schoolYearWithTerms = new SchoolYear(schoolYearWithDates);
        Map<Long, Term> terms = new HashMap<>();
        Term term1 = new Term();
        term1.setStartDate(today);
        term1.setEndDate(nextYear);
        term1.setName(localeServiceUtil.generateName());
        terms.put(1l, term1);
        Term term2 = new Term(term1);
        term2.setName(localeServiceUtil.generateName());
        terms.put(2l, term2);
        schoolYearWithTerms.setTerms(terms);
       
        return new Object[][] {
                { "Empty schoolYear", emptySchoolYear },
                { "Named schoolYear", namedSchoolYear },
                { "Start and end date schoolYear", schoolYearWithDates },
        };
    }
    
    @Test(dataProvider = "createSchoolYearProvider")
    public void createSchoolYearTest(String msg, SchoolYear schoolYear) {
        schoolYearValidatingExecutor.create(school.getId(), schoolYear, msg);
        numberOfItemsCreated++;
    }
    
    @Test(dataProvider = "createSchoolYearProvider")
    public void deleteSchoolYearTest(String msg, SchoolYear schoolYear) {
        SchoolYear createdSchoolYear = schoolYearValidatingExecutor.create(school.getId(), schoolYear, msg);
        schoolYearValidatingExecutor.delete(school.getId(), createdSchoolYear.getId(), msg);
    }
    
    @Test(dataProvider = "createSchoolYearProvider")
    public void replaceSchoolYearTest(String msg, SchoolYear schoolYear) {
        SchoolYear createdSchoolYear = schoolYearValidatingExecutor.create(school.getId(), schoolYear, msg);
        schoolYearValidatingExecutor.replace(school.getId(), createdSchoolYear.getId(), new SchoolYear(), msg);
        numberOfItemsCreated++;
    }
    
    @Test(dataProvider = "createSchoolYearProvider")
    public void updateSchoolYearTest(String msg, SchoolYear schoolYear) {
        SchoolYear createdSchoolYear = schoolYearValidatingExecutor.create(school.getId(), schoolYear, msg);
        SchoolYear updatedSchoolYear = new SchoolYear();
        updatedSchoolYear.setName(localeServiceUtil.generateName());
        //PATCH the existing record with a new name.
        schoolYearValidatingExecutor.update(school.getId(), createdSchoolYear.getId(), updatedSchoolYear, msg);
        numberOfItemsCreated++;
    }
    
    @Test
    public void getAllItems() {
        schoolYearValidatingExecutor.getAll(school.getId(), "Get all records created so far", numberOfItemsCreated++);
    }
    
    //Negative test cases
    @DataProvider
    public Object[][] createSchoolYearNegativeProvider() {
        SchoolYear gradedSchoolYearNameTooLong = new SchoolYear();
        gradedSchoolYearNameTooLong.setName(localeServiceUtil.generateName(257));
        
        return new Object[][] {
                { "SchoolYear with name exceeding 256 char limit", gradedSchoolYearNameTooLong, HttpStatus.BAD_REQUEST }
        };
    }
    
    @Test(dataProvider = "createSchoolYearNegativeProvider")
    public void createSchoolYearNegativeTest(String msg, SchoolYear schoolYear, HttpStatus expectedStatus) {
        schoolYearValidatingExecutor.createNegative(school.getId(), schoolYear, expectedStatus, msg);
    }
    
    @Test(dataProvider = "createSchoolYearNegativeProvider")
    public void replaceSchoolYearNegativeTest(String msg, SchoolYear schoolYear, HttpStatus expectedStatus) {
        SchoolYear created = schoolYearValidatingExecutor.create(school.getId(), new SchoolYear(), msg);
        schoolYearValidatingExecutor.replaceNegative(school.getId(), created.getId(), schoolYear, expectedStatus, msg);
    }
}
