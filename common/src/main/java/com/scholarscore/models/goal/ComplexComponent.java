package com.scholarscore.models.goal;

import java.util.Objects;

/**
 * Created by cwallace on 10/15/2015.
 */
public class ComplexComponent extends  GoalComponent {

    private GoalAggregate goalAggregate;

    public ComplexComponent() {
        setComponentType(GoalType.COMPLEX);
    }

    public GoalAggregate getGoalAggregate() {
        return goalAggregate;
    }

    public void setGoalAggregate(GoalAggregate goalAggregate) {
        this.goalAggregate = goalAggregate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ComplexComponent that = (ComplexComponent) o;
        return Objects.equals(goalAggregate, that.goalAggregate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), goalAggregate);
    }

    @Override
    public String toString() {
        return
                "GOAL " + "\n"
                        + "ComponentType:" + getComponentType() + "\n"
                        + "Student: " + getStudent() + "\n"
                        + "Modifier: " + getModifier() + "\n"
                        + "GoalAggregate: " + getGoalAggregate() ;
    }
}
