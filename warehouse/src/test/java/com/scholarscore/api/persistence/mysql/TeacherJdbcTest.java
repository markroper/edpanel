package com.scholarscore.api.persistence.mysql;

import com.scholarscore.models.user.Staff;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertEquals;

/**
 * Created by mattg on 8/18/15.
 */
@Test(groups = {"functional"})
public class TeacherJdbcTest extends BaseJdbcTest {
    public void testCreateTeacher() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("persistence.xml");
        Staff createdTeacher = createTeacher();
        Long id = createdTeacher.getId();
        Staff out = teacherDao.select(id);
        assertEquals(out, createdTeacher, "Expected teacher values to be equal");
        deleteTeacherAndVerify();
    }
}
