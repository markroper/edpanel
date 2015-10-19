package com.scholarscore.models;

import com.scholarscore.utils.CommonTestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * GradeLevelBuilderUnitTest tests that we can build equivalent GradeLevel objects out of setters and a builder
 * Created by cschneider on 10/11/15.
 */
@Test
public class GradeLevelBuilderUnitTest extends AbstractBuilderUnitTest<GradeLevel>{
    @DataProvider
    @Override
    public Object[][] builderProvider() {
        GradeLevel emptyGradeLevel = new GradeLevel();
        GradeLevel emptyGradeLevelByBuilder = new GradeLevel.GradeLevelBuilder().build();

        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        String name = CommonTestUtils.generateName();

        GradeLevel fullGradeLevel = new GradeLevel();
        fullGradeLevel.setId(id);
        fullGradeLevel.setName(name);

        GradeLevel fullGradeLevelBuilder = new GradeLevel.GradeLevelBuilder().
                withId(id).
                withName(name).
                build();

        return new Object[][]{
                {"Empty grade level", emptyGradeLevelByBuilder, emptyGradeLevel},
                {"Full grade level", fullGradeLevelBuilder, fullGradeLevel},

        };

    }
}
