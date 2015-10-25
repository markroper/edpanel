package com.scholarscore.api.persistence.goalCalculators;

/**
 * Created by cwallace on 9/25/2015.
 */
public interface GoalCalc<T> {

    public Double calculateGoal(T goal);
}
