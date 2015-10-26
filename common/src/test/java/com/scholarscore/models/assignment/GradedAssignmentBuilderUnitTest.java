package com.scholarscore.models.assignment;

import com.scholarscore.models.AbstractBuilderUnitTest;
import com.scholarscore.models.Section;
import com.scholarscore.utils.CommonTestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * GradedAssignmentBuilderUnitTest tests that we can build equivalent GradedAssignment objects with setters and with a
 * builder.
 * Created by cschneider on 10/11/15.
 */
@Test(groups = { "unit" })
public class GradedAssignmentBuilderUnitTest extends AbstractBuilderUnitTest<GradedAssignment> {

    @DataProvider
    @Override
    public Object[][] builderProvider() {
        GradedAssignment emptyGradedAssignment = new GradedAssignment();
        GradedAssignment emptyGradedAssignmentByBuilder = new GradedAssignment.GradedAssignmentBuilder().build();

        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        String name = CommonTestUtils.generateName();
        Long sectionFK = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        Long availablePoints = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        Date dueDate = CommonTestUtils.getRandomDate();
        AssignmentType type = CommonTestUtils.getRandomAssignmentType();
        Section section = CommonTestUtils.generateSection();
        Date assignedDate = CommonTestUtils.getRandomDate();

        GradedAssignment fullGradedAssignment = new GradedAssignment();

        fullGradedAssignment.setId(id);
        fullGradedAssignment.setName(name);
        fullGradedAssignment.setSectionFK(sectionFK);
        fullGradedAssignment.setSection(section);
        fullGradedAssignment.setAvailablePoints(availablePoints);
        fullGradedAssignment.setDueDate(dueDate);
        fullGradedAssignment.setType(type);
        fullGradedAssignment.setAssignedDate(assignedDate);

        GradedAssignment fullGradedAssignmentBuilder = new GradedAssignment.GradedAssignmentBuilder().
                withId(id).
                withName(name).
                withSectionFK(sectionFK).
                withSection(new Section(section)).
                withAvailablePoints(availablePoints).
                withDueDate(dueDate).
                withType(type).
                withAssignedDate(assignedDate).
                build();

        return new Object[][]{
                {"Empty graded assignment", emptyGradedAssignmentByBuilder, emptyGradedAssignment},
                {"Full graded assignment", fullGradedAssignmentBuilder, fullGradedAssignment}
        };
    }
}
