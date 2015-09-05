package com.scholarscore.api.persistence.mysql;

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
public class AdministratorJdbcTest extends BaseJdbcTest {
    public void testAdminCrud() {
        Long adminId = adminDao.createAdministrator(admin);
        assertNotNull(adminId,
                "Expected non-null identifier to be returned");
        assertTrue(adminDao.select(adminId).equals(admin),
                "Expected administrator from database to equal created administrator");
        adminDao.delete(adminId);
        assertNull(adminDao.select(adminId),
                "Expected admin to be removed");
    }
}
