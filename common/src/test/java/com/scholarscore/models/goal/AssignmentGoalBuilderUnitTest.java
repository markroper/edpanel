package com.scholarscore.models.goal;

import com.scholarscore.models.AbstractBuilderUnitTest;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.utils.CommonTestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * AssignmentGoalBuilderUnitTest tests out the AssignmentGoal object's Builder to ensure that constructing an object with the builder
 * Created by cschneider on 10/11/15.
 */
@Test(groups = { "unit" })
public class AssignmentGoalBuilderUnitTest extends AbstractBuilderUnitTest<AssignmentGoal> {

    @DataProvider
    @Override
    public Object[][] builderProvider() {
        AssignmentGoal emptyAssignmentGoal = new AssignmentGoal();
        AssignmentGoal emptyAssignmentGoalByBuilder = new AssignmentGoal.AssignmentGoalBuilder().build();

        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        Long parentId = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        Student student = CommonTestUtils.generateStudent();
        Double calculatedValue = RandomUtils.nextDouble(0d, Double.MIN_VALUE);
        Double desiredValue = RandomUtils.nextDouble(0d, Double.MIN_VALUE);
        GoalType goalType = CommonTestUtils.getRandomGoalType();
        String name = CommonTestUtils.generateName();
        Teacher teacher = CommonTestUtils.generateTeacher();

        AssignmentGoal fullAssignmentGoal = new AssignmentGoal();
        fullAssignmentGoal.setId(id);
        fullAssignmentGoal.setParentId(parentId);
        fullAssignmentGoal.setStudent(student);
        fullAssignmentGoal.setApproved(Boolean.TRUE);
        fullAssignmentGoal.setCalculatedValue(calculatedValue);
        fullAssignmentGoal.setDesiredValue(desiredValue);
        fullAssignmentGoal.setGoalType(goalType);
        fullAssignmentGoal.setName(name);
        fullAssignmentGoal.setTeacher(teacher);

        AssignmentGoal fullAssignmentGoalByBuilder = new AssignmentGoal.AssignmentGoalBuilder().
                withId(id).
                withParentId(parentId).
                withStudent(student).
                withApproved(Boolean.TRUE).
                withCalculatedValue(calculatedValue).
                withDesiredValue(desiredValue).
                withGoalType(goalType).
                withName(name).
                withTeacher(teacher).
                build();

        return new Object[][]{
                {"Empty assignment goal", emptyAssignmentGoalByBuilder, emptyAssignmentGoal},
                {"Full assignment goal", fullAssignmentGoalByBuilder, fullAssignmentGoal}
        };
    }
}
