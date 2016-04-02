package com.scholarscore.api.persistence;

import com.scholarscore.models.Behavior;
import com.scholarscore.models.behavior.BehaviorScore;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * User: jordan
 * Date: 8/8/15
 * Time: 6:17 PM
 */
public interface BehaviorPersistence {
    Collection<BehaviorScore> selectScores(long studentId, LocalDate cutoffDate);
    BehaviorScore selectScore(long studentId, LocalDate date);
    Long createScore(long studentId, BehaviorScore score);
    List<Long> createScores(List<BehaviorScore> scores);
    Long replaceScore(long studentId, LocalDate date, BehaviorScore score);
    void deleteScore(long studentId, LocalDate score);


    Collection<Behavior> selectAll(long studentId, LocalDate cutoffDate);
    Behavior select(long studentId, long behaviorId);
    Behavior selectBySourceSystemId(long studentId, long sourceSystemId);
    Long createBehavior(long studentId, Behavior behavior);
    List<Long> createBehaviors(List<Behavior> behaviors);
    Long replaceBehavior(long studentId, long behaviorId, Behavior behavior);
    Long delete(long studentId, long behaviorId);
    Long deleteBySsid(long studentId, long ssid);
}
