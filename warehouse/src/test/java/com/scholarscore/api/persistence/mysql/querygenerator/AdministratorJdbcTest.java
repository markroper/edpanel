package com.scholarscore.api.persistence.mysql.querygenerator;

import com.scholarscore.api.persistence.AdministratorPersistence;
import com.scholarscore.api.persistence.mysql.jdbc.AdministratorJdbc;
import com.scholarscore.models.Address;
import com.scholarscore.models.Administrator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

import java.util.logging.Level;

import static org.testng.Assert.assertNotNull;

/**
 * Created by mattg on 8/18/15.
 */
@Test(groups = {"functional"})
public class AdministratorJdbcTest {
    public void testSaveAdmin() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("persistence.xml");
        AdministratorPersistence dao = (AdministratorPersistence) ctx.getBean("administratorPersistence");
        Administrator admin = new Administrator();
        admin.setUsername("mattg");
        admin.setHomePhone("555-1212");
        Address homeAddress = new Address();
        homeAddress.setCity("Kingston");
        homeAddress.setState("MA");
        homeAddress.setPostalCode("02364");
        homeAddress.setStreet("51 Round Hill Rd");
        admin.setHomeAddress(homeAddress);
        assertNotNull(dao.createAdministrator(admin), "Expected non-null identifier to be returned");
    }
}
