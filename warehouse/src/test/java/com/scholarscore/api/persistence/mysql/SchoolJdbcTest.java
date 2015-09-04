package com.scholarscore.api.persistence.mysql;

import com.scholarscore.api.persistence.AdministratorPersistence;
import com.scholarscore.api.persistence.mysql.SchoolPersistence;
import com.scholarscore.models.Address;
import com.scholarscore.models.Administrator;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.List;

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
        schoolDao.delete(schoolId);
        assertNull(schoolDao.selectSchool(schoolId), "Expected admin to be removed");
    }
}
