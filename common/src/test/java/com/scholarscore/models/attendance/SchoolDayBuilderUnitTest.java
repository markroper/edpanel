package com.scholarscore.models.attendance;

import com.scholarscore.models.AbstractBuilderUnitTest;
import com.scholarscore.models.School;
import com.scholarscore.models.user.Student;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * SchoolDayBuilderUnitTest tests out the SchoolDay object's Builder to ensure that constructing an object with the builder
 * is equivalent to creating one with setters.
 * Created by cschneider on 10/11/15.
 */
@Test(groups = { "unit" })
public class SchoolDayBuilderUnitTest extends AbstractBuilderUnitTest<SchoolDay>{
    @DataProvider
    @Override
    public Object[][] builderProvider() {
        SchoolDay emptySchoolDay = new SchoolDay();
        SchoolDay emptySchoolDayByBuilder = new SchoolDay.SchoolDayBuilder().build();

        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        School school = new School();
        SchoolDay fullSchoolDay = new SchoolDay();
        SchoolDay fullSchoolDayByBuilder = new SchoolDay.SchoolDayBuilder().
                withId(id).withSchool(school).withDate(new Date()).build();
        return new Object[][]{
                {"Empty schoolDay", emptySchoolDayByBuilder, emptySchoolDay},
                {"Full schoolDay", fullSchoolDayByBuilder, fullSchoolDay}
        };
    }
}
