package com.scholarscore.etl.powerschool.sync;

import com.scholarscore.etl.EtlEngine;
import com.scholarscore.etl.PowerSchoolSyncResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * User: jordan
 * Date: 4/12/16
 * Time: 10:02 PM
 */
public abstract class MultiThreadedSyncBase<T> extends SyncBase<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(MultiThreadedSyncBase.class);
    
    ExecutorService executor;
    
    @Override
    public ConcurrentHashMap<Long, T> syncCreateUpdateDelete(PowerSchoolSyncResult results) {
        executor = Executors.newFixedThreadPool(EtlEngine.THREAD_POOL_SIZE);
        ConcurrentHashMap<Long, T> resultMap = super.syncCreateUpdateDelete(results);
        executor.shutdown();
        //Spin while we wait for all the threads to complete
        try {
            if (!executor.awaitTermination(EtlEngine.TOTAL_TTL_MINUTES, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch(InterruptedException e) {
            LOGGER.error("Executor thread pool interrupted " + e.getMessage());
        }
        return resultMap;
    }

    @Override
    protected void entitySynced(T sourceRecord, PowerSchoolSyncResult results) {
        executor.execute(buildRunnable(sourceRecord, results));
    }
    
    protected abstract Runnable buildRunnable(T sourceRecord, PowerSchoolSyncResult results);
}
