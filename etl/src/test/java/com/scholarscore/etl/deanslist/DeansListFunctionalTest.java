package com.scholarscore.etl.deanslist;

import com.scholarscore.etl.deanslist.api.response.BehaviorResponse;
import com.scholarscore.etl.deanslist.client.IDeansListClient;
import com.scholarscore.models.Behavior;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A simple test to invoke the API and get data
 * 
 * Created by jwinch on 7/22/15.
 */
@Test(groups = { "functional" })
@ContextConfiguration(locations = {"classpath:deanslist.xml"})
public class DeansListFunctionalTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private IDeansListClient client;

    public void testGetStudents() {
        client.getBehaviorData();
    }
    
}
