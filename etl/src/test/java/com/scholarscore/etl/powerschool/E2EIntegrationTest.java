package com.scholarscore.etl.powerschool;

import com.scholarscore.etl.IEtlEngine;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.SyncResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

/**
 * Created by mattg on 7/3/15.
 */
@Test
@ContextConfiguration(locations = {"classpath:ps_etl.xml"})
public class E2EIntegrationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private IEtlEngine engine;

    // Test migrates everything
    public void testMigrateDistrict() {
        SyncResult result = engine.syncDistrict();
        System.out.println("Migration result: " + result);
        assertNotNull(result, "Expected non-null migration result for district");
    }
}
