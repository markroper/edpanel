package com.scholarscore.api.manager;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.StudentWatch;

import java.util.List;

/**
 * Created by cwallace on 4/15/16.
 */
public interface WatchManager {

    public ServiceResponse<Long> createWatch(StudentWatch watch);

    public ServiceResponse<List<StudentWatch>> getAllForStaff(long staffId);

    public ServiceResponse<Long> deleteWatch(long watchId);



}
