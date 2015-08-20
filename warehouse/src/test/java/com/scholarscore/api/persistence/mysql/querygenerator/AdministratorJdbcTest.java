package com.scholarscore.api.persistence.mysql.querygenerator;

import com.scholarscore.api.persistence.AdministratorPersistence;
import com.scholarscore.api.persistence.mysql.jdbc.AdministratorJdbc;
import com.scholarscore.models.Address;
import com.scholarscore.models.Administrator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

import java.util.logging.Level;

import static org.testng.Assert.*;

/**
 * Created by mattg on 8/18/15.
 */
@Test(groups = {"functional"})
public class AdministratorJdbcTest {
    public void testAdminCrud() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("persistence.xml");
        AdministratorPersistence dao = (AdministratorPersistence) ctx.getBean("administratorPersistence");
        Administrator admin = new Administrator();
        admin.setUsername("mattg");
        admin.setHomePhone("555-1212");
        admin.setName("Matt Greenwood");
        admin.setSourceSystemId("1234");
        Address homeAddress = new Address();
        homeAddress.setCity("Kingston");
        homeAddress.setState("MA");
        homeAddress.setPostalCode("02364");
        homeAddress.setStreet("51 Round Hill Rd");
        admin.setHomeAddress(homeAddress);
        Long adminId = dao.createAdministrator(admin);
        assertNotNull(adminId, "Expected non-null identifier to be returned");
        assertTrue(dao.select(adminId).equals(admin), "Expected administrator from database to equal created administrator");
        dao.delete(adminId);
        assertNull(dao.select(adminId), "Expected admin to be removed");
    }
}
