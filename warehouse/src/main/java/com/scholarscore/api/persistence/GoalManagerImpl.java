package com.scholarscore.api.persistence;

import com.scholarscore.api.persistence.mysql.GoalPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.Goal;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

/**
 * Created by cwallace on 9/17/2015.
 */
public class GoalManagerImpl implements GoalManager {

    @Autowired
    private PersistenceManager pm;

    @Autowired
    private GoalPersistence goalPersistence;

    private static final String GOAL = "goal";

    public void setPm(PersistenceManager pm) {
        this.pm = pm;
    }

    public void setGoalPersistence(GoalPersistence goalPersistence) {
        this.goalPersistence = goalPersistence;
    }

    @Override
    public ServiceResponse<Long> createGoal(long studentId, Goal goal) {
        StatusCode code = pm.getStudentManager().studentExists(studentId);
        if (!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        Long goalId = goalPersistence.createGoal(studentId, goal);
        return new ServiceResponse<Long>(goalId);
    }

    @Override
    public ServiceResponse<Goal> getGoal(long studentId, long goalId) {
        StatusCode code = goalExists(studentId, goalId);
        if (!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        Goal goal = goalPersistence.select(studentId, goalId);
        return new ServiceResponse<>(goal);

    }

    @Override
    public StatusCode goalExists(long studentId, long goalId) {
        StatusCode code = pm.getStudentManager().studentExists(studentId);
        if (!code.isOK()) {
            return code;
        }
        Goal goal = goalPersistence.select(studentId, goalId);
        if (null == goal) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{GOAL, goalId});
        }
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    @Override
    public ServiceResponse<Collection<Goal>> getAllGoals(long studentId) {
        return new ServiceResponse<Collection<Goal>>
                (goalPersistence.selectAll(studentId));
    }

    @Override
    public ServiceResponse<Long> replaceGoal(long studentId, long goalId, Goal goal) {
        StatusCode code = goalExists(studentId, goalId);
        if (!code.isOK()) {
            return new ServiceResponse<>(code);
        }

        goalPersistence.replaceGoal(studentId, goalId, goal);
        return new ServiceResponse<>(goalId);
    }

    @Override
    public ServiceResponse<Long> updateGoal(long studentId, long goalId, Goal goal) {
        StatusCode code = goalExists(studentId, goalId);
        if (!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        goal.setId(goalId);
        Goal originalGoal =
                goalPersistence.select(studentId, goalId);
        goal.mergePropertiesIfNull(originalGoal);
        return replaceGoal(studentId, goalId, goal);
    }

    @Override
    public ServiceResponse<Long> deleteGoal(long studentId, long goalId) {
        StatusCode code = goalExists(studentId, goalId);
        if (!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        goalPersistence.delete(studentId, goalId);
        return new ServiceResponse<Long>((Long) null);
    }
}
