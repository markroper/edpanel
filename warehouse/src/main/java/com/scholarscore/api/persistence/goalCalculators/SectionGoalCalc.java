package com.scholarscore.api.persistence.goalCalculators;

import com.scholarscore.api.persistence.StudentSectionGradePersistence;
import com.scholarscore.models.grade.StudentSectionGrade;
import com.scholarscore.models.goal.CalculatableSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cwallace on 9/25/2015.
 */
public class SectionGoalCalc implements GoalCalc<CalculatableSection> {
    private final static Logger LOGGER = LoggerFactory.getLogger(SectionGoalCalc.class);

    private StudentSectionGradePersistence studentSectionGradePersistence;

    public void setStudentSectionGradePersistence(StudentSectionGradePersistence studentSectionGradePersistence) {
        this.studentSectionGradePersistence = studentSectionGradePersistence;
    }

    public Double calculateGoal(CalculatableSection goal) {
        if(null == goal.getSection()) {
            LOGGER.warn("Student section goal missing section for student: " + goal.getStudent().getId());
            return -1D;
        }
        StudentSectionGrade studentSectionGrade = studentSectionGradePersistence.select(goal.getSection().getId(), goal.getStudent().getId());
        Double awardedPoints = studentSectionGrade.getAwardedPoints();
        if (null != awardedPoints) {
            return awardedPoints;
        } else {
            //This is an indication that points have not been assigned for a goal
            return -1D;
        }
    }
}
