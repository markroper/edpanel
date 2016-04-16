package com.scholarscore.etl.schoolbrains.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.schoolbrains.client.ISchoolBrainsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 4/15/16.
 */
public abstract class SchoolBrainsBaseSync<T> {
    protected final static Logger LOGGER = LoggerFactory.getLogger(SchoolBrainsBaseSync.class);
    protected ISchoolBrainsClient schoolBrains;
    protected IAPIClient edPanel;

    public SchoolBrainsBaseSync(ISchoolBrainsClient schoolBrains, IAPIClient edPanel) {
        this.schoolBrains = schoolBrains;
        this.edPanel = edPanel;
    }

    public ConcurrentHashMap<String, T> syncCreateUpdateDelete(PowerSchoolSyncResult results) {
        ConcurrentHashMap<String, T> source = null;
        try {
            source = this.resolveSourceSystem();
        } catch (HttpClientException e) {
            LOGGER.error("Unable to resolve from schoolbrains: " + e.getMessage());
            return new ConcurrentHashMap<>();
        }
        ConcurrentHashMap<String, T> ed = null;
        try {
            ed = this.resolveFromEdPanel();
        } catch (HttpClientException e) {
            LOGGER.error("Unable to resolve from edpanel: " + e.getMessage());
            return new ConcurrentHashMap<>();
        }

        for(Map.Entry<String, T> entry : source.entrySet()) {
            if(!ed.containsKey(entry.getKey())) {
                try {
                    T created = create(entry.getValue());
                    entry.setValue(created);
                } catch (HttpClientException e) {
                    LOGGER.warn("Failed to create in EdPanel: " + e.getMessage());
                }
            } else {
                T edpanelVal = ed.get(entry.getKey());
                T sourceVal = entry.getValue();
                updateIfNeeded(edpanelVal, sourceVal);
            }
        }

        for(Map.Entry<String, T> entry : ed.entrySet()) {
            if(!source.containsKey(entry.getKey())) {
                delete(entry.getValue());
            }
        }

        return source;
    }

    protected abstract T create(T input) throws HttpClientException;

    protected abstract void updateIfNeeded(T oldVal, T newVal);

    protected abstract void delete(T oldVal);

    protected abstract ConcurrentHashMap<String, T> resolveFromEdPanel() throws HttpClientException;

    protected abstract ConcurrentHashMap<String, T> resolveSourceSystem() throws HttpClientException;
}
