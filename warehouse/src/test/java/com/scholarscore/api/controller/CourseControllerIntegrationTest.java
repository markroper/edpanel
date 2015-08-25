package com.scholarscore.api.controller;

import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;

@Test(groups = { "integration" })
public class CourseControllerIntegrationTest extends IntegrationBase {
    private int numberOfItemsCreated = 0;
    private School school;
    
    @BeforeClass
    public void init() {
        authenticate();
        
        numberOfItemsCreated = 0;
        school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolValidatingExecutor.create(school, "Create base school");
    }
    
    //Positive test cases
    @DataProvider
    public Object[][] createCourseProvider() {
        Course emptyCourse = new Course();
        Course namedCourse = new Course();
        namedCourse.setName(localeServiceUtil.generateName());
        emptyCourse.setSchool(school);
        namedCourse.setSchool(school);
        return new Object[][] {
                { "Empty course", emptyCourse },
                { "Named course", namedCourse }
        };
    }
    
    @Test(dataProvider = "createCourseProvider")
    public void createCourseTest(String msg, Course course) {
        courseValidatingExecutor.create(school.getId(), course, msg);
        numberOfItemsCreated++;
    }
    
    @Test(dataProvider = "createCourseProvider")
    public void deleteCourseTest(String msg, Course course) {
        Course createdCourse = courseValidatingExecutor.create(school.getId(), course, msg);
        courseValidatingExecutor.delete(school.getId(), createdCourse.getId(), msg);
    }
    
    @Test(dataProvider = "createCourseProvider")
    public void replaceCourseTest(String msg, Course course) {
        Course createdCourse = courseValidatingExecutor.create(school.getId(), course, msg);
        courseValidatingExecutor.replace(school.getId(), createdCourse.getId(), new Course(), msg);
        numberOfItemsCreated++;
    }
    
    @Test(dataProvider = "createCourseProvider")
    public void updateCourseTest(String msg, Course course) {
        Course createdCourse = courseValidatingExecutor.create(school.getId(), course, msg);
        Course updatedCourse = new Course();
        updatedCourse.setName(localeServiceUtil.generateName());
        //PATCH the existing record with a new name.
        courseValidatingExecutor.update(school.getId(), createdCourse.getId(), updatedCourse, msg);
        numberOfItemsCreated++;
    }
    
    @Test
    public void getAllItems() {
        courseValidatingExecutor.getAll(school.getId(), "Get all records created so far", numberOfItemsCreated++);
    }
    
    //Negative test cases
    @DataProvider
    public Object[][] createCourseNegativeProvider() {
        Course gradedCourseNameTooLong = new Course();
        gradedCourseNameTooLong.setName(localeServiceUtil.generateName(257));
        
        return new Object[][] {
                { "Course with name exceeding 256 char limit", gradedCourseNameTooLong, HttpStatus.BAD_REQUEST }
        };
    }
    
    @Test(dataProvider = "createCourseNegativeProvider")
    public void createCourseNegativeTest(String msg, Course course, HttpStatus expectedStatus) {
        courseValidatingExecutor.createNegative(school.getId(), course, expectedStatus, msg);
    }
    
    @Test(dataProvider = "createCourseNegativeProvider")
    public void replaceCourseNegativeTest(String msg, Course course, HttpStatus expectedStatus) {
        Course created = courseValidatingExecutor.create(school.getId(), new Course(), msg);
        courseValidatingExecutor.replaceNegative(school.getId(), created.getId(), course, expectedStatus, msg);
    }
}
