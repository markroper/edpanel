package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.AdminManager;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.models.Administrator;
import org.springframework.jdbc.core.RowMapper;

import java.util.Collection;

/**
 * Created by mattg on 7/20/15.
 */
public class AdminJdbc extends EnhancedBaseJdbc<Administrator> implements AdminManager {
    @Override
    public ServiceResponse<Collection<Administrator>> getAllAdministrators() {
        return null;
    }

    @Override
    public StatusCode administratorExists(long administratorId) {
        return null;
    }

    @Override
    public ServiceResponse<Administrator> getAdministrator(long administratorId) {
        return null;
    }

    @Override
    public ServiceResponse<Long> createAdministrator(Administrator admin) {
        return null;
    }

    @Override
    public ServiceResponse<Long> replaceAdministrator(long administratorId, Administrator administrator) {
        return null;
    }

    @Override
    public ServiceResponse<Long> updateAdministrator(long administratorId, Administrator administrator) {
        return null;
    }

    @Override
    public ServiceResponse<Long> deleteAdministrator(long administratorId) {
        return null;
    }

    @Override
    public RowMapper<Administrator> getMapper() {
        return null;
    }

    @Override
    public String getTableName() {
        return null;
    }
}
