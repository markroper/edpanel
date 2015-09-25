package com.scholarscore.api.persistence.goalCalculators;

import com.scholarscore.models.Goal;

/**
 * Created by cwallace on 9/25/2015.
 */
public interface GoalCalc<T extends Goal> {

    public Float calculateGoal(T goal);
}
