package com.scholarscore.api.persistence.goalCalculators;

import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.goal.CalculatableAssignment;

/**
 * Calculator class for calculating the value of an assignment goal.
 * Returns the awarded points of the assignment that the goal references.
 * Created by cwallace on 9/21/2015.
 */
public class AssignmentGoalCalc implements GoalCalc<CalculatableAssignment> {
    
    public Double calculateGoal(CalculatableAssignment goal) {
        StudentAssignment goalAssignment = goal.getStudentAssignment();
        //We may not have this because the assignment is null in which ase the teacher will ahve to approve it
        if (null != goalAssignment) {
            return  goalAssignment.getAwardedPoints();
        } else {
            return null;
        }

    }
}
