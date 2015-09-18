package com.scholarscore.api.persistence;

import com.scholarscore.api.persistence.mysql.BehaviorPersistence;
import com.scholarscore.api.persistence.mysql.GoalPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.models.Goal;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by cwallace on 9/17/2015.
 */
public class GoalManagerImpl implements GoalManager {

    @Autowired
    private PersistenceManager pm;

    @Autowired
    private GoalPersistence goalPersistence;

    public void setPm(PersistenceManager pm) {
        this.pm = pm;
    }

    public void setGoalPersistence(GoalPersistence goalPersistence) {
        this.goalPersistence = goalPersistence;
    }

    @Override
    public ServiceResponse<Long> createGoal(Long studentId, Goal goal) {
        StatusCode code = pm.getStudentManager().studentExists(studentId);
        if (!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        Long goalId = goalPersistence.createGoal(studentId, goal);
        return new ServiceResponse<Long>(goalId);
    }
}
