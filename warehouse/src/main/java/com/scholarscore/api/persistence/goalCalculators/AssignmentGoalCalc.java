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
public class AssignmentGoalCalc {

    @Autowired
    private StudentAssignmentPersistence studentAssignmentPersistence;


    public Float calculateAssignmentGoal(AssignmentGoal goal) {
        StudentAssignment goalAssignment = studentAssignmentPersistence.select(goal.getParentId(),goal.getStudent().getId());
        Long awardedPoints = goalAssignment.getAwardedPoints();
                if (null != awardedPoints) {
                    return awardedPoints.floatValue();
                } else {
                    //This is an indication that points have not been assigned for a goal
                    return -1f;
                }
    }
}
