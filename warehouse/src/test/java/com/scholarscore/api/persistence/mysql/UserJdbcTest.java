package com.scholarscore.api.persistence.mysql;

import com.scholarscore.models.user.User;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by mattg on 8/31/15.
 */
@Test(groups = {"functional"})
public class UserJdbcTest extends BaseJdbcTest {

    public void testUserCrud() {
        User user = createUser();
        assertNotNull(user);

        assertTrue(userDao.selectAllUsers().contains(user), "Expect user to exist in all users list");

        User out = userDao.selectUserByName(user.getUsername());
        assertEquals(out, user, "Expect user identifies to be equal from select method call");
    }



}
