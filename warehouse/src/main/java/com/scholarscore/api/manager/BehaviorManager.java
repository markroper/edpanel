package com.scholarscore.api.manager;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.models.Behavior;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * User: jordan
 * Date: 8/8/15
 * Time: 5:28 PM
 */
public interface BehaviorManager {
    public ServiceResponse<Collection<Behavior>> getAllBehaviors(long studentId, LocalDate cuttoffDate);
    
    public StatusCode behaviorExists(long studentId, long behaviorId);

    public ServiceResponse<Behavior> getBehavior(long studentId, long behaviorId);
    
    public ServiceResponse<Long> createBehavior(long studentId, Behavior behavior);

    public ServiceResponse<List<Long>> createBehaviors(List<Behavior> behaviors);
    
    public ServiceResponse<Long> replaceBehavior(long studentId, long behaviorId, Behavior behavior);
    
    public ServiceResponse<Long> updateBehavior(long studentId, long behaviorId, Behavior behavior);
    
    public ServiceResponse<Long> deleteBehavior(long studentId, long behaviorId);

    public ServiceResponse<Long> deleteBehaviorBySsid(long studentId, long ssid);
}
