package com.scholarscore.api.persistence.mysql;

import com.scholarscore.models.Course;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.testng.Assert.*;

/**
 * Created by mattg on 8/29/15.
 */
@Test(groups = { "functional" })
public class CourseJdbcTest extends BaseJdbcTest {

    public void testCourseCrud() {

        Course course = createCourse();
        Long courseId = course.getId();
        Long schoolId = course.getSchool().getId();

        assertNotNull(courseId, "Unexpected null course id upon course creation");
        Course selectCourse = courseDao.select(schoolId, courseId);

        assertEquals(selectCourse, course, "Unexpected course output from select method");
        Collection<Course> courses = courseDao.selectAll(schoolId);
        assertTrue(courses.contains(selectCourse), "Expected a list all for courses shows the inserted course");
        deleteCourseAndVerify();
    }
}
