package com.scholarscore.models.assignment;

import com.scholarscore.models.AbstractBuilderUnitTest;
import com.scholarscore.models.Section;
import com.scholarscore.models.user.Student;
import com.scholarscore.utils.CommonTestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * StudentAssignmentBuilderUnitTest checks that we can make equivalent objects with setters and builders
 * Created by cschneider on 10/11/15.
 */
@Test(groups = { "unit" })
public class StudentAssignmentBuilderUnitTest extends AbstractBuilderUnitTest<StudentAssignment> {
    @DataProvider
    @Override
    public Object[][] builderProvider() {
        StudentAssignment emptyStudentAssignment = new StudentAssignment();
        StudentAssignment emptyStudentAssignmentByBuilder = new StudentAssignment.StudentAssignmentBuilder().build();

        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        String name = CommonTestUtils.generateName();
        Boolean completed = CommonTestUtils.getRandomBoolean();
        Date completionDate = CommonTestUtils.getRandomDate();
        Double awardedPoints = RandomUtils.nextDouble(0d, Double.MAX_VALUE);
        Section section = CommonTestUtils.generateSection();
        Assignment assignment = CommonTestUtils.generateAssignment(AssignmentType.CLASSWORK, section);
        Student student = CommonTestUtils.generateStudent();
        section.addEnrolledStudent(student);

        StudentAssignment fullStudentAssignment = new StudentAssignment();

        fullStudentAssignment.setId(id);
        fullStudentAssignment.setName(name);
        fullStudentAssignment.setCompletionDate(completionDate);
        fullStudentAssignment.setAwardedPoints(awardedPoints);
        fullStudentAssignment.setAssignment(assignment);
        fullStudentAssignment.setStudent(student);

        StudentAssignment fullStudentAssignmentBuilder = new StudentAssignment.StudentAssignmentBuilder()
                .withId(id)
                .withName(name)
                .withCompleted(completed)
                .withCompletionDate(completionDate)
                .withAwardedPoints(awardedPoints)
                .withAssignment(assignment)
                .withStudent(student)
                .build();

        return new Object[][]{
                {"Empty student assignment", emptyStudentAssignmentByBuilder, emptyStudentAssignment},
                {"Full student assignment", fullStudentAssignmentBuilder, fullStudentAssignment}
        };
    }
}
