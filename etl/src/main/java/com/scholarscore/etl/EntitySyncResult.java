package com.scholarscore.etl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * For a given entity (e.g. School, Section, Student) stores the associated
 * source system ID and EdPanel ID for entities created, updated, and deleted during a sync.
 *
 * Created by markroper on 10/29/15.
 */
public class EntitySyncResult {
    //Successful operations
    protected ConcurrentHashMap<Long, Long> created = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<Long, Long> updated = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<Long, Long> deleted = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<Long, Long> untouched = new ConcurrentHashMap<>();
    //Unsuccessful operations
    protected List<Long> failedCreates = Collections.synchronizedList(new ArrayList<>());
    protected ConcurrentHashMap<Long, Long> failedUpdates = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<Long, Long> failedDeletes = new ConcurrentHashMap<>();
    //The failed gets store their parent ssid to edPanelId mapping
    protected ConcurrentHashMap<Long, Long> sourceGetFailed = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<Long, Long> edPanelGetFailed = new ConcurrentHashMap<>();

    /*
    *   Helper methods
    */
    public void created(long ssid, long edPanelId) {
        created.put(ssid, edPanelId);
    }

    public void updated(long ssid, long edPanelId) {
        updated.put(ssid, edPanelId);
    }

    public void deleted(long ssid, long edPanelId) {
        deleted.put(ssid, edPanelId);
    }
    
    public void untouched(long ssid, long edPanelId) { untouched.put(ssid, edPanelId); }

    public void failedCreate(long ssid) {
        failedCreates.add(ssid);
    }

    public void failedUpdate(long ssid, long edPanelId) {
        failedUpdates.put(ssid, edPanelId);
    }

    public void failedDelete(long ssid, long edPanelId) {
        failedDeletes.put(ssid, edPanelId);
    }

    /*
    *   GETTERS & SETTERS
    */
    public ConcurrentHashMap<Long, Long> getCreated() {
        return created;
    }

    public void setCreated(ConcurrentHashMap<Long, Long> created) {
        this.created = created;
    }

    public ConcurrentHashMap<Long, Long> getUpdated() {
        return updated;
    }

    public ConcurrentHashMap<Long, Long> getDeleted() {
        return deleted;
    }

    public ConcurrentHashMap<Long, Long> getUntouched() {
        return untouched;
    }

    public List<Long> getFailedCreates() {
        return failedCreates;
    }

    public ConcurrentHashMap<Long, Long> getFailedUpdates() {
        return failedUpdates;
    }

    public ConcurrentHashMap<Long, Long> getFailedDeletes() {
        return failedDeletes;
    }

    public ConcurrentHashMap<Long, Long> getSourceGetFailed() {
        return sourceGetFailed;
    }

    public ConcurrentHashMap<Long, Long> getEdPanelGetFailed() {
        return edPanelGetFailed;
    }
}
