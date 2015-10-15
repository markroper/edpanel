package com.scholarscore.api.persistence.goalCalculators;

import com.scholarscore.models.goal.Goal;

/**
 * Created by cwallace on 9/25/2015.
 */
public interface GoalCalc<T> {

    public Double calculateGoal(T goal);
}
