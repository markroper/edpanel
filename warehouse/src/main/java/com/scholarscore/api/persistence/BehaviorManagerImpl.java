package com.scholarscore.api.persistence;

import com.scholarscore.api.persistence.mysql.BehaviorPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.Behavior;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

/**
 * Created by cwallace on 9/16/2015.
 */
public class BehaviorManagerImpl implements BehaviorManager {

    @Autowired
    private BehaviorPersistence behaviorPersistence;

    @Autowired
    private PersistenceManager pm;

    private static final String BEHAVIOR = "behavior";


    public void setBehaviorPersistence(BehaviorPersistence behaviorPersistence) {
        this.behaviorPersistence = behaviorPersistence;
    }


    public void setPm(PersistenceManager pm) {
        this.pm = pm;
    }

    @Override
    public StatusCode behaviorExists(long studentId, long behaviorId) {
        StatusCode code = pm.getStudentManager().studentExists(studentId);
        if (!code.isOK()) {
            return code;
        }
        Behavior behavior = behaviorPersistence.select(studentId, behaviorId);
        if (null == behavior) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{BEHAVIOR, behaviorId});
        }
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    @Override
    public ServiceResponse<Behavior> getBehavior(long studentId, long behaviorId) {
        StatusCode code = behaviorExists(studentId, behaviorId);
        if (!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        Behavior behavior = behaviorPersistence.select(studentId, behaviorId);
        return new ServiceResponse<>(behavior);
    }

    @Override
    public ServiceResponse<Long> createBehavior(long studentId, Behavior behavior) {
        StatusCode code = pm.getStudentManager().studentExists(studentId);
        if (!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        Long behaviorId = behaviorPersistence.createBehavior(studentId, behavior);
        return new ServiceResponse<Long>(behaviorId);
    }

    @Override
    public ServiceResponse<Long> replaceBehavior(long studentId, long behaviorId, Behavior behavior) {
        StatusCode code = behaviorExists(studentId, behaviorId);
        if (!code.isOK()) {
            return new ServiceResponse<>(code);
        }

        behaviorPersistence.replaceBehavior(studentId, behaviorId, behavior);
        return new ServiceResponse<>(behaviorId);
    }

    @Override
    public ServiceResponse<Long> updateBehavior(long studentId, long behaviorId, Behavior behavior) {
        StatusCode code = behaviorExists(studentId, behaviorId);
        if (!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        behavior.setId(behaviorId);
        Behavior originalBehavior =
                behaviorPersistence.select(studentId, behaviorId);
        behavior.mergePropertiesIfNull(originalBehavior);
        return replaceBehavior(studentId, behaviorId, behavior);
    }

    @Override
    public ServiceResponse<Long> deleteBehavior(long studentId, long behaviorId) {
        StatusCode code = behaviorExists(studentId, behaviorId);
        if (!code.isOK()) {
            return new ServiceResponse<Long>(code);
        }
        behaviorPersistence.delete(studentId, behaviorId);
        return new ServiceResponse<Long>((Long) null);
    }

    @Override
    public ServiceResponse<Collection<Behavior>> getAllBehaviors(long studentId) {
        return new ServiceResponse<Collection<Behavior>>
                (behaviorPersistence.selectAll(studentId));
    }

}
