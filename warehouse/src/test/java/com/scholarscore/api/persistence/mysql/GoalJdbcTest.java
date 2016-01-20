package com.scholarscore.api.persistence.mysql;

import com.scholarscore.models.goal.AssignmentGoal;
import com.scholarscore.models.goal.BehaviorGoal;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Created by cwallace on 9/23/2015.
 */
@Test(groups = { "functional"})
public class GoalJdbcTest extends BaseJdbcTest {
    public void testBehaviorGoalCrud() {
        BehaviorGoal goalToCreate = new BehaviorGoal(behaviorGoal);
        goalToCreate.setStudent(createStudent());
        goalToCreate.setStaff(createTeacher());
        Long goalId = goalDao.createGoal(goalToCreate.getStudent().getId(), goalToCreate);
        assertNotNull(goalId, "Expected non-null sectionId from create of section");

        goalDao.delete(goalToCreate.getStudent().getId(), goalId);
        assertNull(goalDao.select(goalToCreate.getStudent().getId(), goalId), "Expected section to be null after delete operation");
    }

    public void testAssignmentGoalCrud() {
        AssignmentGoal goalToCreate = new AssignmentGoal(assignmentGoal);
        goalToCreate.setStudent(createStudent());
        goalToCreate.setStaff(createTeacher());
        Long goalId = goalDao.createGoal(goalToCreate.getStudent().getId(), goalToCreate);
        assertNotNull(goalId, "Expected non-null sectionId from create of section");

        goalDao.delete(goalToCreate.getStudent().getId(), goalId);
        assertNull(goalDao.select(goalToCreate.getStudent().getId(), goalId), "Expected section to be null after delete operation");
    }
}
