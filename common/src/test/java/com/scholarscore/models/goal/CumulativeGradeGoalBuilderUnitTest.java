package com.scholarscore.models.goal;

import com.scholarscore.models.AbstractBuilderUnitTest;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.utils.CommonTestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * CumulativeGradeGoalBuilderUnitTest tests
 * Created by cschneider on 10/11/15.
 */
@Test(groups = { "unit" })
public class CumulativeGradeGoalBuilderUnitTest extends AbstractBuilderUnitTest<CumulativeGradeGoal> {

    @DataProvider
    @Override
    public Object[][] builderProvider() {
        CumulativeGradeGoal emptyCumulativeGradeGoal = new CumulativeGradeGoal();
        CumulativeGradeGoal emptyCumulativeGradeGoalByBuilder = new CumulativeGradeGoal.CumulativeGradeGoalBuilder().build();

        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        Long parentId = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        Student student = CommonTestUtils.generateStudent();
        Double calculatedValue = RandomUtils.nextDouble(0d, Double.MIN_VALUE);
        Double desiredValue = RandomUtils.nextDouble(0d, Double.MIN_VALUE);
        GoalType goalType = GoalType.CUMULATIVE_GRADE;
        String name = CommonTestUtils.generateName();
        Teacher teacher = CommonTestUtils.generateTeacher();

        CumulativeGradeGoal fullCumulativeGradeGoal = new CumulativeGradeGoal();
        fullCumulativeGradeGoal.setId(id);
        fullCumulativeGradeGoal.setParentId(parentId);
        fullCumulativeGradeGoal.setStudent(student);
        fullCumulativeGradeGoal.setApproved(Boolean.TRUE);
        fullCumulativeGradeGoal.setCalculatedValue(calculatedValue);
        fullCumulativeGradeGoal.setDesiredValue(desiredValue);
        fullCumulativeGradeGoal.setGoalType(goalType);
        fullCumulativeGradeGoal.setName(name);
        fullCumulativeGradeGoal.setStaff(teacher);

        CumulativeGradeGoal fullCumulativeGradeGoalByBuilder = new CumulativeGradeGoal.CumulativeGradeGoalBuilder().
                withId(id).
                withParentId(parentId).
                withStudent(student).
                withApproved(Boolean.TRUE).
                withCalculatedValue(calculatedValue).
                withDesiredValue(desiredValue).
                withGoalType(goalType).
                withName(name).
                withPerson(teacher).
                build();

        return new Object[][]{
                {"Empty cumulative grade goal", emptyCumulativeGradeGoalByBuilder, emptyCumulativeGradeGoal},
                {"Full cumulative grade goal", fullCumulativeGradeGoalByBuilder, fullCumulativeGradeGoal}
        };
    }
}
