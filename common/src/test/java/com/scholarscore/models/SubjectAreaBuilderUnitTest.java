package com.scholarscore.models;

import com.beust.jcommander.internal.Sets;
import com.scholarscore.utils.CommonTestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.Set;

/**
 * SubjectAreaBuilderUnitTest tests that we can create equivalent SubjectArea objects using setters and builders
 * Created by cschneider on 10/11/15.
 */
@Test
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
