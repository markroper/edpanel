package com.scholarscore.api.persistence.mysql;

import com.scholarscore.models.AssignmentType;
import com.scholarscore.models.GradedAssignment;
import com.scholarscore.models.Section;
import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.Assert.assertEquals;
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
        assignment.setDueDate(new Date());
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
