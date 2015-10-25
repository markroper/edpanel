package com.scholarscore.models;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * The AuthorityBuilderUnitTest tests that we can build an equivalent Authority object with a builder and setters
 * Created by cschneider on 10/11/15.
 */
@Test(groups = { "unit" })
public class AuthorityBuilderUnitTest extends AbstractBuilderUnitTest<Authority>{
    @DataProvider
    @Override
    public Object[][] builderProvider() {
        Authority emptyAuthority = new Authority();
        Authority emptyAuthorityByBuilder = new Authority.AuthorityBuilder().build();

        Long userId = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        String authority = RandomStringUtils.randomAlphabetic(10);

        Authority fullAuthority = new Authority();
        fullAuthority.setUserId(userId);
        fullAuthority.setAuthority(authority);

        Authority fullAuthorityByBuilder = new Authority.AuthorityBuilder().
                withUserId(userId).
                withAuthority(authority).
                build();

        return new Object[][]{
                {"Empty authority", emptyAuthorityByBuilder, emptyAuthority},
                {"Full authority", fullAuthorityByBuilder, fullAuthority}
        };
    }
}

