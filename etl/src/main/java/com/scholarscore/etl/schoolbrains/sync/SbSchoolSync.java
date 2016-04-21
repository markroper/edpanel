package com.scholarscore.etl.schoolbrains.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.schoolbrains.client.ISchoolBrainsClient;
import com.scholarscore.models.School;

import java.util.Collection;
import java.util.List;

/**
 * Created by markroper on 4/15/16.
 * 
 * SchoolBrains School Sync class
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
            input.setId(created.getId());
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
    protected List<School> fetchSourceRecords() throws HttpClientException {
        return schoolBrains.getSchools();
    }

    @Override
    protected Collection<School> fetchEdPanelRecords() throws HttpClientException {
        return edPanel.getSchools();
    }
}
