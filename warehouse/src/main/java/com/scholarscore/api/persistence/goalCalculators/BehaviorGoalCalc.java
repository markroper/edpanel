package com.scholarscore.api.persistence.goalCalculators;

import com.scholarscore.api.persistence.BehaviorPersistence;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.BehaviorGoal;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Calculator for determining the present value of behavior goals.
 * Searches for behaviors matching the goal category within the goal date range that are specified by the goal.
 * Adds 1 to each goal that is returned.
 * Created by cwallace on 9/20/2015.
 */

public class BehaviorGoalCalc implements GoalCalc<BehaviorGoal> {


    private BehaviorPersistence behaviorPersistence;

    public void setBehaviorPersistence(BehaviorPersistence behaviorPersistence) {
        this.behaviorPersistence = behaviorPersistence;
    }

    public Double calculateGoal(BehaviorGoal goal) {
        Collection<Behavior> studentBehaviors = behaviorPersistence.selectAll(goal.getStudent().getId());
        //Make sure behavior category matches and it is within the dates specified.
        Collection<Behavior> relevantBehaviors = studentBehaviors.stream().filter(bg -> bg.getBehaviorCategory().equals(goal.getBehaviorCategory()))
                .filter(bg -> bg.getBehaviorDate().after(goal.getStartDate()) && bg.getBehaviorDate().before(goal.getEndDate()))
                .collect(Collectors.toList());

        return new Double(relevantBehaviors.size());
    }
}
