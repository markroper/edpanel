package com.scholarscore.api.persistence.goalCalculators;

import com.scholarscore.models.goal.AssignmentComponent;
import com.scholarscore.models.goal.AttendanceComponent;
import com.scholarscore.models.goal.BehaviorComponent;
import com.scholarscore.models.goal.ComplexComponent;
import com.scholarscore.models.goal.ComplexGoal;
import com.scholarscore.models.goal.ConstantComponent;
import com.scholarscore.models.goal.CumulativeGradeComponent;
import com.scholarscore.models.goal.GoalAggregate;
import com.scholarscore.models.goal.GoalComponent;

/**
 * Created by cwallace on 10/14/2015.
 */
public class ComplexGoalCalc implements GoalCalc<ComplexGoal> {

    private AssignmentGoalCalc assignmentGoalCalc;
    private AttendanceGoalCalc attendanceGoalCalc;
    private BehaviorGoalCalc behaviorGoalCalc;
    private SectionGoalCalc sectionGoalCalc;

    public void setAssignmentGoalCalc(AssignmentGoalCalc assignmentGoalCalc) {
        this.assignmentGoalCalc = assignmentGoalCalc;
    }

    public void setAttendanceGoalCalc(AttendanceGoalCalc attendanceGoalCalc) {
        this.attendanceGoalCalc = attendanceGoalCalc;
    }

    public void setBehaviorGoalCalc(BehaviorGoalCalc behaviorGoalCalc) {
        this.behaviorGoalCalc = behaviorGoalCalc;
    }

    public void setSectionGoalCalc(SectionGoalCalc sectionGoalCalc) {
        this.sectionGoalCalc = sectionGoalCalc;
    }

    @Override
    public Double calculateGoal(ComplexGoal goal) {
        GoalAggregate subGoals = goal.getGoalAggregate();
        return calculateGoalAggregate(subGoals);
    }

    private Double calculateComponent(GoalComponent component) {

        switch (component.getComponentType()){
            case CONSTANT:
                ConstantComponent constantComponent = (ConstantComponent) component;
                return constantComponent.getInitialValue();
            case BEHAVIOR:
                BehaviorComponent behaviorComponent = (BehaviorComponent) component;
                return behaviorGoalCalc.calculateGoal(behaviorComponent) * component.getModifier();
            case ASSIGNMENT:
                AssignmentComponent assignmentComponent = (AssignmentComponent) component;
                return assignmentGoalCalc.calculateGoal(assignmentComponent) * component.getModifier();
            case ATTENDANCE:
                AttendanceComponent attendanceComponent = (AttendanceComponent) component;
                return attendanceGoalCalc.calculateGoal(attendanceComponent) * component.getModifier();
            case CUMULATIVE_GRADE:
                CumulativeGradeComponent cumulativeGradeComponent = (CumulativeGradeComponent) component;
                return sectionGoalCalc.calculateGoal(cumulativeGradeComponent);
            case COMPLEX:
                break;


        }

        return -1D;
    }

    private Double calculateGoalAggregate(GoalAggregate subGoals) {
        Double total = 0D;
        for (int i = 0; i < subGoals.getGoalComponents().size(); i++) {
            GoalComponent goalComponent = subGoals.getGoalComponents().get(i);
            if (goalComponent instanceof ComplexComponent) {
                ComplexComponent complexComponent = (ComplexComponent) goalComponent;
                total += calculateGoalAggregate(complexComponent.getGoalAggregate()) * complexComponent.getModifier();
            } else {
                total += calculateComponent(goalComponent);
            }

        }
        return total;
    }
}
