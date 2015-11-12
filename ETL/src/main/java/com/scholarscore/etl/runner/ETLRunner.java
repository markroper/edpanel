package com.scholarscore.etl.runner;

import com.scholarscore.etl.IETLEngine;
import com.scholarscore.etl.SyncResult;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: jordan
 * Date: 11/12/15
 * Time: 5:56 PM
 */
public class ETLRunner {
    
    @Autowired
    private IETLEngine engine;

    // Test migrates everything
    public void migrateDistrict() {
        System.out.println("Migration running!! engine : " + engine);
//        SyncResult result = engine.syncDistrict();
//        System.out.println("Migration result: " + result);
//        assertNotNull(result, "Expected non-null migration result for district");
    }

}
