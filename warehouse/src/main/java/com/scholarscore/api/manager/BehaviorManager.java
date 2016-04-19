package com.scholarscore.api.manager;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.behavior.BehaviorScore;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * User: jordan
 * Date: 8/8/15
 * Time: 5:28 PM
 */
public interface BehaviorManager {
    ServiceResponse<Collection<Behavior>> getAllBehaviors(long studentId, LocalDate cutoffDate);
    
    StatusCode behaviorExists(long studentId, long behaviorId);

    ServiceResponse<Behavior> getBehavior(long studentId, long behaviorId);
    
    ServiceResponse<Long> createBehavior(long studentId, Behavior behavior);

    ServiceResponse<List<Long>> createBehaviors(List<Behavior> behaviors);
    
    ServiceResponse<Long> replaceBehavior(long studentId, long behaviorId, Behavior behavior);
    
    ServiceResponse<Long> updateBehavior(long studentId, long behaviorId, Behavior behavior);
    
    ServiceResponse<Long> deleteBehavior(long studentId, long behaviorId);

    ServiceResponse<Long> deleteBehaviorBySsid(long studentId, long ssid);

    // behavior scores
    StatusCode behaviorScoreExists(long studentId, LocalDate date);

    ServiceResponse<BehaviorScore> getBehaviorScore(long studentId, LocalDate date);

    ServiceResponse<Collection<BehaviorScore>> getAllBehaviorScores(long studentId, LocalDate cuttoffDate);

    ServiceResponse<Long> createBehaviorScore(long studentId, BehaviorScore score);

    ServiceResponse<List<Long>> createBehaviorScores(List<BehaviorScore> scores);

    ServiceResponse<Long> updateBehaviorScore(long studentId, LocalDate date, BehaviorScore score);

    ServiceResponse<Long> replaceBehaviorScore(long studentId, LocalDate date, BehaviorScore score);

    ServiceResponse<Void> deleteBehaviorScore(long studentId, LocalDate date);
}
