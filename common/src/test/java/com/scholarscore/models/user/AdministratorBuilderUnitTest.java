package com.scholarscore.models.user;

import com.scholarscore.models.AbstractBuilderUnitTest;
import com.scholarscore.models.Address;
import com.scholarscore.utils.CommonTestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test that we can create equivalent administrator objects using setters and builders
 * Created by cschneider on 10/11/15.
 */
@Test(groups = { "unit" })
public class AdministratorBuilderUnitTest extends AbstractBuilderUnitTest<Administrator> {

    @DataProvider
    @Override
    public Object[][] builderProvider() {
        Administrator emptyAdministrator = new Administrator();
        Administrator emptyAdministratorByBuilder = new Administrator.AdministratorBuilder().build();

        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        Address address = CommonTestUtils.generateAddress();
        String sourceSystemId = RandomStringUtils.randomAlphanumeric(10);
        String homePhone = CommonTestUtils.generatePhoneNumber();
        String username = RandomStringUtils.randomAlphanumeric(8);
        String password =  RandomStringUtils.randomAlphanumeric(14);
        Administrator fullAdministrator = new Administrator();
        fullAdministrator.setId(id);
        fullAdministrator.setHomeAddress(address);
        fullAdministrator.setHomePhone(homePhone);
        fullAdministrator.setSourceSystemId(sourceSystemId);
        fullAdministrator.setUsername(username);
        fullAdministrator.setPassword(password);
        fullAdministrator.setEnabled(false);

        Administrator fullAdministratorByBuilder = new Administrator.AdministratorBuilder().withId(id).
                withHomeAddress(address).withHomePhone(homePhone).withSourceSystemid(sourceSystemId).
                withUsername(username).withPassword(password).withEnabled(false).build();

        return new Object[][]{
                {"Empty administrator", emptyAdministratorByBuilder, emptyAdministrator},
                {"Full administrator", fullAdministratorByBuilder, fullAdministrator}
        };
    }
}
