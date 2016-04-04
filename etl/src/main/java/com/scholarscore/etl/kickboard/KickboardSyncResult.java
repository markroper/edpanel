package com.scholarscore.etl.kickboard;

import com.scholarscore.etl.BaseSyncResult;

/**
 * Created by markroper on 4/2/16.
 */
public class KickboardSyncResult extends BaseSyncResult {
    int created = 0;
    int updated = 0;
    int deleted = 0;

    int scoresCreated = 0;
    int scoresUpdated = 0;
    int scoresDeleted = 0;

    int failedCreated = 0;
    int failedUpdated = 0;
    int failedDeleted = 0;

    int failedScoresCreated = 0;
    int failedScoresUpdated = 0;
    int failedScoresDeleted = 0;

    public void addCreated(int num) {
        created += num;
    }

    public void addUpdated(int num) {
        updated += num;
    }

    public void addDeleted(int num) {
        deleted += num;
    }

    public void addScoreCreated(int num) {
        scoresCreated += num;
    }

    public void addScoreUpdated(int num) {
        scoresUpdated += num;
    }

    public void addScoreDeleted(int num) {
        scoresDeleted += num;
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

    public void addFailedScoreCreate(int num) {
        failedScoresCreated += num;
    }

    public void addFailedScoreUpdate(int num) {
        failedScoresUpdated += num;
    }

    public void addFailedScoreDeleted(int num) {
        failedScoresDeleted += num;
    }

    @Override
    public String getResultString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\nBehavior events created: " + created + "\n");
        builder.append("Behavior events updated: " + updated + "\n");
        builder.append("Behavior events deleted: " + deleted + "\n");
        builder.append("Behavior scores created: " + scoresCreated + "\n");
        builder.append("Behavior scores updated: " + scoresUpdated + "\n");
        builder.append("Behavior scores deleted: " + scoresDeleted + "\n");

        builder.append("Behavior event failed creates: " + failedCreated + "\n");
        builder.append("Behavior event failed updates: " + failedUpdated + "\n");
        builder.append("Behavior event failed deleted: " + failedDeleted + "\n");
        builder.append("Behavior score failed creates: " + failedScoresCreated + "\n");
        builder.append("Behavior score failed updates: " + failedScoresUpdated + "\n");
        builder.append("Behavior score failed deletes: " + failedScoresDeleted + "\n");

        return builder.toString();
    }
}
