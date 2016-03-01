package com.scholarscore.api.persistence.mysql;

import com.scholarscore.models.user.Student;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by mattg on 8/18/15.
 */
@Test(groups = {"functional"})
public class StudentJdbcTest extends BaseJdbcTest {
    
    public void testStudentCRUD() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("persistence.xml");
        Student createStudent = createStudent();
        Long studentId = createStudent.getId();
        assertNotNull(studentId, "Expected non-null identifier to be returned");
        Student selectStudent = studentDao.select(studentId);
        assertTrue(selectStudent.equals(createStudent), "Expected saved student to be the same as the selected student");
        studentDao.delete(studentId);
        assertNull(studentDao.select(studentId), "Expected admin to be removed");
    }
}
