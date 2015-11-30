package com.scholarscore.models;

import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.utils.CommonTestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;

/**
 * BehaviorBuilderUnitTest tests that we can build an equivalent Behavior object with setters and with a builder
 * Created by cschneider on 10/11/15.
 */
@Test(groups = { "unit" })
public class BehaviorBuilderUnitTest extends AbstractBuilderUnitTest<Behavior>{
    @DataProvider
    @Override
    public Object[][] builderProvider() {
        Behavior emptyBehavior = new Behavior();
        Behavior emptyBehaviorByBuilder = new Behavior.BehaviorBuilder().build();

        String remoteStudentId = RandomStringUtils.random(10);
        String remoteSystem = RandomStringUtils.random(10);
        String remoteBehaviorId = RandomStringUtils.random(10);
        LocalDate behaviorDate = CommonTestUtils.getRandomLocalDate();
        BehaviorCategory behaviorCategory = CommonTestUtils.getRandomBehaviorCategory();
        String pointValue = RandomStringUtils.random(10);
        String roster = RandomStringUtils.random(10);
        Student student = CommonTestUtils.generateStudent();
        Teacher teacher = CommonTestUtils.generateTeacher();


        Behavior fullBehavior = new Behavior();
        fullBehavior.setRemoteStudentId(remoteStudentId);
        fullBehavior.setRemoteSystem(remoteSystem);
        fullBehavior.setRemoteBehaviorId(remoteBehaviorId);
        fullBehavior.setBehaviorDate(behaviorDate);
        fullBehavior.setBehaviorCategory(behaviorCategory);
        fullBehavior.setPointValue(pointValue);
        fullBehavior.setRoster(roster);
        fullBehavior.setStudent(student);
        fullBehavior.setAssigner(teacher);

        Behavior fullBehaviorByBuilder = new Behavior.BehaviorBuilder().
                withRemoteStudentId(remoteStudentId).
                withRemoteSystem(remoteSystem).
                withRemoteBehaviorId(remoteBehaviorId).
                withBehaviorDate(behaviorDate).
                withBehaviorCategory(behaviorCategory).
                withPointValue(pointValue).
                withRoster(roster).
                withStudent(student).
                withAssigner(teacher).
                build();

        return new Object[][]{
                {"Empty behavior", emptyBehaviorByBuilder, emptyBehavior},
                {"Full behavior", fullBehaviorByBuilder, fullBehavior}
        };
    }
}
