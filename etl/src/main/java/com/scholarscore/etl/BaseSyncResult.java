package com.scholarscore.etl;

/**
 * User: jordan
 * Date: 11/25/15
 * Time: 5:48 PM
 */
public abstract class BaseSyncResult implements SyncResult {
    
    @Override
    public String toString() { 
        return getResultString();
    }
}
