package com.scholarscore.etl.powerschool;

import com.scholarscore.etl.powerschool.api.response.DistrictResponse;
import com.scholarscore.etl.powerschool.api.response.SchoolsResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.client.PowerSchoolClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * A simple functional client test to prove that we can get results from invoking the API
 *
 * Created by mattg on 7/2/15.
 */
@Test
@ContextConfiguration(locations = { "classpath:powerschool.xml" })
public class ClientFunctionalTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private IPowerSchoolClient client;


    public void testLoadSchools() {
        SchoolsResponse response = client.getSchools();
        assertNotNull(response);
        assertNotNull(response.schools);
        assertTrue(response.schools.school.size() > 0);
    }

    public void testLoadDistrict() {
        DistrictResponse response = client.getDistrict();

        assertNotNull(response);
        assertNotNull(response.district);
    }
}
