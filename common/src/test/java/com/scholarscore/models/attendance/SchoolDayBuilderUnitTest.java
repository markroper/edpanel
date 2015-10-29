package com.scholarscore.models.attendance;

import com.scholarscore.models.AbstractBuilderUnitTest;
import com.scholarscore.models.School;
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
        Date date = new Date();
        SchoolDay fullSchoolDay = new SchoolDay();
        fullSchoolDay.setSchool(school);
        fullSchoolDay.setId(id);
        fullSchoolDay.setDate(date);
        SchoolDay fullSchoolDayByBuilder = new SchoolDay.SchoolDayBuilder().
                withId(id).withSchool(school).withDate(date).build();
        return new Object[][]{
                {"Empty schoolDay", emptySchoolDayByBuilder, emptySchoolDay},
                {"Full schoolDay", fullSchoolDayByBuilder, fullSchoolDay}
        };
    }
}