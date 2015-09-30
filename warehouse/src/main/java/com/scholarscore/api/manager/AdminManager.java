package com.scholarscore.api.manager;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.models.Administrator;
import com.scholarscore.models.Teacher;

import java.util.Collection;

/**
 * Manages the persistence (CRUD) on administrator entities
 *
 * Created by mattg on 7/20/15.
 */
public interface AdminManager {
    ServiceResponse<Collection<Administrator>> getAllAdministrators();
    StatusCode administratorExists(long administratorId);
    ServiceResponse<Administrator> getAdministrator(long administratorId);
    ServiceResponse<Long> createAdministrator(Administrator admin);
    ServiceResponse<Long> replaceAdministrator(long administratorId, Administrator administrator);
    ServiceResponse<Long> updateAdministrator(long administratorId, Administrator administrator);
    ServiceResponse<Long> deleteAdministrator(long administratorId);
}
