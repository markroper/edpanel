package com.scholarscore.api.persistence.goalCalculators;

import com.scholarscore.api.persistence.StudentAssignmentPersistence;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.goal.CalculatableAssignment;

/**
 * Calculator class for calculating the value of an assignment goal.
 * Returns the awarded points of the assignment that the goal references.
 * Created by cwallace on 9/21/2015.
 */
public class AssignmentGoalCalc implements GoalCalc<CalculatableAssignment> {


    private StudentAssignmentPersistence studentAssignmentPersistence;

    public void setStudentAssignmentPersistence(StudentAssignmentPersistence studentAssignmentPersistence) {
        this.studentAssignmentPersistence = studentAssignmentPersistence;
    }

    public Double calculateGoal(CalculatableAssignment goal) {
        StudentAssignment goalAssignment = studentAssignmentPersistence.select(goal.getStudent().getId(), goal.getParentId());
        Double awardedPoints = goalAssignment.getAwardedPoints();
                if (null != awardedPoints) {
                    return awardedPoints.doubleValue();
                } else {
                    //This is an indication that points have not been assigned for a goal
                    return -1d;
                }
    }
}
