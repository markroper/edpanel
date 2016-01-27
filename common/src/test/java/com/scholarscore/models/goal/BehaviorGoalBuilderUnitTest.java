package com.scholarscore.models.goal;

import com.scholarscore.models.AbstractBuilderUnitTest;
import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import com.scholarscore.utils.CommonTestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;

/**
 * BehaviorGoalBuilderUnitTest tests that we can build equivalent objects with setters and a builder
 * Created by cschneider on 10/11/15.
 */
@Test(groups = { "unit" })
public class BehaviorGoalBuilderUnitTest extends AbstractBuilderUnitTest<BehaviorGoal> {

    @DataProvider
    @Override
    public Object[][] builderProvider() {
        BehaviorGoal emptyBehaviorGoal = new BehaviorGoal();
        BehaviorGoal emptyBehaviorGoalByBuilder = new BehaviorGoal.BehaviorGoalBuilder().build();

        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        LocalDate startDate = CommonTestUtils.getRandomLocalDate();
        LocalDate endDate = CommonTestUtils.getRandomLocalDate();
        BehaviorCategory behaviorCategory = CommonTestUtils.getRandomBehaviorCategory();
        Student student = CommonTestUtils.generateStudent();
        Double calculatedValue = RandomUtils.nextDouble(0d, Double.MIN_VALUE);
        Double desiredValue = RandomUtils.nextDouble(0d, Double.MIN_VALUE);
        GoalType goalType = GoalType.BEHAVIOR;
        String name = CommonTestUtils.generateName();
        Staff teacher = CommonTestUtils.generateTeacher();

        BehaviorGoal fullBehaviorGoal = new BehaviorGoal();
        fullBehaviorGoal.setId(id);
        fullBehaviorGoal.setStartDate(startDate);
        fullBehaviorGoal.setEndDate(endDate);
        fullBehaviorGoal.setBehaviorCategory(behaviorCategory);
        fullBehaviorGoal.setStudent(student);
        fullBehaviorGoal.setApproved(Boolean.TRUE);
        fullBehaviorGoal.setCalculatedValue(calculatedValue);
        fullBehaviorGoal.setDesiredValue(desiredValue);
        fullBehaviorGoal.setGoalType(goalType);
        fullBehaviorGoal.setName(name);
        fullBehaviorGoal.setStaff(teacher);

        BehaviorGoal fullBehaviorGoalByBuilder = new BehaviorGoal.BehaviorGoalBuilder().
                withId(id).
                withStartDate(startDate).
                withEndDate(endDate).
                withBehaviorCategory(behaviorCategory).
                withStudent(student).
                withApproved(Boolean.TRUE).
                withCalculatedValue(calculatedValue).
                withDesiredValue(desiredValue).
                withGoalType(goalType).
                withName(name).
                withStaff(teacher).
                build();

        return new Object[][]{
                {"Empty behavior goal", emptyBehaviorGoalByBuilder, emptyBehaviorGoal},
                {"Full behavior goal", fullBehaviorGoalByBuilder, fullBehaviorGoal}
        };
    }
}
