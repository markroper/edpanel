package com.scholarscore.etl.schoolbrains.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.schoolbrains.client.ISchoolBrainsClient;
import com.scholarscore.models.user.Staff;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 4/15/16.
 */
public class StaffSync extends SchoolBrainsBaseSync<Staff> {
    public StaffSync(ISchoolBrainsClient schoolBrains, IAPIClient edPanel) {
        super(schoolBrains, edPanel);
    }

    @Override
    protected Staff create(Staff input) throws HttpClientException {
        return edPanel.createTeacher(input);
    }

    @Override
    protected void updateIfNeeded(Staff oldVal, Staff newVal) {
        newVal.setId(oldVal.getId());
        if(!oldVal.equals(newVal)) {
            try {
                edPanel.updateUser(newVal);
            } catch (HttpClientException e) {
                LOGGER.warn("Unable to update teacher: " + e.getMessage());
            }
        }
    }

    @Override
    protected void delete(Staff oldVal) {
        //TODO SchoolBrains: figure out how to handle deletion of teachers... (I think this is also an issue in PS)
    }

    @Override
    protected Collection<Staff> fetchSourceRecords() throws HttpClientException {
        return schoolBrains.getStaff();
    }

    @Override
    protected Collection<Staff> fetchEdPanelRecords() throws HttpClientException {
        return edPanel.getTeachers();
    }
}
