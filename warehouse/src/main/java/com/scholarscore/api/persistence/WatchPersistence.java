package com.scholarscore.api.persistence;

import com.scholarscore.models.StudentWatch;

import java.util.List;

/**
 * Created by cwallace on 4/16/16.
 */
public interface WatchPersistence {

    public Long createWatch(StudentWatch watch);

    public List<StudentWatch> getAllForStaff(long staffId);
}
