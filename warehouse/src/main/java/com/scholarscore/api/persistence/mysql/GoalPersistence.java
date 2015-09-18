package com.scholarscore.api.persistence.mysql;

import com.scholarscore.models.Goal;

/**
 * Created by cwallace on 9/17/2015.
 */
public interface GoalPersistence {

    public Long createGoal(Long studentId, Goal goal);
}
