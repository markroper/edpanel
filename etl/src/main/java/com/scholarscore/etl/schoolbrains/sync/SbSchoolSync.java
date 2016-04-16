package com.scholarscore.etl.schoolbrains.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.schoolbrains.client.ISchoolBrainsClient;
import com.scholarscore.models.School;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 4/15/16.
 */
public class SbSchoolSync extends SchoolBrainsBaseSync<School> {

    public SbSchoolSync(ISchoolBrainsClient schoolBrains, IAPIClient edPanel) {
        super(schoolBrains, edPanel);
    }

    @Override
    protected School create(School input) throws HttpClientException {
        School created = null;
        try {
            created = edPanel.createSchool(input);
        } catch (HttpClientException e) {
            LOGGER.warn("Failed to create school: " + e.getMessage());
        }
        return created;
    }

    @Override
    protected void updateIfNeeded(School oldVal, School newVal) {
        newVal.setId(oldVal.getId());
        if(!oldVal.equals(newVal)) {
            try {
                edPanel.updateSchool(newVal);
            } catch (HttpClientException e) {
                LOGGER.warn("Failed to update school: " + e.getMessage());
            }
        }
    }

    @Override
    protected void delete(School oldVal) {
        try {
            edPanel.deleteSchool(oldVal);
        } catch (HttpClientException e) {
            LOGGER.warn("Failed to delete school from EdPanel: " + e.getMessage());
        }
    }

    @Override
    protected ConcurrentHashMap<String, School> resolveFromEdPanel() throws HttpClientException {
        ConcurrentHashMap<String, School> ed = new ConcurrentHashMap<>();
        School[] schools = edPanel.getSchools();
        if(null != schools) {
            for(School s: schools) {
                ed.put(s.getSourceSystemId(), s);
            }
        }
        return ed;
    }

    @Override
    protected ConcurrentHashMap<String, School> resolveSourceSystem() throws HttpClientException {
        ConcurrentHashMap<String, School> source = new ConcurrentHashMap<>();
        List<School> schools = schoolBrains.getSchools();
        if(null != schools) {
            for(School s: schools) {
                source.put(s.getSourceSystemId(), s);
            }
        }
        return source;
    }
}
