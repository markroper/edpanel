package com.scholarscore.models;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * SubjectAreaBuilderUnitTest tests that we can create equivalent SubjectArea objects using setters and builders
 * Created by cschneider on 10/11/15.
 */
@Test(groups = { "unit" })
public class SubjectAreaBuilderUnitTest extends AbstractBuilderUnitTest<SubjectArea>{
    @DataProvider
    @Override
    public Object[][] builderProvider() {
        SubjectArea emptySubjectArea = new SubjectArea();
        SubjectArea emptySubjectAreaByBuilder = new SubjectArea.SubjectAreaBuilder().build();

        String name = RandomStringUtils.randomAlphabetic(15);
        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);

        SubjectArea fullSubjectArea = new SubjectArea();
        fullSubjectArea.setId(id);
        fullSubjectArea.setName(name);

        SubjectArea fullSubjectAreaBuilder = new SubjectArea.SubjectAreaBuilder()
                .withId(id)
                .withName(name)
                .build();

        return new Object[][]{
                {"Empty subject area", emptySubjectAreaByBuilder, emptySubjectArea},
                {"Full subject area", fullSubjectAreaBuilder, fullSubjectArea},
        };
    }
}
