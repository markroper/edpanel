package com.scholarscore.etl.powerschool.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.etl.ISync;
import com.scholarscore.etl.PowerSchoolSyncResult;

import java.util.concurrent.ConcurrentHashMap;

/**
 * User: jordan
 * Date: 4/6/16
 * Time: 5:36 PM
 * 
 * This class can be extended by sync objects that only care about reading from the source system,
 * but don't need to reconcile anything from edpanel or write back to it
 */
public abstract class ReadOnlySyncBase<T> implements ISync<T> {

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
        return sourceRecords;
    }

    protected abstract ConcurrentHashMap<Long, T> resolveAllFromSourceSystem() throws HttpClientException;

    protected abstract void handleSourceGetFailure(PowerSchoolSyncResult results);
}
