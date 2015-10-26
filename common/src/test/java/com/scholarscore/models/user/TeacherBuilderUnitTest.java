package com.scholarscore.models.user;

import com.scholarscore.models.AbstractBuilderUnitTest;
import com.scholarscore.models.Address;
import com.scholarscore.utils.CommonTestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test that we can make equivalent teacher objects using setters and builders
 * Created by cschneider on 10/11/15.
 */
@Test(groups = { "unit" })
public class TeacherBuilderUnitTest extends AbstractBuilderUnitTest<Teacher> {
    @DataProvider
    @Override
    public Object[][] builderProvider() {
        Teacher emptyTeacher = new Teacher();
        Teacher emptyTeacherByBuilder = new Teacher.TeacherBuilder().build();

        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        Address address = CommonTestUtils.generateAddress();
        String sourceSystemId = RandomStringUtils.randomAlphanumeric(10);
        String homePhone = CommonTestUtils.generatePhoneNumber();
        String username = RandomStringUtils.randomAlphanumeric(8);
        String password =  RandomStringUtils.randomAlphanumeric(14);
        Teacher fullTeacher = new Teacher();
        fullTeacher.setId(id);
        fullTeacher.setHomeAddress(address);
        fullTeacher.setHomePhone(homePhone);
        fullTeacher.setSourceSystemId(sourceSystemId);
        fullTeacher.setUsername(username);
        fullTeacher.setPassword(password);
        fullTeacher.setEnabled(false);

        Teacher fullTeacherByBuilder = new Teacher.TeacherBuilder().withId(id).
                withHomeAddress(address).withHomePhone(homePhone).withSourceSystemid(sourceSystemId).
                withUsername(username).withPassword(password).withEnabled(false).build();

        return new Object[][]{
                {"Empty teacher", emptyTeacherByBuilder, emptyTeacher},
                {"Full teacher", fullTeacherByBuilder, fullTeacher}
        };
    }
}
