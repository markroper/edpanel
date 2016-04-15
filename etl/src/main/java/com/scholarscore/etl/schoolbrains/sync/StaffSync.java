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
        //TODO: figure out how to handle deletion of teachers...
    }

    @Override
    protected ConcurrentHashMap<String, Staff> resolveFromEdPanel() throws HttpClientException {
        Collection<Staff> staff = edPanel.getTeachers();
        ConcurrentHashMap<String, Staff> edpanel = new ConcurrentHashMap<>();
        for(Staff s: staff) {
            edpanel.put(s.getSourceSystemId(), s);
        }
        return edpanel;
    }

    @Override
    protected ConcurrentHashMap<String, Staff> resolveSourceSystem() throws HttpClientException {
        List<Staff> source = schoolBrains.getStaff();
        ConcurrentHashMap<String, Staff> sourceMap = new ConcurrentHashMap<>();
        for(Staff s : source) {
            sourceMap.put(s.getSourceSystemId(), s);
        }
        return sourceMap;
    }
}
