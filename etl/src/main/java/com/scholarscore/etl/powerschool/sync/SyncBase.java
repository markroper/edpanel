package com.scholarscore.etl.powerschool.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.PowerSchoolSyncResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: jordan
 * Date: 4/5/16
 * Time: 8:12 PM
 */
public abstract class SyncBase<T> implements ISync<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(CourseSync.class);
    
    @Override
    public ConcurrentHashMap<Long, T> syncCreateUpdateDelete(PowerSchoolSyncResult results) {
        ConcurrentHashMap<Long, T> sourceRecords = null;
        try {
            sourceRecords = resolveAllFromSourceSystem();
        } catch (HttpClientException e) {
            try {
                sourceRecords = resolveAllFromSourceSystem();
            } catch (HttpClientException ex) {
                handleSourceGetFailure(results);
                return new ConcurrentHashMap<>();
            }
        }

        ConcurrentHashMap<Long, T> edpanelRecords = null;
        try {
            edpanelRecords = resolveFromEdPanel();
        } catch (HttpClientException e) {
            try {
                edpanelRecords = resolveFromEdPanel();
            } catch (HttpClientException ex) {
                handleEdPanelGetFailure(results);
                return new ConcurrentHashMap<>();
            }
        }

        Iterator<Map.Entry<Long, T>> sourceIterator = sourceRecords.entrySet().iterator();
        while(sourceIterator.hasNext()) {
            Map.Entry<Long, T> entry = sourceIterator.next();
            T sourceRecord = entry.getValue();
            T edPanelRecord = edpanelRecords.get(entry.getKey());
            if (null == edPanelRecord) {    // doesn't exist, must create
                createEdPanelRecord(sourceRecord, results);
            } else {                        // already exists, must update
                updateEdPanelRecord(sourceRecord, edPanelRecord, results);
            }
        }

        //Delete anything IN EdPanel that is NOT in source system
        Iterator<Map.Entry<Long, T>> edpanelIterator = edpanelRecords.entrySet().iterator();
        while(edpanelIterator.hasNext()) {
            Map.Entry<Long, T> entry = edpanelIterator.next();
            if(!sourceRecords.containsKey(entry.getKey())) {
                deleteEdPanelRecord(entry.getValue(), results);
            }
        }
        return sourceRecords;
    }

    protected abstract ConcurrentHashMap<Long, T> resolveAllFromSourceSystem() throws HttpClientException;
    
    protected abstract void handleSourceGetFailure(PowerSchoolSyncResult results);

    protected abstract ConcurrentHashMap<Long, T> resolveFromEdPanel() throws HttpClientException;

    protected abstract void handleEdPanelGetFailure(PowerSchoolSyncResult results);
    
    protected abstract void createEdPanelRecord(T entityToSave, PowerSchoolSyncResult results);

    protected abstract void updateEdPanelRecord(T sourceSystemEntity, T edPanelEntity, PowerSchoolSyncResult results);
    
    protected abstract void deleteEdPanelRecord(T entityToDelete, PowerSchoolSyncResult results);
}