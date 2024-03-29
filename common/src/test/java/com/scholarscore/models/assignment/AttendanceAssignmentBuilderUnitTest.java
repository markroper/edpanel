package com.scholarscore.models.assignment;

import com.scholarscore.models.AbstractBuilderUnitTest;
import com.scholarscore.models.Section;
import com.scholarscore.utils.CommonTestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;

/**
 * AttendanceAssignmentBuilderUnitTest tests that we can build equivalent objects with setters and a builder
 * Created by cschneider on 10/11/15.
 */
@Test(groups = { "unit" })
public class AttendanceAssignmentBuilderUnitTest extends AbstractBuilderUnitTest<AttendanceAssignment> {

    @DataProvider
    @Override
    public Object[][] builderProvider() {
        AttendanceAssignment emptyAttendanceAssignment = new AttendanceAssignment();
        AttendanceAssignment emptyAttendanceAssignmentByBuilder = new AttendanceAssignment.AttendanceAssignmentBuilder().build();

        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        String name = CommonTestUtils.generateName();
        Long sectionFK = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        Long availablePoints = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        LocalDate dueDate = CommonTestUtils.getRandomLocalDate();
        AssignmentType type = AssignmentType.ATTENDANCE;
        Section section = CommonTestUtils.generateSection();
        Double weight = RandomUtils.nextDouble(0D, Double.MAX_VALUE);

        AttendanceAssignment fullAttendanceAssignment = new AttendanceAssignment();

        fullAttendanceAssignment.setId(id);
        fullAttendanceAssignment.setName(name);
        fullAttendanceAssignment.setSectionFK(sectionFK);
        fullAttendanceAssignment.setSection(section);
        fullAttendanceAssignment.setAvailablePoints(availablePoints);
        fullAttendanceAssignment.setUserDefinedType(name);
        fullAttendanceAssignment.setWeight(weight);
        fullAttendanceAssignment.setIncludeInFinalGrades(true);
        fullAttendanceAssignment.setSourceSystemId(name);
        fullAttendanceAssignment.setDueDate(dueDate);
        fullAttendanceAssignment.setType(type);

        AttendanceAssignment fullAttendanceAssignmentBuilder = new AttendanceAssignment.AttendanceAssignmentBuilder().
                withId(id).
                withName(name).
                withSectionFK(sectionFK).
                withWeight(weight).
                withSection(new Section(section)).
                withAvailablePoints(availablePoints).
                withType(type).
                withDueDate(dueDate).
                withUserDefinedType(name).
                withSourceSystemId(name).
                withIncludeInfinalGrades(true).
                build();

        return new Object[][]{
                {"Empty attendance assignment", emptyAttendanceAssignmentByBuilder, emptyAttendanceAssignment},
                {"Full attendance assignment", fullAttendanceAssignmentBuilder, fullAttendanceAssignment}
        };
    }
}
