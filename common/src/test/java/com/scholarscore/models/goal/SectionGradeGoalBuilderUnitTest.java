package com.scholarscore.models.goal;

import com.scholarscore.models.AbstractBuilderUnitTest;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import com.scholarscore.utils.CommonTestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * SectionGradeGoalBuilderUnitTest tests
 * Created by cschneider on 10/11/15.
 */
@Test(groups = { "unit" })
public class SectionGradeGoalBuilderUnitTest extends AbstractBuilderUnitTest<SectionGradeGoal> {

    @DataProvider
    @Override
    public Object[][] builderProvider() {
        SectionGradeGoal emptySectionGradeGoal = new SectionGradeGoal();
        SectionGradeGoal emptySectionGradeGoalByBuilder = new SectionGradeGoal.CumulativeGradeGoalBuilder().build();

        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        Long parentId = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        Student student = CommonTestUtils.generateStudent();
        Double calculatedValue = RandomUtils.nextDouble(0d, Double.MIN_VALUE);
        Double desiredValue = RandomUtils.nextDouble(0d, Double.MIN_VALUE);
        GoalType goalType = GoalType.SECTION_GRADE;
        String name = CommonTestUtils.generateName();
        Staff teacher = CommonTestUtils.generateTeacher();

        SectionGradeGoal fullSectionGradeGoal = new SectionGradeGoal();
        fullSectionGradeGoal.setId(id);
        fullSectionGradeGoal.setParentId(parentId);
        fullSectionGradeGoal.setStudent(student);
        fullSectionGradeGoal.setApproved(Boolean.TRUE);
        fullSectionGradeGoal.setCalculatedValue(calculatedValue);
        fullSectionGradeGoal.setDesiredValue(desiredValue);
        fullSectionGradeGoal.setGoalType(goalType);
        fullSectionGradeGoal.setName(name);
        fullSectionGradeGoal.setStaff(teacher);

        SectionGradeGoal fullSectionGradeGoalByBuilder = new SectionGradeGoal.CumulativeGradeGoalBuilder().
                withId(id).
                withParentId(parentId).
                withStudent(student).
                withApproved(Boolean.TRUE).
                withCalculatedValue(calculatedValue).
                withDesiredValue(desiredValue).
                withGoalType(goalType).
                withName(name).
                withStaff(teacher).
                build();

        return new Object[][]{
                {"Empty cumulative grade goal", emptySectionGradeGoalByBuilder, emptySectionGradeGoal},
                {"Full cumulative grade goal", fullSectionGradeGoalByBuilder, fullSectionGradeGoal}
        };
    }
}
