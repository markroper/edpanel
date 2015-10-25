package com.scholarscore.models;

import com.scholarscore.utils.CommonTestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * CourseBuilderUnitTest tests that we can create equivalent objects out of a builder and using setters
 * Created by cschneider on 10/11/15.
 */
@Test(groups = { "unit" })
public class CourseBuilderUnitTest extends AbstractBuilderUnitTest<Course>{

    @DataProvider
    @Override
    public Object[][] builderProvider() {
        Course emptyCourse = new Course();
        Course emptyCourseByBuilder = new Course.CourseBuilder().build();

        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        String name = RandomStringUtils.randomAlphabetic(10);
        String number = RandomStringUtils.randomAlphabetic(10);
        String sourceSystemId = RandomStringUtils.randomAlphanumeric(10);
        School school = CommonTestUtils.generateSchool();

        Course fullCourse = new Course();
        fullCourse.setId(id);
        fullCourse.setNumber(number);
        fullCourse.setSourceSystemId(sourceSystemId);
        fullCourse.setSchool(school);
        fullCourse.setName(name);

        Course fullCourseByBuilder = new Course.CourseBuilder().
                withId(id).
                withName(name).
                withNumber(number).
                withSourceSystemId(sourceSystemId).
                withSchool(school).
                build();

        return new Object[][]{
                {"Empty course", emptyCourseByBuilder, emptyCourse},
                {"Full course", fullCourseByBuilder, fullCourse}
        };
    }
}
