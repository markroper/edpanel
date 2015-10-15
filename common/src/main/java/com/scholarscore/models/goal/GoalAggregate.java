package com.scholarscore.models.goal;

import com.scholarscore.models.ApiModel;
import com.scholarscore.models.IApiModel;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Created by cwallace on 10/14/2015.
 */
public class GoalAggregate extends ApiModel implements Serializable, IApiModel<GoalAggregate> {

    private List<GoalComponent> goalComponents;

    public GoalAggregate() {

    }

    @Override
    public void mergePropertiesIfNull(GoalAggregate mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if (null == mergeFrom || !(mergeFrom instanceof GoalAggregate)) {
            return;
        }
        GoalAggregate goalAggregate = (GoalAggregate) mergeFrom;
        if (null == this.goalComponents) {
            this.goalComponents = goalAggregate.goalComponents;
        }
    }


    public List<GoalComponent> getGoalComponents() {
        return goalComponents;
    }

    public void setGoalComponents(List<GoalComponent> goalComponents) {
        this.goalComponents = goalComponents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GoalAggregate that = (GoalAggregate) o;
        return Objects.equals(goalComponents, that.goalComponents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), goalComponents);
    }

    @Override
    public String toString() {
        return
                "GOAL_AGGREGATE " + "\n"
                        + "Id  : " + getId() + "\n"
                        + "Name: " + getName() + "\n"
                        + "GoalComponents: " + getGoalComponents();
    }
}
