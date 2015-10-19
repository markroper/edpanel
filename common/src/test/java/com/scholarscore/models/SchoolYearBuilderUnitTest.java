package com.scholarscore.models;

import com.scholarscore.utils.CommonTestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.LinkedHashSet;

/**
 * SchoolYearBuilderUnitTest tests that we can build equivalent SchoolYear objects using both setters and builders
 * Created by cschneider on 10/11/15.
 */
@Test
public class SchoolYearBuilderUnitTest extends AbstractBuilderUnitTest<SchoolYear>{

    @DataProvider
    @Override
    public Object[][] builderProvider() {
        SchoolYear emptySchoolYear = new SchoolYear();
        SchoolYear emptySchoolYearByBuilder = new SchoolYear.SchoolYearBuilder().build();

        String name = RandomStringUtils.randomAlphabetic(15);
        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        School parentSchool = CommonTestUtils.generateSchool();
        Date startDate = new Date();
        Date endDate = DateUtils.addMonths(startDate, 9);


        SchoolYear fullSchoolYear = new SchoolYear();
        fullSchoolYear.setId(id);
        fullSchoolYear.setName(name);
        fullSchoolYear.setStartDate(startDate);
        fullSchoolYear.setEndDate(endDate);
        fullSchoolYear.setSchool(parentSchool);
        fullSchoolYear.setTerms(new LinkedHashSet<Term>());

        SchoolYear fullSchoolYearBuilder = new SchoolYear.SchoolYearBuilder()
                .withId(id)
                .withName(name)
                .withStartDate(startDate)
                .withEndDate(endDate)
                .withTerms(new LinkedHashSet<Term>())
                .withSchool(parentSchool)
                .build();

        return new Object[][]{
                {"Empty school year", emptySchoolYearByBuilder, emptySchoolYear},
                {"Full school year", fullSchoolYearBuilder, fullSchoolYear},
        };

    }
}
