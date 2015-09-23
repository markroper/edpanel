package com.scholarscore.api.persistence.goalCalculators;

import com.scholarscore.api.persistence.mysql.BehaviorPersistence;
import com.scholarscore.api.persistence.mysql.jdbc.BehaviorJdbc;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.BehaviorGoal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Calculator for determining the present value of behavior goals.
 * Searches for behaviors matching the goal category within the goal date range that are specified by the goal.
 * Adds 1 to each goal that is returned.
 * Created by cwallace on 9/20/2015.
 */

public class BehaviorGoalCalc {

    @Autowired
    private BehaviorPersistence behaviorPersistence;



    public Float calculateBehaviorGoal(BehaviorGoal goal) {
        Collection<Behavior> studentBehaviors = behaviorPersistence.selectAll(goal.getStudent().getId());
        //Make sure behavior category matches and it is within the dates specified.
        Collection<Behavior> relevantBehaviors = studentBehaviors.stream().filter(bg -> bg.getBehaviorCategory().equals(goal.getBehaviorCategory()))
                .filter(bg -> bg.getBehaviorDate().after(goal.getStartDate()) && bg.getBehaviorDate().before(goal.getEndDate()))
                .collect(Collectors.toList());

        return new Float(relevantBehaviors.size());
    }
}
