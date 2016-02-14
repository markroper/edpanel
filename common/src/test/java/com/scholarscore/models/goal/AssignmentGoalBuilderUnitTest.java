package com.scholarscore.models.goal;

import com.scholarscore.models.AbstractBuilderUnitTest;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
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
        GoalType goalType = GoalType.ASSIGNMENT;
        String name = CommonTestUtils.generateName();
        Staff teacher = CommonTestUtils.generateTeacher();
        StudentAssignment studAss = new StudentAssignment();

        AssignmentGoal fullAssignmentGoal = new AssignmentGoal();
        fullAssignmentGoal.setId(id);
        fullAssignmentGoal.setStudentAssignment(studAss);
        fullAssignmentGoal.setStudent(student);
        fullAssignmentGoal.setApproved(Boolean.TRUE);
        fullAssignmentGoal.setCalculatedValue(calculatedValue);
        fullAssignmentGoal.setDesiredValue(desiredValue);
        fullAssignmentGoal.setGoalType(goalType);
        fullAssignmentGoal.setName(name);
        fullAssignmentGoal.setStaff(teacher);

        AssignmentGoal fullAssignmentGoalByBuilder = new AssignmentGoal.AssignmentGoalBuilder().
                withId(id).
                withStudentAsssignment(studAss).
                withStudent(student).
                withApproved(Boolean.TRUE).
                withCalculatedValue(calculatedValue).
                withDesiredValue(desiredValue).
                withGoalType(goalType).
                withName(name).
                withStaff(teacher).
                build();

        return new Object[][]{
                {"Empty assignment goal", emptyAssignmentGoalByBuilder, emptyAssignmentGoal},
                {"Full assignment goal", fullAssignmentGoalByBuilder, fullAssignmentGoal}
        };
    }
}
