package com.scholarscore.api.persistence.goalCalculators;

import com.scholarscore.api.persistence.StudentSectionGradePersistence;
import com.scholarscore.models.*;

/**
 * Created by cwallace on 9/25/2015.
 */
public class CumulativeGoalCalc implements GoalCalc<CumulativeGradeGoal> {

    private StudentSectionGradePersistence studentSectionGradePersistence;

    public void setStudentSectionGradePersistence(StudentSectionGradePersistence studentSectionGradePersistence) {
        this.studentSectionGradePersistence = studentSectionGradePersistence;
    }

    public Double calculateGoal(CumulativeGradeGoal goal) {
        StudentSectionGrade studentSectionGrade = studentSectionGradePersistence.select(goal.getParentId(),goal.getStudent().getId());
        Double awardedPoints = studentSectionGrade.getAwardedPoints();
        if (null != awardedPoints) {
            return awardedPoints;
        } else {
            //This is an indication that points have not been assigned for a goal
            return -1D;
        }
    }
}
