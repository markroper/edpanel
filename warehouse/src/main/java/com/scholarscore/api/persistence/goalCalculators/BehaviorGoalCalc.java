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
 * Created by cwallace on 9/20/2015.
 */

public class BehaviorGoalCalc {

    @Autowired
    private BehaviorPersistence behaviorPersistence;



    public Long calculateBehaviorGoal(BehaviorGoal goal) {
        Collection<Behavior> studentBehaviors = behaviorPersistence.selectAll(goal.getStudent().getId());
        //Collections.sort(studentBehaviors, (BehaviorGoal behavior1, BehaviorGoal behavior2) ->
        //behavior1.getBehaviorCategory(), equals(goal.getBehaviorCategory()));
        Collection<Behavior> relevantBehaviors = studentBehaviors.stream().filter(bg -> bg.getBehaviorCategory().equals(goal.getBehaviorCategory()))
                .collect(Collectors.toList());
        return new Long(relevantBehaviors.size());
    }
}
