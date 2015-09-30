package com.scholarscore.api.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scholarscore.models.Behavior;

import java.util.Collection;

/**
 * User: jordan
 * Date: 8/8/15
 * Time: 6:17 PM
 */
public interface BehaviorPersistence {

    public Collection<Behavior> selectAll(long studentId);
    
    public Behavior select(long studentId, long behaviorId);
    
    public Long createBehavior(long studentId, Behavior behavior) /*throws JsonProcessingException*/;
    
    public Long replaceBehavior(long studentId, long behaviorId, Behavior behavior);
    
    public Long delete(long studentId, long behaviorId);

}
