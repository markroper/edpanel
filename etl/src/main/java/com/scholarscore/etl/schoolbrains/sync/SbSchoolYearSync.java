package com.scholarscore.etl.schoolbrains.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.schoolbrains.client.ISchoolBrainsClient;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;

import java.util.Collection;
import java.util.List;

/**
 * User: jordan
 * Date: 4/20/16
 * Time: 3:56 PM
 */
public class SbSchoolYearSync extends SchoolBrainsBaseSync<SchoolYear> {

    private School school;
    
    public SbSchoolYearSync(ISchoolBrainsClient schoolBrains, IAPIClient edPanel, School school) {
        super(schoolBrains, edPanel);
        this.school = school;
    }

    @Override
    protected SchoolYear create(SchoolYear input) throws HttpClientException {
        SchoolYear created = null;
        try {
            created = edPanel.createSchoolYear(input.getSchool().getId(), input);
            input.setId(created.getId());
        } catch (HttpClientException e) {
            LOGGER.warn("Failed to create school: " + e.getMessage());
        }
        return created;

    }

    @Override
    protected void updateIfNeeded(SchoolYear oldVal, SchoolYear newVal) {
        newVal.setId(oldVal.getId());
        if(!oldVal.equals(newVal)) {
            try {
                edPanel.updateSchoolYear(newVal.getSchool().getId(), newVal);
            } catch (HttpClientException e) {
                LOGGER.warn("Failed to update school: " + e.getMessage());
            }
        }
    }

    @Override
    protected void delete(SchoolYear oldVal) {
        try {
            edPanel.deleteSchoolYear(oldVal.getSchool().getId(), oldVal);
        } catch (HttpClientException e) {
            LOGGER.warn("Failed to delete school from EdPanel: " + e.getMessage());
        }

    }

    @Override
    protected Collection<SchoolYear> fetchSourceRecords() throws HttpClientException {
        return schoolBrains.getSchoolYears();
    }

    @Override
    protected Collection<SchoolYear> fetchEdPanelRecords() throws HttpClientException {
        return edPanel.getSchoolYears(school.getId());
    }
}
