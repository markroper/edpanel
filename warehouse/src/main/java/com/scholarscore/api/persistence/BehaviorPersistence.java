package com.scholarscore.api.persistence;

import com.scholarscore.models.Behavior;

import java.time.LocalDate;
import java.util.Collection;

/**
 * User: jordan
 * Date: 8/8/15
 * Time: 6:17 PM
 */
public interface BehaviorPersistence {

    public Collection<Behavior> selectAll(long studentId, LocalDate cutoffDate);
    
    public Behavior select(long studentId, long behaviorId);

    public Behavior selectBySourceSystemId(long studentId, long sourceSystemId);
    
    public Long createBehavior(long studentId, Behavior behavior) /*throws JsonProcessingException*/;
    
    public Long replaceBehavior(long studentId, long behaviorId, Behavior behavior);
    
    public Long delete(long studentId, long behaviorId);

    public Long deleteBySsid(long studentId, long ssid);

}
