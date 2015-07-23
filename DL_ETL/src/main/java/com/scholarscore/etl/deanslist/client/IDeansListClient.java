package com.scholarscore.etl.deanslist.client;

import com.scholarscore.etl.deanslist.api.response.BehaviorResponse;
import com.scholarscore.etl.deanslist.api.response.StudentResponse;

/**
 * Created by jwinch on 7/22/15.
 */
public interface IDeansListClient {
    
    StudentResponse getStudents();
    
    BehaviorResponse getBehaviorData();
}
