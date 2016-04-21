package com.scholarscore.etl.schoolbrains.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.SyncResult;
import com.scholarscore.etl.schoolbrains.client.ISchoolBrainsClient;
import com.scholarscore.models.ISourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 4/15/16.
 * 
 * Base class for all SchoolBrains Sync Objects
 */
public abstract class SchoolBrainsBaseSync<T extends ISourceModel<String>> {
    protected final static Logger LOGGER = LoggerFactory.getLogger(SchoolBrainsBaseSync.class);
    protected ISchoolBrainsClient schoolBrains;
    protected IAPIClient edPanel;

    public SchoolBrainsBaseSync(ISchoolBrainsClient schoolBrains, IAPIClient edPanel) {
        this.schoolBrains = schoolBrains;
        this.edPanel = edPanel;
    }

    public ConcurrentHashMap<String, T> syncCreateUpdateDelete(SyncResult results) {
        ConcurrentHashMap<String, T> source;
        try {
            source = this.resolveSourceSystem();
        } catch (HttpClientException e) {
            LOGGER.error("Unable to resolve from schoolbrains: " + e.getMessage());
            return new ConcurrentHashMap<>();
        }
        ConcurrentHashMap<String, T> ed;
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

    private ConcurrentHashMap<String, T> resolveFromEdPanel() throws HttpClientException {
        ConcurrentHashMap<String, T> ed = new ConcurrentHashMap<>();
        Collection<T> edPanelResults = fetchEdPanelRecords();
        if(null != edPanelResults) {
            for(T result: edPanelResults) {
                ed.put(result.getSourceSystemId(), result);
            }
        }
        return ed;
    }

    private ConcurrentHashMap<String, T> resolveSourceSystem() throws HttpClientException {
        ConcurrentHashMap<String, T> source = new ConcurrentHashMap<>();
        Collection<T> results = fetchSourceRecords();
        if(null != results) {
            for(T entity: results) {
                source.put(entity.getSourceSystemId(), entity);
            }
        }
        return source;
    }

    protected abstract T create(T input) throws HttpClientException;

    protected abstract void updateIfNeeded(T oldVal, T newVal);

    protected abstract void delete(T oldVal);

    protected abstract Collection<T> fetchSourceRecords() throws HttpClientException;

    protected abstract Collection<T> fetchEdPanelRecords() throws HttpClientException;
    
}
