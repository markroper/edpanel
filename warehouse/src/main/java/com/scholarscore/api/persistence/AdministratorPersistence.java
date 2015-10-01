package com.scholarscore.api.persistence;

import com.scholarscore.api.util.StatusCode;
import com.scholarscore.models.user.Administrator;

import java.util.Collection;
import java.util.List;

/**
 * Created by mattg on 7/20/15.
 */
public interface AdministratorPersistence {
    /**
     * Select all administrators
     *
     * @return
     */
    Collection<Administrator> selectAll();

    /**
     * Select a single administrator
     *
     * @param administratorId
     * @return
     */
    Administrator select(long administratorId);

    /**
     * Find the user by username
     *
     * @param username
     * @return
     */
    Administrator select(String username);

    /**
     * Create an administrator
     *
     * @param admin
     * @return
     */
    Long createAdministrator(Administrator admin);

    /**
     * Update / replace an existing administrator - none of the attributes will remain
     *
     * @param administratorId
     * @param administrator
     */
    void replaceAdministrator(long administratorId, Administrator administrator);

    /**
     * Delete an existing administrator
     *
     * @param administratorId
     */
    Long delete(long administratorId);
}
