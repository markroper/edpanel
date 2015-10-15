package com.scholarscore.api.persistence.goalCalculators;

import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.goal.*;

/**
 * Created by cwallace on 10/14/2015.
 */
public class ComplexGoalCalc implements GoalCalc<ComplexGoal> {

    private AssignmentGoalCalc assignmentGoalCalc;
    private AttendanceGoalCalc attendanceGoalCalc;
    private BehaviorGoalCalc behaviorGoalCalc;
    private CumulativeGoalCalc cumulativeGoalCalc;

    public void setAssignmentGoalCalc(AssignmentGoalCalc assignmentGoalCalc) {
        this.assignmentGoalCalc = assignmentGoalCalc;
    }

    public void setAttendanceGoalCalc(AttendanceGoalCalc attendanceGoalCalc) {
        this.attendanceGoalCalc = attendanceGoalCalc;
    }

    public void setBehaviorGoalCalc(BehaviorGoalCalc behaviorGoalCalc) {
        this.behaviorGoalCalc = behaviorGoalCalc;
    }

    public void setCumulativeGoalCalc(CumulativeGoalCalc cumulativeGoalCalc) {
        this.cumulativeGoalCalc = cumulativeGoalCalc;
    }

    @Override
    public Double calculateGoal(ComplexGoal goal) {
        GoalAggregate subGoals = goal.getGoalAggregate();
        Double total = 0D;
        for (int i = 0; i < subGoals.getGoalComponents().size(); i++) {
            total += calculateComponent(subGoals.getGoalComponents().get(i), subGoals.getModifiers().get(i));
        }
        return total;
    }

    private Double calculateComponent(GoalComponent component, Long modifier) {

       if (component instanceof BehaviorComponent) {
           BehaviorComponent behaviorComponent = (BehaviorComponent) component;
           return behaviorGoalCalc.calculateGoal(behaviorComponent) * modifier;
       }
        return -1D;
    }
}
