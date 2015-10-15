package com.scholarscore.api.persistence.goalCalculators;

import com.scholarscore.api.persistence.StudentAssignmentPersistence;
import com.scholarscore.api.persistence.StudentSectionGradePersistence;
import com.scholarscore.models.AttendanceGoal;
import com.scholarscore.models.CumulativeGradeGoal;
import com.scholarscore.models.StudentAssignment;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.attendance.Attendance;

import java.util.Collection;
import java.util.List;

/**
 * Created by cwallace on 10/5/2015.
 */
public class AttendanceGoalCalc implements GoalCalc<AttendanceGoal> {

    private StudentAssignmentPersistence studentAssignmentPersistence;

    public void setStudentAssignmentPersistence(StudentAssignmentPersistence studentAssignmentPersistence) {
        this.studentAssignmentPersistence = studentAssignmentPersistence;
    }

    public Double calculateGoal(AttendanceGoal goal) {
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
