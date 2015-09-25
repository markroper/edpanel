package com.scholarscore.api.persistence.goalCalculators;

import com.scholarscore.api.persistence.mysql.StudentAssignmentPersistence;
import com.scholarscore.models.AssignmentGoal;
import com.scholarscore.models.BehaviorGoal;
import com.scholarscore.models.StudentAssignment;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Calculator class for calculating the value of an assignment goal.
 * Returns the awarded points of the assignment that the goal references.
 * Created by cwallace on 9/21/2015.
 */
public class AssignmentGoalCalc implements GoalCalc<AssignmentGoal> {


    private StudentAssignmentPersistence studentAssignmentPersistence;

    public void setStudentAssignmentPersistence(StudentAssignmentPersistence studentAssignmentPersistence) {
        this.studentAssignmentPersistence = studentAssignmentPersistence;
    }

    public Float calculateGoal(AssignmentGoal goal) {
        StudentAssignment goalAssignment = studentAssignmentPersistence.select(goal.getStudent().getId(), goal.getParentId());
        Long awardedPoints = goalAssignment.getAwardedPoints();
                if (null != awardedPoints) {
                    return awardedPoints.floatValue();
                } else {
                    //This is an indication that points have not been assigned for a goal
                    return -1f;
                }
    }
}
