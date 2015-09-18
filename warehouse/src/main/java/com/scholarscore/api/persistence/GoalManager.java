package com.scholarscore.api.persistence;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.Goal;

/**
 * Created by cwallace on 9/17/2015.
 */
public interface GoalManager {

    public ServiceResponse<Long> createGoal(Long studentId, Goal goal);
}
