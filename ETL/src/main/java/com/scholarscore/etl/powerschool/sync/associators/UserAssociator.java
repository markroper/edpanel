package com.scholarscore.etl.powerschool.sync.associators;

import com.scholarscore.models.user.User;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/27/15.
 */
public abstract class UserAssociator<T extends User> {
    public abstract T findBySourceSystemId(Long ssid);

    public abstract T findByOtherId(Long otherId);

    public abstract void associateIds(Long ssid, Long otherId);

    public abstract void addOtherIdMap(ConcurrentHashMap<Long, T> entriesToAdd);

}
