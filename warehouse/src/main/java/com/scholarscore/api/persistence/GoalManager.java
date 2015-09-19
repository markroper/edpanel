package com.scholarscore.api.persistence;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.Goal;

import java.util.Collection;

/**
 * Created by cwallace on 9/17/2015.
 */
public interface GoalManager {

    public ServiceResponse<Long> createGoal(long studentId, Goal goal);

    public ServiceResponse<Goal> getGoal(long studentId, long goalId);

    public StatusCode goalExists(long studentId, long goalId);

    public ServiceResponse<Collection<Goal>> getAllGoals(long studentId);

    public ServiceResponse<Long> replaceGoal(long studentId, long goalId, Goal goal);

    public ServiceResponse<Long> updateGoal(long studentId, long goalId, Goal goal);

    public ServiceResponse<Long> deleteGoal(long studentId, long goalId);
}
