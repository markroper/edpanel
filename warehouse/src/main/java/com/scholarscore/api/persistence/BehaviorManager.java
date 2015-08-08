package com.scholarscore.api.persistence;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.models.Behavior;

import java.util.Collection;

/**
 * User: jordan
 * Date: 8/8/15
 * Time: 5:28 PM
 */
public interface BehaviorManager {
    public ServiceResponse<Collection<Behavior>> getAllBehaviors();
    
    public StatusCode behaviorExists(long behaviorId);
    public ServiceResponse<Behavior> getBehavior(long behaviorId);
    
    public ServiceResponse<Long> createBehavior(Behavior behavior);
    
    public ServiceResponse<Long> replaceBehavior(long behaviorId, Behavior behavior);
    
    public ServiceResponse<Long> updateBehavior(long behaviorId, Behavior behavior);
    
    public ServiceResponse<Long> deleteBehavior(long behaviorId);
}
