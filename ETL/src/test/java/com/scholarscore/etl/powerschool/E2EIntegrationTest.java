package com.scholarscore.etl.powerschool;

import com.scholarscore.etl.IETLEngine;
import com.scholarscore.etl.MigrationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

/**
 * Created by mattg on 7/3/15.
 */
@Test
@ContextConfiguration(locations = {"classpath:etl.xml"})
public class E2EIntegrationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private IETLEngine engine;

    // Test migrates everything
    public void testMigrateDistrict() {
        MigrationResult result = engine.syncDistrict();
        System.out.println("Migration result: " + result);
        assertNotNull(result, "Expected non-null migration result for district");
    }
}
