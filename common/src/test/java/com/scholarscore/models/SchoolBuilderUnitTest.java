package com.scholarscore.models;

import com.scholarscore.utils.CommonTestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * SchoolBuilderUnitTest tests that we can build equivalent school objects using setters or a builder
 * Created by cschneider on 10/11/15.
 */
@Test(groups = { "unit" })
public class SchoolBuilderUnitTest extends AbstractBuilderUnitTest<School>{

    @DataProvider
    @Override
    public Object[][] builderProvider() {
        School emptySchool = new School();
        School emptySchoolByBuilder = new School.SchoolBuilder().build();

        Address address = CommonTestUtils.generateAddress();
        String phoneNumber = CommonTestUtils.generatePhoneNumber();
        String sourceSystemId = RandomStringUtils.randomAlphanumeric(10);
        String principalEmail = CommonTestUtils.generateEmail();
        String principalName = CommonTestUtils.generateName();
        String name = RandomStringUtils.randomAlphabetic(15);
        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);

        School fullSchool = new School();
        fullSchool.setId(id);
        fullSchool.setName(name);
        fullSchool.setAddress(address);
        fullSchool.setMainPhone(phoneNumber);
        fullSchool.setSourceSystemId(sourceSystemId);
        fullSchool.setPrincipalEmail(principalEmail);
        fullSchool.setPrincipalName(principalName);

        School fullSchoolBuilder = new School.SchoolBuilder()
                .withAddress(address)
                .withMainPhone(phoneNumber)
                .withSourceSystemId(sourceSystemId)
                .withPrincipalEmail(principalEmail)
                .withPrincipalName(principalName)
                .withName(name)
                .withId(id)
                .build();

        return new Object[][]{
                {"Empty school", emptySchoolByBuilder, emptySchool},
                {"Full school", fullSchoolBuilder, fullSchool},
        };

    }
}
