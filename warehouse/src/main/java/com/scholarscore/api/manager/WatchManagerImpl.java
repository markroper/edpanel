package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.WatchPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.StudentWatch;

import java.util.List;

/**
 * Created by cwallace on 4/15/16.
 */
public class WatchManagerImpl implements WatchManager {

    private OrchestrationManager pm;

    private WatchPersistence watchPersistence;
    private static final String WATCH = "watch";


    public WatchPersistence getWatchPersistence() {
        return watchPersistence;
    }

    public void setWatchPersistence(WatchPersistence watchPersistence) {
        this.watchPersistence = watchPersistence;
    }

    public OrchestrationManager getPm() {
        return pm;
    }

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }

    public ServiceResponse<Long> createWatch(StudentWatch watch) {

        if (watch.getStudent() != null && watch.getStaff() != null) {
            StatusCode studentCode = pm.getStudentManager().studentExists(watch.getStudent().getId());
            StatusCode teacherCode = pm.getTeacherManager().teacherExists(watch.getStaff().getId());
            if(!studentCode.isOK() ) {
                return new ServiceResponse<Long>(studentCode);
            }
            else if (!teacherCode.isOK()) {
                return new ServiceResponse<Long>(teacherCode);
            }
        }


        return new ServiceResponse<Long>(watchPersistence.createWatch(watch));
    }
    public ServiceResponse<List<StudentWatch>> getAllForStaff(long staffId) {
        return new ServiceResponse<List<StudentWatch>>(watchPersistence.getAllForStaff(staffId));

    }

    public ServiceResponse<Long> deleteWatch(long watchId) {
        Long watch = watchPersistence.deleteWatch(watchId);
        if (null == watch) {
            return new ServiceResponse<Long>(
                    StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{WATCH, watchId}));
        } else {
            return new ServiceResponse<Long>(watch);
        }

    }

    public ServiceResponse<StudentWatch> getWatch(long watchId) {
        return new ServiceResponse<StudentWatch>(watchPersistence.getWatch(watchId));
    }

}