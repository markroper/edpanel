package com.scholarscore.api.persistence.mysql;

import com.scholarscore.api.persistence.mysql.jdbc.BaseJdbc;
import com.scholarscore.models.StudentSectionGrade;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by mattg on 8/31/15.
 */
@Test(groups = {"functional"})
public class StudentSectionGradeJdbcTest extends BaseJdbcTest {

    public void testStudentSectionGradeCrud() {
        StudentSectionGrade studentSectionGrade = createStudentSectionGrade();
        assertNotNull(studentSectionGrade, "Unexpected null student section grade from create call");

        assertEquals(studentSectionGradeDao.select(studentSectionGrade.getSection().getId(), studentSectionGrade.getStudent().getId()), studentSectionGrade,
                "Expected equality in studentSectionGrade object from select method call");
        assertTrue(studentSectionGradeDao.selectAll(studentSectionGrade.getSection().getId()).contains(studentSectionGrade),
                "Expected selectAll to contain the newly created StudentSectionGrade object");

        assertTrue(studentSectionGradeDao.selectAllByStudent(studentSectionGrade.getStudent().getId()).contains(studentSectionGrade),
                "Expected select all by student with student id from test object contains the test object");
        assertTrue(studentSectionGradeDao.selectAll(studentSectionGrade.getSection().getId()).contains(studentSectionGrade),
                "Unexpected missing student section grade from list all for section");
        studentSectionGradeDao.delete(studentSectionGrade.getSection().getId(), studentSectionGrade.getStudent().getId());

        assertNull(studentSectionGradeDao.select(studentSectionGrade.getSection().getId(), studentSectionGrade.getStudent().getId()),  "Expected student section grade query is null after delete method call");
    }
}
