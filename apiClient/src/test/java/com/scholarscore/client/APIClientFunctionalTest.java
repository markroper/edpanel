package com.scholarscore.client;

import com.scholarscore.models.School;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Series of tests to validate that we can communicate with the ScholarScore Endpoint
 *
 * Created by mattg on 7/3/15.
 */
@Test
@ContextConfiguration(locations = { "classpath:scholarscore.xml" })
public class APIClientFunctionalTest extends AbstractTestNGSpringContextTests {

    @Autowired
    public APIClient client;

    public void testAuthenticate() {
        client.authenticate();
        assertTrue(client.isAuthenticated(), "Expected client to be authenticated");
    }

    public void testAddSchool() {
        School school = new School();
        school.setName("Inner City YMCA");
        School result = client.createSchool(school);
        assertNotNull(result, "Expected non-null school");
        assertNotNull(result.getId(), "Expected an identifier for the new school");
    }
}
