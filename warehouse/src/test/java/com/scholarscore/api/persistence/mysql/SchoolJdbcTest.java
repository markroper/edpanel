package com.scholarscore.api.persistence.mysql;

import com.scholarscore.models.School;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by mattg on 8/18/15.
 */
@Test(groups = {"functional"})
public class SchoolJdbcTest extends BaseJdbcTest {
    public void testSchoolCrud() {
        School createdSchool = createSchool();
        Long schoolId = createdSchool.getId();
        assertNotNull(schoolId, "Expected non-null identifier to be returned");
        assertTrue(schoolDao.selectSchool(schoolId).equals(createdSchool), "Expected school from database to equal created administrator");
        deleteSchoolAndVerify();
    }
}
