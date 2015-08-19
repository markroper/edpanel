package com.scholarscore.api.persistence.mysql.querygenerator;

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

/**
 * Created by mattg on 8/18/15.
 */
@Test(groups = {"functional"})
public class TeacherJdbcTest {
    public void testCreateTeacher() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("persistence.xml");
        TeacherPersistence dao = (TeacherPersistence) ctx.getBean("teacherPersistence");
        Teacher teacher = new Teacher();
        teacher.setUsername("mattg");
        teacher.setHomePhone("555-1212");
        teacher.setSourceSystemId("abc");
        Address homeAddress = new Address();
        homeAddress.setCity("Kingston");
        homeAddress.setState("MA");
        homeAddress.setPostalCode("02364");
        homeAddress.setStreet("51 Round Hill Rd");
        teacher.setHomeAddress(homeAddress);
        Long id = dao.createTeacher(teacher);
        assertNotNull(id, "Expected non-null identifier to be returned");
        Teacher out = dao.select(id);
        assertEquals(out, teacher, "Expected teacher values to be equal");
    }
}
