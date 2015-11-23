package com.scholarscore.models;

import com.beust.jcommander.internal.Lists;
import com.scholarscore.utils.CommonTestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

/**
 * SchoolYearBuilderUnitTest tests that we can build equivalent SchoolYear objects using both setters and builders
 * Created by cschneider on 10/11/15.
 */
@Test(groups = { "unit" })
public class SchoolYearBuilderUnitTest extends AbstractBuilderUnitTest<SchoolYear>{

    @DataProvider
    @Override
    public Object[][] builderProvider() {
        SchoolYear emptySchoolYear = new SchoolYear();
        SchoolYear emptySchoolYearByBuilder = new SchoolYear.SchoolYearBuilder().build();

        String name = RandomStringUtils.randomAlphabetic(15);
        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        School parentSchool = CommonTestUtils.generateSchool();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(9);
        List<Term> terms = Lists.newArrayList();
        int numTerms = RandomUtils.nextInt(2, 5);
        for(int i = 0; i < numTerms; i++){
            terms.add(CommonTestUtils.generateTermWithoutSchoolYear(startDate, startDate.plusMonths(12l/numTerms)));
        }
        SchoolYear fullSchoolYear = new SchoolYear();
        fullSchoolYear.setId(id);
        fullSchoolYear.setName(name);
        fullSchoolYear.setStartDate(startDate);
        fullSchoolYear.setEndDate(endDate);
        fullSchoolYear.setSchool(parentSchool);
        fullSchoolYear.setTerms(terms);

        SchoolYear fullSchoolYearBuilder = new SchoolYear.SchoolYearBuilder()
                .withId(id)
                .withName(name)
                .withStartDate(startDate)
                .withEndDate(endDate)
                .withTerms(terms)
                .withSchool(parentSchool)
                .build();

        return new Object[][]{
                {"Empty school year", emptySchoolYearByBuilder, emptySchoolYear},
                {"Full school year", fullSchoolYearBuilder, fullSchoolYear},
        };
    }
}
