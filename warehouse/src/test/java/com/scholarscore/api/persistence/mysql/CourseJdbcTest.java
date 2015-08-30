package com.scholarscore.api.persistence.mysql;

import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.testng.Assert.*;

/**
 * Created by mattg on 8/29/15.
 */
@Test(groups = { "functional" })
public class CourseJdbcTest extends BaseJdbcTest {

    public void testCourseCrud() {
        Long schoolId = schoolDao.createSchool(school);
        School createdSchool = schoolDao.selectSchool(schoolId);
        assertNotNull(createdSchool, "Expected created school to not be null upon select");

        Long courseId = courseDao.insert(schoolId, course);

        assertNotNull(courseId, "Unexpected null course id upon course creation");
        Course selectCourse = courseDao.select(schoolId, courseId);

        assertEquals(selectCourse, course, "Unexpected course output from select method");
        Collection<Course> courses = courseDao.selectAll(schoolId);
        assertTrue(courses.contains(selectCourse), "Expected a list all for courses shows the inserted course");
        courseDao.delete(courseId);
        Course expectNullCourse = courseDao.select(schoolId, courseId);
        assertNull(expectNullCourse, "Expected the course after delete to be null upon select");
    }
}
