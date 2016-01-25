package com.scholarscore.api.persistence;

import com.scholarscore.models.user.Staff;

import java.util.Collection;

/**
 * Created by mattg on 7/20/15.
 */
public interface AdministratorPersistence {
    /**
     * Select all administrators
     *
     * @return
     */
    Collection<Staff> selectAll();

    /**
     * Select a single administrator
     *
     * @param administratorId
     * @return
     */
    Staff select(long administratorId);

    /**
     * Find the user by username
     *
     * @param username
     * @return
     */
    Staff select(String username);

    /**
     * Create an administrator
     *
     * @param admin
     * @return
     */
    Long createAdministrator(Staff admin);

    /**
     * Update / replace an existing administrator - none of the attributes will remain
     *
     * @param administratorId
     * @param administrator
     */
    void replaceAdministrator(long administratorId, Staff administrator);

    /**
     * Delete an existing administrator
     *
     * @param administratorId
     */
    Long delete(long administratorId);
}
