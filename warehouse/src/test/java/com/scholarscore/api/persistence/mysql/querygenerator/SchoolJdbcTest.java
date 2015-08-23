package com.scholarscore.api.persistence.mysql.querygenerator;

import com.scholarscore.api.persistence.AdministratorPersistence;
import com.scholarscore.api.persistence.mysql.SchoolPersistence;
import com.scholarscore.models.Address;
import com.scholarscore.models.Administrator;
import com.scholarscore.models.School;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by mattg on 8/18/15.
 */
@Test(groups = {"functional"})
public class SchoolJdbcTest {
    public void testSchoolCrud() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("persistence.xml");
        SchoolPersistence dao = (SchoolPersistence) ctx.getBean("schoolPersistence");
        School school = new School();
        school.setName("school-test");
        school.setMainPhone("781-555-1212");
        school.setPrincipalEmail("matt@mjgreenwood.net");
        school.setPrincipalName("Matt Greenwood");
        Address schoolMainAddress = new Address();
        schoolMainAddress.setCity("Kingston");
        schoolMainAddress.setState("MA");
        schoolMainAddress.setPostalCode("02364");
        schoolMainAddress.setStreet("51 Round Hill Rd");
        school.setAddress(schoolMainAddress);
        Long schoolId = dao.createSchool(school);
        assertNotNull(schoolId, "Expected non-null identifier to be returned");
        assertTrue(dao.selectSchool(schoolId).equals(school), "Expected school from database to equal created administrator");
        dao.delete(schoolId);
        assertNull(dao.selectSchool(schoolId), "Expected admin to be removed");
    }
}
