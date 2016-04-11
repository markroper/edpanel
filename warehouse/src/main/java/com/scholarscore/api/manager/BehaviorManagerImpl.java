package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.BehaviorPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.behavior.BehaviorScore;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * Created by cwallace on 9/16/2015.
 */
public class BehaviorManagerImpl implements BehaviorManager {

    @Autowired
    private BehaviorPersistence behaviorPersistence;

    @Autowired
    private OrchestrationManager pm;

    private static final String BEHAVIOR = "behavior";
    private static final String BEHAVIOR_SCORE = "behavior_score";

    public void setBehaviorPersistence(BehaviorPersistence behaviorPersistence) {
        this.behaviorPersistence = behaviorPersistence;
    }


    public void setPm(OrchestrationManager pm) {
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
    public StatusCode behaviorScoreExists(long studentId, LocalDate date) {
        StatusCode code = pm.getStudentManager().studentExists(studentId);
        if (!code.isOK()) {
            return code;
        }
        BehaviorScore behaviorScore = behaviorPersistence.selectScore(studentId, date);
        if (null == behaviorScore) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{BEHAVIOR_SCORE, "(s:" + studentId + "/d:" + date + ")"});
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
            return new ServiceResponse<>(code);
        }
        Long behaviorId = behaviorPersistence.createBehavior(studentId, behavior);
        return new ServiceResponse<>(behaviorId);
    }

    @Override
    public ServiceResponse<List<Long>> createBehaviors(List<Behavior> behaviors) {
        return new ServiceResponse<>(behaviorPersistence.createBehaviors(behaviors));
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
            return new ServiceResponse<>(code);
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
            return new ServiceResponse<>(code);
        }
        behaviorPersistence.delete(studentId, behaviorId);
        return new ServiceResponse<>((Long) null);
    }

    @Override
    public ServiceResponse<Long> deleteBehaviorBySsid(long studentId, long ssid) {
        behaviorPersistence.deleteBySsid(studentId, ssid);
        return new ServiceResponse<>((Long) null);
    }

    @Override
    public ServiceResponse<BehaviorScore> getBehaviorScore(long studentId, LocalDate date) {
        return new ServiceResponse<>(behaviorPersistence.selectScore(studentId, date));
    }

    @Override
    public ServiceResponse<Collection<BehaviorScore>> getAllBehaviorScores(long studentId, LocalDate cutoffDate) {
        return new ServiceResponse<>(behaviorPersistence.selectScores(studentId, cutoffDate));
    }

    @Override
    public ServiceResponse<Long> createBehaviorScore(long studentId, BehaviorScore score) {
        return new ServiceResponse<>(behaviorPersistence.createScore(studentId, score));
    }

    @Override
    public ServiceResponse<List<Long>> createBehaviorScores(List<BehaviorScore> scores) {
        return new ServiceResponse<>(behaviorPersistence.createScores(scores));
    }

    @Override
    public ServiceResponse<Long> updateBehaviorScore(long studentId, LocalDate date, BehaviorScore score) {
        StatusCode code = behaviorScoreExists(studentId, date);
        if (!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        score.setDate(date);
        BehaviorScore originalBehaviorScore =
                behaviorPersistence.selectScore(studentId, date);
        score.mergePropertiesIfNull(originalBehaviorScore);
        return replaceBehaviorScore(studentId, date, score);
    }
    
    @Override
    public ServiceResponse<Long> replaceBehaviorScore(long studentId, LocalDate date, BehaviorScore score) {
        return new ServiceResponse<>(behaviorPersistence.replaceScore(studentId, date, score));
    }

    @Override
    public ServiceResponse<Void> deleteBehaviorScore(long studentId, LocalDate date) {
        behaviorPersistence.deleteScore(studentId, date);
        return new ServiceResponse<>((Void) null);
    }

    @Override
    public ServiceResponse<Collection<Behavior>> getAllBehaviors(long studentId, LocalDate cutoffDate) {
        return new ServiceResponse<>
                (behaviorPersistence.selectAll(studentId, cutoffDate));
    }

}
