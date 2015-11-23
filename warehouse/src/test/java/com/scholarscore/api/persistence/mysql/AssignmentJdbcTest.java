package com.scholarscore.api.persistence.mysql;

import com.scholarscore.models.Section;
import com.scholarscore.models.assignment.AssignmentType;
import com.scholarscore.models.assignment.GradedAssignment;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Created by mattg on 8/29/15.
 */
@Test(groups = {"functional"})
public class AssignmentJdbcTest extends BaseJdbcTest {

    public void testGradedAssignmentCrud() {

        Section section = createSection();

        GradedAssignment assignment = new GradedAssignment();
        assignment.setName("Section Test 1");
        assignment.setAvailablePoints(10L);
        assignment.setType(AssignmentType.ATTENDANCE);
        assignment.setDueDate(LocalDate.now());
        assignment.setSection(createSection());
        Long id = assignmentDao.insert(section.getId(), assignment);
        assertNotNull(id, "Expected assignment id to not be null");

        GradedAssignment outputAssignment = (GradedAssignment) assignmentDao.select(section.getId(), id);
        assertNotNull(outputAssignment, "Expected select statement to generate non-null assigment output from select method call");

        assignmentDao.delete(id);

        outputAssignment = (GradedAssignment) assignmentDao.select(section.getId(), id);

        assertNull(outputAssignment, "Expected after delete method call assignment to be null");
    }
}
