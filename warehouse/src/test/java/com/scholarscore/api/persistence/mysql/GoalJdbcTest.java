package com.scholarscore.api.persistence.mysql;

import com.scholarscore.models.BehaviorGoal;
import com.scholarscore.models.Course;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Created by cwallace on 9/23/2015.
 */
@Test(groups = { "functional"})
public class GoalJdbcTest extends BaseJdbcTest {
    public void testGoalCrud() {
        BehaviorGoal goalToCreate = new BehaviorGoal(behaviorGoal);
        goalToCreate.setStudent(createStudent());
        goalToCreate.setTeacher(createTeacher());
        Long goalId = goalDao.createGoal(goalToCreate.getStudent().getId(), goalToCreate);
        assertNotNull(goalId, "Expected non-null sectionId from create of section");

        goalDao.delete(goalToCreate.getStudent().getId(), goalId);
        assertNull(goalDao.select(goalToCreate.getStudent().getId(), goalId), "Expected section to be null after delete operation");
    }
}
