package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Created by cwallace on 9/21/2015.
 */
@Test(groups = { "integration" })
public class GoalControllerIntegrationTest extends IntegrationBase {

    private Student student;
    private Teacher teacher;

    @BeforeClass
    public void init() {
        authenticate();

        student = new Student();
        student.setName(localeServiceUtil.generateName());
        student = studentValidatingExecutor.create(student, "create base student");

        teacher = new Teacher();
        teacher.setName(localeServiceUtil.generateName());
        teacher = teacherValidatingExecutor.create(teacher, "create base teacher");
    }

    @DataProvider(name = "createGoalDataProvider")
    public Object[][] createGoalDataMethod() {
        BehaviorGoal behaviorGoal = new BehaviorGoal();
        behaviorGoal.setStudent(student);
        behaviorGoal.setTeacher(teacher);
        behaviorGoal.setBehaviorCategory(BehaviorCategory.MERIT);
        behaviorGoal.setStartDate(new Date());
        behaviorGoal.setEndDate(new Date());
        behaviorGoal.setDesiredValue(4l);
        behaviorGoal.setName("To win them all");
        behaviorGoal.setApproved(false);

        AssignmentGoal assGoal = new AssignmentGoal();
        assGoal.setStudent(student);
        assGoal.setTeacher(teacher);
        assGoal.setName("The final final");
        assGoal.setApproved(false);
        assGoal.setParentId(1L);
        assGoal.setDesiredValue(95L);

        return new Object[][] {
                {behaviorGoal, "Test failed with a behavior goal"},
                {assGoal, "Test failed with an assignment goal"}
        };
    }

    @Test(dataProvider = "createGoalDataProvider" )
    public void createTest(Goal goal, String msg) {
        goalValidatingExecutor.create(goal.getStudent().getId(), goal, msg);
    }

    @Test(dataProvider = "createGoalDataProvider")
    public void deleteGoalTest(Goal goal, String msg) {
        Goal createdGoal = goalValidatingExecutor.create(goal.getStudent().getId(), goal, msg);
        goalValidatingExecutor.delete(student.getId(), createdGoal.getId(), msg);
    }
}
