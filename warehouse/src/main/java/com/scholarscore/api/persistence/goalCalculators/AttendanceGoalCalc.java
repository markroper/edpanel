package com.scholarscore.api.persistence.goalCalculators;

import com.scholarscore.api.persistence.StudentAssignmentPersistence;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.goal.CalculatableAttendance;

import java.util.Collection;

/**
 * Created by cwallace on 10/5/2015.
 */
public class AttendanceGoalCalc implements GoalCalc<CalculatableAttendance> {

    private StudentAssignmentPersistence studentAssignmentPersistence;

    public void setStudentAssignmentPersistence(StudentAssignmentPersistence studentAssignmentPersistence) {
        this.studentAssignmentPersistence = studentAssignmentPersistence;
    }

    public Double calculateGoal(CalculatableAttendance goal) {
        Collection<StudentAssignment> attendances = studentAssignmentPersistence.selectAllAttendanceSection(goal.getParentId(), goal.getStudent().getId());
        Double missedClasses = 0d;
        for (StudentAssignment dayAttendance : attendances) {
            if (dayAttendance.getAssignment().getDueDate().after(goal.getEndDate()) ||
                    dayAttendance.getAssignment().getDueDate().before(goal.getStartDate())) {
                continue;
            }
            Long points = dayAttendance.getAwardedPoints();
            if (null == points) {

            } else if (points == 0) {
                missedClasses += 1;
            }

        }
        return missedClasses;
    }
}
