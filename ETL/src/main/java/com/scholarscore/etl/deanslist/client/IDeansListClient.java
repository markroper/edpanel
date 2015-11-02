package com.scholarscore.etl.deanslist.client;

import com.scholarscore.etl.deanslist.api.response.BehaviorResponse;

/**
 * Created by jwinch on 7/22/15.
 */
public interface IDeansListClient {
    
    BehaviorResponse getBehaviorData();
}
