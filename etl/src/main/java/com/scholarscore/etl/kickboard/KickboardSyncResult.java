package com.scholarscore.etl.kickboard;

import com.scholarscore.etl.BaseSyncResult;

/**
 * Created by markroper on 4/2/16.
 */
public class KickboardSyncResult extends BaseSyncResult {
    int created = 0;
    int updated = 0;
    int deleted = 0;

    int failedCreated = 0;
    int failedUpdated = 0;
    int failedDeleted = 0;

    public void addCreated(int num) {
        created += num;
    }

    public void addUpdated(int num) {
        updated += num;
    }

    public void addDeleted(int num) {
        deleted += num;
    }

    public void addFailedToCreate(int num) {
        failedCreated += num;
    }

    public void addFailedToUpdate(int num) {
        failedUpdated += num;
    }

    public void addFailedDeleted(int num) {
        failedDeleted += num;
    }

    @Override
    public String getResultString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Behavior events created: " + created + "\n");
        builder.append("Behavior events udpated: " + updated + "\n");
        builder.append("Behavior events deleted: " + deleted + "\n");
        builder.append("Behavior event failed creates: " + failedCreated + "\n");
        builder.append("Behavior event failed updates: " + failedUpdated + "\n");
        builder.append("Behavior event failed deleted: " + failedDeleted + "\n");
        return builder.toString();
    }
}
