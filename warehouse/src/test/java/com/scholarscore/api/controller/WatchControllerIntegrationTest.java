package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.StudentWatch;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.fail;

/**
 * Created by cwallace on 4/19/16.
 */
@Test(groups = { "integration" })
public class WatchControllerIntegrationTest extends IntegrationBase {

    private Student student;
    private Staff teacher;
    private Staff teacher2;
    private Boolean initialized;

    @BeforeClass
    public void init() {
        authenticate();

        student = new Student();
        student.setName(localeServiceUtil.generateName());
        student = studentValidatingExecutor.create(student, "create base student");

        teacher = new Staff();
        teacher.setIsTeacher(true);
        teacher.setName(localeServiceUtil.generateName());
        teacher = teacherValidatingExecutor.create(teacher, "create base teacher");

        teacher2 = new Staff();
        teacher2.setIsTeacher(true);
        teacher2.setName(localeServiceUtil.generateName());
        teacher2 = teacherValidatingExecutor.create(teacher, "create base teacher");
        initialized = true;

    }

    @DataProvider(name = "createWatchDataProvider")
    public Object[][] createWatchDataMethod() {
        if (!initialized) { fail("Failed to initialize"); }
        StudentWatch studentWatch = new StudentWatch();
        studentWatch.setStaff(teacher);
        studentWatch.setStudent(student);

        StudentWatch studentWatch2 = new StudentWatch();
        studentWatch2.setStaff(teacher2);
        studentWatch2.setStudent(student);

        return new Object[][] {
                {studentWatch, "Test failed with a student watch"},
                {studentWatch2, "Test failed with a second student watch"}
        };
    }

    @Test(dataProvider = "createWatchDataProvider" )
    public void createGetDeleteTest(StudentWatch watch, String msg) {
        watchValidatingExecutor.create(watch, msg);
        testGetAllStaff(watch.getStaff(), 1);
        deleteWatchTest(watch, msg);
    }

    public void deleteWatchTest(StudentWatch watch, String msg) {
        watchValidatingExecutor.delete(watch.getId(), msg);
    }


    public void testGetAllStaff(Staff staff, int expectedItems) {
        watchValidatingExecutor.getAllStaff(staff.getId(),"Get all test failed", expectedItems);
    }

}