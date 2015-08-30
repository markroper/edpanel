package com.scholarscore.api.persistence.mysql;

import com.scholarscore.api.persistence.AdministratorPersistence;
import com.scholarscore.api.persistence.mysql.TeacherPersistence;
import com.scholarscore.models.Address;
import com.scholarscore.models.Administrator;
import com.scholarscore.models.Teacher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Created by mattg on 8/18/15.
 */
@Test(groups = {"functional"})
public class TeacherJdbcTest extends BaseJdbcTest {
    public void testCreateTeacher() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("persistence.xml");
        Long id = teacherDao.createTeacher(teacher);
        assertNotNull(id, "Expected non-null identifier to be returned");
        Teacher out = teacherDao.select(id);
        assertEquals(out, teacher, "Expected teacher values to be equal");
        teacherDao.delete(teacher.getId());
        Teacher deletedTeacher = teacherDao.select(id);
        assertNull(deletedTeacher, "Expected deleted teacher to be null");
    }
}
