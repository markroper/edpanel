package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.AdministratorPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.user.Administrator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

/**
 * Created by cwallace on 9/16/2015.
 */
public class AdminManagerImpl implements  AdminManager {
    private static final int CREATE_RETRY_MAX = 15;
    private static final String ADMINISTRATOR = "administrator";

    @Autowired
    private AdministratorPersistence administratorPersistence;

    @Autowired
    private OrchestrationManager pm;

    public void setAdministratorPersistence(AdministratorPersistence administratorPersistence) {
        this.administratorPersistence = administratorPersistence;
    }

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }

    @Override
    public ServiceResponse<Collection<Administrator>> getAllAdministrators() {
        return new ServiceResponse<>(
                administratorPersistence.selectAll());
    }

    @Override
    public StatusCode administratorExists(long administratorId) {
        Administrator stud = administratorPersistence.select(administratorId);
        if(null == stud) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND,
                    new Object[]{ADMINISTRATOR, administratorId});
        }
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    @Override
    public ServiceResponse<Administrator> getAdministrator(long administratorId) {
        StatusCode code = pm.getTeacherManager().teacherExists(administratorId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(administratorPersistence.select(administratorId));
    }

    @Override
    public ServiceResponse<Long> createAdministrator(Administrator admin) {
        String initUsername = admin.getUsername();
        boolean retry = true;
        int suffix = 0;
        while(retry && suffix < CREATE_RETRY_MAX) {
            retry = false;
            try {
                return new ServiceResponse<>(administratorPersistence.createAdministrator(admin));
            } catch (Throwable e) {
                suffix++;
                retry = true;
                if (null == initUsername) {
                    initUsername = admin.getUsername();
                }
                admin.setUsername(initUsername + suffix);
            }
        }
        return null;
    }

    @Override
    public ServiceResponse<Long> replaceAdministrator(long administratorId, Administrator administrator) {
        StatusCode code = pm.getTeacherManager().teacherExists(administratorId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        administratorPersistence.replaceAdministrator(administratorId, administrator);
        return new ServiceResponse<>(administratorId);
    }

    @Override
    public ServiceResponse<Long> updateAdministrator(long administratorId,
                                                     Administrator administrator) {
        StatusCode code = administratorExists(administratorId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        administrator.setId(administratorId);
        administrator.mergePropertiesIfNull(administratorPersistence.select(administratorId));
        replaceAdministrator(administratorId, administrator);
        return new ServiceResponse<>(administratorId);

    }

    @Override
    public ServiceResponse<Long> deleteAdministrator(long administratorId) {
        StatusCode code = administratorExists(administratorId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        administratorPersistence.delete(administratorId);
        return new ServiceResponse<Long>((Long) null);
    }
}
