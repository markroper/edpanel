package com.scholarscore.etl.powerschool.sync.associators;

import com.scholarscore.models.user.Person;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by markroper on 10/27/15.
 */
public class StaffAssociator extends UserAssociator<Person> {
    private ConcurrentHashMap<Long, Long> ssidToLocalId = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, Person> staff = new ConcurrentHashMap<>();

    @Override
    public Person findBySourceSystemId(Long ssid) {
        Long otherId = ssidToLocalId.get(ssid);
        return findByOtherId(otherId);
    }

    @Override
    public Person findByOtherId(Long otherId) {
        if(null == otherId) {
            return null;
        }
        return staff.get(otherId);
    }

    @Override
    public void associateIds(Long ssid, Long otherId) {
        ssidToLocalId.put(ssid, otherId);
    }

    @Override
    public void addOtherIdMap(ConcurrentHashMap<Long, Person> entriesToAdd) {
        staff.putAll(entriesToAdd);
    }
}
