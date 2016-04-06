package com.scholarscore.etl.powerschool.sync;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.etl.PowerSchoolSyncResult;

import java.util.concurrent.ConcurrentHashMap;

/**
 * User: jordan
 * Date: 4/6/16
 * Time: 5:36 PM
 */
public abstract class ReadOnlySyncBase<T> extends SyncBase<T> {

    // By ensuring the following methods do nothing, we have a read-only sync object
    // whose sole purpose is to resolveAllFromSourceSystem and return that value
    @Override protected final ConcurrentHashMap<Long, T> resolveFromEdPanel() throws HttpClientException { return null; }

    @Override protected final void handleEdPanelGetFailure(PowerSchoolSyncResult results) { }

    @Override protected final void createEdPanelRecord(T entityToSave, PowerSchoolSyncResult results) { }

    @Override protected final void updateEdPanelRecord(T sourceSystemEntity, T edPanelEntity, PowerSchoolSyncResult results) { }

    @Override protected final void deleteEdPanelRecord(T entityToDelete, PowerSchoolSyncResult results) { }
}
