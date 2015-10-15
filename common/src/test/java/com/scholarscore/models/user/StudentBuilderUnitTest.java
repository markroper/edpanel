package com.scholarscore.models.user;

import com.scholarscore.models.AbstractBuilderUnitTest;
import com.scholarscore.models.Address;
import com.scholarscore.models.Gender;
import com.scholarscore.utils.CommonTestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Ensure we can create equivalent Student objects using setters and builder for
 * Created by cschneider on 10/11/15.
 */
@Test
public class StudentBuilderUnitTest extends AbstractBuilderUnitTest<Student> {

    @DataProvider
    @Override
    public Object[][] builderProvider() {
        Student emptyTeacher = new Student();
        Student emptyTeacherByBuilder = new Student.StudentBuilder().build();

        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        String sourceSystemId = RandomStringUtils.randomAlphanumeric(10);
        String homePhone = CommonTestUtils.generatePhoneNumber();
        String username = RandomStringUtils.randomAlphanumeric(8);
        String password =  RandomStringUtils.randomAlphanumeric(14);
        //Source system identifier. E.g. powerschool ID
        //Addresses
        Address mailingAddress = CommonTestUtils.generateAddress();
        Address homeAddress = CommonTestUtils.generateAddress();
        //Demographics
        Gender gender = Gender.FEMALE;
        Date birthDate = new Date();
        Date districtEntryDate= DateUtils.addMonths(new Date(), -36);
        Long projectedGraduationYear = DateUtils.addYears(new Date(), 3).getTime();
        String socialSecurityNumber = CommonTestUtils.generateSocialSecurityNumber();
        //EthnicityRace
        String federalRace = RandomStringUtils.randomAlphabetic(5);
        String federalEthnicity = RandomStringUtils.randomAlphabetic(5);
        Long currentSchoolId = RandomUtils.nextLong(0L, Long.MAX_VALUE);

        Student fullStudent = new Student();
        fullStudent.setId(id);
        fullStudent.setSourceSystemId(sourceSystemId);
        fullStudent.setMailingAddress(mailingAddress);
        fullStudent.setHomeAddress(homeAddress);
        fullStudent.setGender(gender);
        fullStudent.setBirthDate(birthDate);
        fullStudent.setDistrictEntryDate(districtEntryDate);
        fullStudent.setProjectedGraduationYear(projectedGraduationYear);
        fullStudent.setSocialSecurityNumber(socialSecurityNumber);
        fullStudent.setFederalRace(federalRace);
        fullStudent.setFederalEthnicity(federalEthnicity);
        fullStudent.setCurrentSchoolId(currentSchoolId);

        Student fullTeacherByBuilder = new Student.StudentBuilder().
                withId(id).
                withSourceSystemId(sourceSystemId).
                withMailingAddress(mailingAddress).
                withHomeAddress(homeAddress).
                withGender(gender).
                withBirthDate(birthDate).
                withDistrictEntryDate(districtEntryDate).
                withProjectedGraduationYear(projectedGraduationYear).
                withSocialSecurityNumber(socialSecurityNumber).
                withFederalEthnicity(federalEthnicity).
                withFederalRace(federalRace).
                withCurrentSchoolId(currentSchoolId).
                build();

        return new Object[][]{
                {"Empty student", emptyTeacherByBuilder, emptyTeacher},
                {"Full student", fullTeacherByBuilder, fullStudent}
        };
    }
}
