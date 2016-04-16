package com.scholarscore.etl.schoolbrains.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.schoolbrains.client.ISchoolBrainsClient;
import com.scholarscore.models.user.Staff;

import java.util.concurrent.ConcurrentHashMap;

/**
 * User: jordan
 * Date: 4/15/16
 * Time: 8:35 PM
 */
public class SbStaffSync extends SchoolBrainsBaseSync<Staff> {
    
    public SbStaffSync(ISchoolBrainsClient schoolBrains, IAPIClient edPanel) {
        super(schoolBrains, edPanel);
    }

    @Override
    protected Staff create(Staff input) throws HttpClientException {
        return null;
    }

    @Override
    protected void updateIfNeeded(Staff oldVal, Staff newVal) {

    }

    @Override
    protected void delete(Staff oldVal) {

    }

    @Override
    protected ConcurrentHashMap<String, Staff> resolveFromEdPanel() throws HttpClientException {
        return null;
    }

    @Override
    protected ConcurrentHashMap<String, Staff> resolveSourceSystem() throws HttpClientException {
        return null;
    }
}
