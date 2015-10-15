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
        return calculateGoalAggregate(subGoals);
    }

    private Double calculateComponent(GoalComponent component) {

        switch (component.getComponentType()){
            case BEHAVIOR:
                BehaviorComponent behaviorComponent = (BehaviorComponent) component;
                return behaviorGoalCalc.calculateGoal(behaviorComponent) * component.getModifier();
            case ASSIGNMENT:
                break;
            case ATTENDANCE:
                AttendanceComponent attendanceComponent = (AttendanceComponent) component;
                return attendanceGoalCalc.calculateGoal(attendanceComponent) * component.getModifier();
            case CUMULATIVE_GRADE:
                CumulativeGradeComponent cumulativeGradeComponent = (CumulativeGradeComponent) component;
                return cumulativeGoalCalc.calculateGoal(cumulativeGradeComponent);
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
