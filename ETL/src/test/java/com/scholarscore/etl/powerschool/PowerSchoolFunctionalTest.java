package com.scholarscore.etl.powerschool;

import com.scholarscore.etl.powerschool.api.model.*;
import com.scholarscore.etl.powerschool.api.response.*;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.client.PowerSchoolClient;
import com.scholarscore.models.IStaff;
import com.scholarscore.models.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * A simple functional client test to prove that we can get results from invoking the API
 *
 * Created by mattg on 7/2/15.
 */
@Test
@ContextConfiguration(locations = { "classpath:powerschool.xml" })
public class PowerSchoolFunctionalTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private IPowerSchoolClient client;

    public void testLoadSchools() {
        SchoolsResponse response = client.getSchools();
        assertNotNull(response);
        assertNotNull(response.schools);
        assertTrue(response.schools.school.size() > 0);
    }

    public void testGetStaffBySchool() {
        for (School school : client.getSchools().schools.school) {
            Staffs response = client.getStaff(school.id);
            System.out.println(response);
            assertNotNull(response);
            Collection<User> internalModel = response.toInternalModel();
            assertNotNull(internalModel);
        }
    }

    public void testExecuteNamedQueryRoom() {
        String json = client.executeNamedQuery("room");
        System.out.println(json);
        assertNotNull(json);
    }

    public void testGetSectionsBySchool() {
        for (School school : client.getSchools().schools.school) {
            SectionResponse sectionResponse = client.getSectionsBySchoolId(school.id);
            assertNotNull(sectionResponse);
            assertNotNull(sectionResponse.sections.section);
        }
    }

    public void testGetTermBySchool() {
        for (School school : client.getSchools().schools.school) {
            TermResponse termResponse = client.getTermsBySchoolId(school.id);
            assertNotNull(termResponse);
        }
    }

    public void testGetCoursesBySchool() {
        for (School school : client.getSchools().schools.school) {
            Courses response = client.getCoursesBySchool(school.id);
            assertNotNull(response);
            // no data is in any of the 3 schools thus we cannot verify the data in powerschool
        }
    }

    public void testLoadDistrict() {
        DistrictResponse response = client.getDistrict();
        assertNotNull(response);
        assertNotNull(response.district);
        assertNotNull(response.district.uuid);
    }

    public void testGetAsMap() {
        Object response = client.getAsMap(PowerSchoolClient.PATH_RESOURCE_DISTRICT);
        Assert.assertNotNull(response);
    }

    public void testGetAllStudentsBySchoolId() {
        for (School school : client.getSchools().schools.school) {
            Students response = client.getStudentsBySchool(school.id);
            Assert.assertNotNull(response);
        }
    }
}
