package com.scholarscore.etl;

/**
 * Created by mattg on 7/3/15.
 */
public interface IETLEngine {

    /**
     * Scorched earth - create everything don't try and resolve what is present
     */
    MigrationResult migrateDistrict();
}
