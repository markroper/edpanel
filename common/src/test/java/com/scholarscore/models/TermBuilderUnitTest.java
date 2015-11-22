package com.scholarscore.models;

import com.scholarscore.utils.CommonTestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;

/**
 * TermBuilderUnitTest ensures that we create equivalent Term objects when using setters and builders
 * Created by cschneider on 10/11/15.
 */
@Test(groups = { "unit" })
public class TermBuilderUnitTest extends AbstractBuilderUnitTest<Term>{
    @DataProvider
    @Override
    public Object[][] builderProvider() {
        Term emptyTerm = new Term();
        Term emptyTermByBuilder = new Term.TermBuilder().build();

        String name = RandomStringUtils.randomAlphabetic(15);
        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        LocalDate startDate = CommonTestUtils.getRandomLocalDate();
        LocalDate endDate = CommonTestUtils.getRandomLocalDate();
        SchoolYear schoolYear = CommonTestUtils.generateSchoolYear(CommonTestUtils.generateSchool());
        
        Term fullTerm = new Term();
        fullTerm.setId(id);
        fullTerm.setName(name);
        fullTerm.setStartDate(startDate);
        fullTerm.setEndDate(endDate);
        fullTerm.setSchoolYear(schoolYear);

        Term fullTermBuilder = new Term.TermBuilder()
                .withId(id)
                .withName(name)
                .withStartDate(startDate)
                .withEndDate(endDate)
                .withSchoolYear(schoolYear)
                .build();

        return new Object[][]{
                {"Empty term", emptyTermByBuilder, emptyTerm},
                {"Full term", fullTermBuilder, fullTerm},
        };
    }
}
