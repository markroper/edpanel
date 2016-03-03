package com.scholarscore.api.persistence.mysql;

import com.scholarscore.models.user.Staff;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by mattg on 8/18/15.
 */
@Test(groups = {"functional"})
public class AdministratorJdbcTest extends BaseJdbcTest {
    public void testAdminCrud() {
        Staff administrator = createAdmin();
        Long id = administrator.getId();
        assertEquals(adminDao.select(id), administrator,
                "Expected administrator from database to equal created administrator");
        adminDao.delete(id);
        assertNull(adminDao.select(id),
                "Expected admin to be removed");
    }
    
}
