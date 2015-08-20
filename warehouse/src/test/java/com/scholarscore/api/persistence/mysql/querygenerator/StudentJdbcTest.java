package com.scholarscore.api.persistence.mysql.querygenerator;

import com.scholarscore.api.persistence.AdministratorPersistence;
import com.scholarscore.api.persistence.mysql.StudentPersistence;
import com.scholarscore.models.Address;
import com.scholarscore.models.Administrator;
import com.scholarscore.models.Student;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by mattg on 8/18/15.
 */
@Test(groups = {"functional"})
public class StudentJdbcTest {
    public void testStudentCRUD() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("persistence.xml");
        StudentPersistence dao = (StudentPersistence) ctx.getBean("studentPersistence");
        Student student = new Student();
        student.setUsername("mattg");
        //student.setCurrentSchoolId(10L);
        student.setName("Matt Greenwood");
        student.setSourceSystemId("1234");
        Address homeAddress = new Address();
        homeAddress.setCity("Kingston");
        homeAddress.setState("MA");
        homeAddress.setPostalCode("02364");
        homeAddress.setStreet("51 Round Hill Rd");
        student.setHomeAddress(homeAddress);
        Long studentId = dao.createStudent(student);
        assertNotNull(studentId, "Expected non-null identifier to be returned");
        Student selectStudent = dao.select(studentId);
        assertTrue(selectStudent.equals(student), "Expected saved student to be the same as the selected student");
        dao.delete(studentId);
        assertNull(dao.select(studentId), "Expected admin to be removed");
    }
}
