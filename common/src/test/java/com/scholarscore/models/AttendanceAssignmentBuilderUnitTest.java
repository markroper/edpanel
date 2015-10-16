package com.scholarscore.models;

import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.utils.CommonTestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Created by cschneider on 10/11/15.
 */
@Test
public class AttendanceAssignmentBuilderUnitTest extends AbstractBuilderUnitTest<AttendanceAssignment>{

    @DataProvider
    @Override
    public Object[][] builderProvider() {
        AttendanceAssignment emptyAttendanceAssignment = new AttendanceAssignment();
        AttendanceAssignment emptyAttendanceAssignmentByBuilder = new AttendanceAssignment.AttendanceAssignmentBuilder().build();

        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        String name = CommonTestUtils.generateName();
        Long sectionFK = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        Long availablePoints = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        Date dueDate = CommonTestUtils.getRandomDate();
        AssignmentType type = AssignmentType.ATTENDANCE;
        Section section = CommonTestUtils.generateSection();

        AttendanceAssignment fullAttendanceAssignment = new AttendanceAssignment();

        fullAttendanceAssignment.setId(id);
        fullAttendanceAssignment.setName(name);
        fullAttendanceAssignment.setSectionFK(sectionFK);
        fullAttendanceAssignment.setSection(section);
        fullAttendanceAssignment.setAvailablePoints(availablePoints);
        fullAttendanceAssignment.setDueDate(dueDate);
        fullAttendanceAssignment.setType(type);

        AttendanceAssignment fullAttendanceAssignmentBuilder = new AttendanceAssignment.AttendanceAssignmentBuilder().
                withId(id).
                withName(name).
                withSectionFK(sectionFK).
                withSection(new Section(section)).
                withAvailablePoints(availablePoints).
                withDueDate(dueDate).
                withType(type).
                build();

        return new Object[][]{
                {"Empty schoolDay", emptyAttendanceAssignmentByBuilder, emptyAttendanceAssignment},
                {"Full schoolDay", fullAttendanceAssignmentBuilder, fullAttendanceAssignment}
        };
    }
}
