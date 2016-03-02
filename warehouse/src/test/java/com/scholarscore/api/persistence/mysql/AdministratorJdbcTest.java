package com.scholarscore.api.persistence.mysql;

import org.testng.annotations.Test;

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
        assertEquals(adminDao.select(adminId), admin,
                "Expected administrator from database to equal created administrator");
        adminDao.delete(adminId);
        assertNull(adminDao.select(adminId),
                "Expected admin to be removed");
    }
    
}
