package com.scholarscore.etl.powerschool.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.etl.PowerSchoolSyncResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: jordan
 * Date: 4/5/16
 * Time: 8:12 PM
 */
public abstract class SyncBase<T> extends ReadOnlySyncBase<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(CourseSync.class);

    private List<T> objectsToBulkCreate;

    @Override
    public ConcurrentHashMap<Long, T> syncCreateUpdateDelete(PowerSchoolSyncResult results) {
        objectsToBulkCreate = new ArrayList<>();
        ConcurrentHashMap<Long, T> sourceRecords = super.syncCreateUpdateDelete(results);
        ConcurrentHashMap<Long, T> edpanelRecords;
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

        if (null == sourceRecords) { sourceRecords = new ConcurrentHashMap<>(); }
        if (null == edpanelRecords) { edpanelRecords = new ConcurrentHashMap<>(); }
        
        for (Map.Entry<Long, T> entry : sourceRecords.entrySet()) {
            T sourceRecord = entry.getValue();
            T edPanelRecord = edpanelRecords.get(entry.getKey());
            if (null == edPanelRecord) {    // doesn't exist, must create
                createEdPanelRecord(sourceRecord, results);
            } else {                        // already exists, must update
                updateEdPanelRecord(sourceRecord, edPanelRecord, results);
            }
            
            // for some entities -- like Section, which usually has many StudentSectionGrades and Attendance for each section -- 
            // we want a chance to do something (e.g. create a new instance of some Child Sync classes) for every parent record we encounter
            entitySynced(sourceRecord, results);
        }
        // ... and sometimes we may want a chance to perform other actions, once after completion
        allEntitiesSynced(results);

        createBulkEdPanelRecords(objectsToBulkCreate);
        
        //Delete anything IN EdPanel that is NOT in source system
        for (Map.Entry<Long, T> entry : edpanelRecords.entrySet()) {
            if (!sourceRecords.containsKey(entry.getKey())) {
                deleteEdPanelRecord(entry.getValue(), results);
            }
        }
        return sourceRecords;
    }

    protected void entitySynced(T sourceRecord, PowerSchoolSyncResult results) {
        // overriding this method is purely optional, providing a hook for any additional actions that need to be done on EVERY record
    }
    
    protected void allEntitiesSynced(PowerSchoolSyncResult results) {
        // overriding this method is purely optional, providing a hook for any additional actions that need to be done ONCE, after all records
    }

    protected final void enqueueForBulkCreate(T entityToCreate) { objectsToBulkCreate.add(entityToCreate); }
    
    protected void createBulkEdPanelRecords(List<T> entitiesToCreate) { 
        if (objectsToBulkCreate != null && objectsToBulkCreate.size() > 0) {
            throw new RuntimeException("Records added to bulk collection but createBulkEdPanelRecords not overridden!");
        }
    }
    
    protected abstract ConcurrentHashMap<Long, T> resolveFromEdPanel() throws HttpClientException;

    protected abstract void handleEdPanelGetFailure(PowerSchoolSyncResult results);
    
    protected abstract void createEdPanelRecord(T entityToSave, PowerSchoolSyncResult results);

    protected abstract void updateEdPanelRecord(T sourceSystemEntity, T edPanelEntity, PowerSchoolSyncResult results);
    
    protected abstract void deleteEdPanelRecord(T entityToDelete, PowerSchoolSyncResult results);
}