package com.scholarscore.etl.schoolbrains.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.schoolbrains.client.ISchoolBrainsClient;
import com.scholarscore.models.Section;

import java.util.concurrent.ConcurrentHashMap;

/**
 * User: jordan
 * Date: 4/15/16
 * Time: 8:26 PM
 */
public class SbSectionSync extends SchoolBrainsBaseSync<Section> {


    public SbSectionSync(ISchoolBrainsClient schoolBrains, IAPIClient edPanel) {
        super(schoolBrains, edPanel);
    }

    @Override
    protected Section create(Section input) throws HttpClientException {
        return null;
    }

    @Override
    protected void updateIfNeeded(Section oldVal, Section newVal) {

    }

    @Override
    protected void delete(Section oldVal) {

    }

    @Override
    protected ConcurrentHashMap<String, Section> resolveFromEdPanel() throws HttpClientException {
        return null;
    }

    @Override
    protected ConcurrentHashMap<String, Section> resolveSourceSystem() throws HttpClientException {
        return null;
    }
}
