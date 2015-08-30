package com.scholarscore.api.persistence.mysql;

import com.scholarscore.models.GradedAssignment;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Created by mattg on 8/29/15.
 */
@Test(groups = {"functional"})
public class AssignmentJdbcTest extends BaseJdbcTest {

    public void testGradedAssignmentCrud() {
        GradedAssignment assignment = new GradedAssignment();
        assignment.setName("Section Test 1");
        assignment.setAvailablePoints(10L);
        assignment.setDueDate(new Date());
    }
}
