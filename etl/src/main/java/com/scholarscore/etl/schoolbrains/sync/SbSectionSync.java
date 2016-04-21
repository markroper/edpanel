package com.scholarscore.etl.schoolbrains.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.schoolbrains.client.ISchoolBrainsClient;
import com.scholarscore.models.Section;

import java.util.Collection;
import java.util.List;

/**
 * User: jordan
 * Date: 4/15/16
 * Time: 8:26 PM
 */
public class SbSectionSync extends SchoolBrainsBaseSync<Section> {

    private Long edpanelSchoolId;
    private Long edpanelSchoolYearId;
    private Long edpanelTermId;

    public SbSectionSync(ISchoolBrainsClient schoolBrains, 
                         IAPIClient edPanel,
                         Long edpanelSchoolId,
                         Long edpanelSchoolYearId,
                         Long edpanelTermId) {
        super(schoolBrains, edPanel);
        this.edpanelSchoolId = edpanelSchoolId;
        this.edpanelSchoolYearId = edpanelSchoolYearId;
        this.edpanelTermId = edpanelTermId;
    }

    @Override
    protected Section create(Section input) throws HttpClientException {
        Section created = null;
        try {
            created = edPanel.createSection(edpanelSchoolId, edpanelSchoolYearId, edpanelTermId, input);
            input.setId(created.getId());
        } catch (HttpClientException e) {
            LOGGER.warn("Failed to create school: " + e.getMessage());
        }
        return created;
    }

    @Override
    protected void updateIfNeeded(Section oldVal, Section newVal) {
        newVal.setId(oldVal.getId());
        if(!oldVal.equals(newVal)) {
            try {
                edPanel.replaceSection(edpanelSchoolId, edpanelSchoolYearId, edpanelTermId, newVal);
            } catch (HttpClientException e) {
                LOGGER.warn("Failed to update school: " + e.getMessage());
            }
        }
    }

    @Override
    protected void delete(Section oldVal) {
        try {
            edPanel.deleteSection(edpanelSchoolId, edpanelSchoolYearId, edpanelTermId, oldVal);
        } catch (HttpClientException e) {
            LOGGER.warn("Failed to delete school from EdPanel: " + e.getMessage());
        }
    }

    @Override
    protected List<Section> fetchSourceRecords() throws HttpClientException {
        return schoolBrains.getSections();
    }

    @Override
    protected Collection<Section> fetchEdPanelRecords() throws HttpClientException {
        return edPanel.getSections(edpanelSchoolId);
    }
}
