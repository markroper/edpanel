package com.scholarscore.etl.deanslist;

import com.scholarscore.etl.deanslist.client.IDeansListClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

/**
 * A simple test to invoke the API and get data
 * 
 * Created by jwinch on 7/22/15.
 */
@Test
@ContextConfiguration(locations = { "classpath:deanslist.xml" })
public class DeansListFunctionalTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private IDeansListClient client;
    
    public void testGetStudents() { 
        client.getStudents();
    }
    
//    public void testGetBehaviorData() { 
//        client.getBehaviorData();
//    }
    
    /* 
    * 
    * public void testLoadSchools() {
        SchoolsResponse response = client.getSchools();
        assertNotNull(response);
        assertNotNull(response.schools);
        assertTrue(response.schools.school.size() > 0);
    }*/
    
}
