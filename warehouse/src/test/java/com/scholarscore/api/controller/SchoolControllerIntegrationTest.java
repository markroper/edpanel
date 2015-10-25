package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.School;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test(groups = { "integration" })
public class SchoolControllerIntegrationTest extends IntegrationBase {
    
    @BeforeClass
    public void init() {
        authenticate();
    }
    
    //Positive test cases
    @DataProvider
    public Object[][] createSchoolProvider() {
        School emptySchool = new School();
        School namedSchool = new School();
        namedSchool.setName(localeServiceUtil.generateName());
        
        return new Object[][] {
                { "Empty school", emptySchool },
                { "Named school", namedSchool }
        };
    }
    
    @Test(dataProvider = "createSchoolProvider")
    public void createSchoolTest(String msg, School school) {
        schoolValidatingExecutor.create(school, msg);
    }
    
    @Test(dataProvider = "createSchoolProvider")
    public void deleteSchoolTest(String msg, School school) {
        School createdSchool = schoolValidatingExecutor.create(school, msg);
        schoolValidatingExecutor.delete(createdSchool.getId(), msg);
    }
    
    @Test(dataProvider = "createSchoolProvider")
    public void replaceSchoolTest(String msg, School school) {
        School createdSchool = schoolValidatingExecutor.create(school, msg);
        schoolValidatingExecutor.replace(createdSchool.getId(), new School(), msg);
    }
    
    @Test(dataProvider = "createSchoolProvider")
    public void updateSchoolTest(String msg, School school) {
        School createdSchool = schoolValidatingExecutor.create(school, msg);
        School updatedSchool = new School();
        updatedSchool.setName(localeServiceUtil.generateName());
        //PATCH the existing record with a new name.
        schoolValidatingExecutor.update(createdSchool.getId(), updatedSchool, msg);
    }
    
    @Test
    public void getAllItems() {
        schoolValidatingExecutor.getAll("Get all records created so far");
    }
    
    //Negative test cases
    @DataProvider
    public Object[][] createSchoolNegativeProvider() {
        School gradedSchoolNameTooLong = new School();
        gradedSchoolNameTooLong.setName(localeServiceUtil.generateName(257));
        
        return new Object[][] {
                { "School with name exceeding 256 char limit", gradedSchoolNameTooLong, HttpStatus.BAD_REQUEST }
        };
    }
    
    @Test(dataProvider = "createSchoolNegativeProvider")
    public void createSchoolNegativeTest(String msg, School school, HttpStatus expectedStatus) {
        schoolValidatingExecutor.createNegative(school, expectedStatus, msg);
    }
    
    @Test(dataProvider = "createSchoolNegativeProvider")
    public void replaceSchoolNegativeTest(String msg, School school, HttpStatus expectedStatus) {
        School created = schoolValidatingExecutor.create(new School(), msg);
        schoolValidatingExecutor.replaceNegative(created.getId(), school, expectedStatus, msg);
    }
}
