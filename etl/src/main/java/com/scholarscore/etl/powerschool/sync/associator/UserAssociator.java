package com.scholarscore.etl.powerschool.sync.associator;

import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.User;

import java.util.concurrent.ConcurrentHashMap;

/**
 * In PowerSchool, users appear to be globally unique within a powerschool install, but a single user
 * have multiple identities. So there may be multiple Teacher rows for a single user if that user has taught at
 * multiple schools in the district.  In EdPanel, a User has a one to one relationship with a student, teacher, or
 * administrative identity and a foreign key to the current active school.
 *
 * For this reason, in order to migrate and sync students, administrators, and teachers, the ETL code needs to
 * store not one sourceSystemId for each of these entities, but two, one that is the underlying user ID and the other
 * that is the Teacher, Student or Admin ID.
 *
 * This class encapsulates the data structures and lookup methods required to handle this mapping.
 *
 * 
 * Created by markroper on 10/27/15.
 */
public abstract class UserAssociator<T extends User> {

    private final ConcurrentHashMap<Long, Long> ssidToLocalIdUser = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, T> users = new ConcurrentHashMap<>();

    public T findBySourceSystemId(Long ssid) {
        Long otherId = ssidToLocalIdUser.get(ssid);
        return findByOtherId(otherId);
    }

    private T findByOtherId(Long otherId) {
        if(null == otherId) {
            return null;
        }
        return users.get(otherId);
    }

    public void associateIds(Long ssid, Long otherId) {
        ssidToLocalIdUser.put(ssid, otherId);
    }

    public void addOtherIdMap(ConcurrentHashMap<Long, T> entriesToAdd) {
        users.putAll(entriesToAdd);
    }

    public ConcurrentHashMap<Long, Long> getSsidToLocalIdUser() {
        return ssidToLocalIdUser;
    }

    public ConcurrentHashMap<Long, T> getUsers() {
        return users;
    }
}
