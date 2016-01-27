package com.scholarscore.api.manager;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.models.user.Staff;

import java.util.Collection;

/**
 * Manages the persistence (CRUD) on administrator entities
 *
 * Created by mattg on 7/20/15.
 */
public interface AdminManager {
    ServiceResponse<Collection<Staff>> getAllAdministrators();
    StatusCode administratorExists(long administratorId);
    ServiceResponse<Staff> getAdministrator(long administratorId);
    ServiceResponse<Long> createAdministrator(Staff admin);
    ServiceResponse<Long> replaceAdministrator(long administratorId, Staff administrator);
    ServiceResponse<Long> updateAdministrator(long administratorId, Staff administrator);
    ServiceResponse<Long> deleteAdministrator(long administratorId);
}
