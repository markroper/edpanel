package com.scholarscore.etl.deanslist;

import com.scholarscore.etl.IETLEngine;
import com.scholarscore.etl.SyncResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

/**
 * User: jordan
 * Date: 7/28/15
 * Time: 7:08 PM
 */
@Test
@ContextConfiguration(locations = {"classpath:dl_etl.xml"})
public class E2EIntegrationTest extends AbstractTestNGSpringContextTests {

    final static Logger logger = LoggerFactory.getLogger(E2EIntegrationTest.class);

    @Autowired
    private IETLEngine engine;

    // Test pulls behaviors from deanslist and matches to students in scholarscore
    public void testMigrateDistrict() {
        SyncResult result = engine.syncDistrict();
        logger.info("Migration result: " + result);
        assertNotNull(result, "Expected non-null migration result for district");
    }
}
