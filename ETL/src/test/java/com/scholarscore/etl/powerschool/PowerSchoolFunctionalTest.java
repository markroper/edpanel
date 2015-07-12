package com.scholarscore.etl.powerschool;

import com.scholarscore.etl.powerschool.api.model.School;
import com.scholarscore.etl.powerschool.api.model.Staff;
import com.scholarscore.etl.powerschool.api.response.*;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.etl.powerschool.client.PowerSchoolClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

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

    public void testCreateStaff() {
        Staff staff = new Staff();
        staff.admin_username = "mroper";
        staff.teacher_username = "mroper";

        School indexZeroSchool = client.getSchools().schools.school.get(0);
        System.out.println("Using school: " + indexZeroSchool.id + ", name: " + indexZeroSchool.name);
        client.createStaff(staff, indexZeroSchool.id);
    }

    public void testLoadSchools() {
        SchoolsResponse response = client.getSchools();
        assertNotNull(response);
        assertNotNull(response.schools);
        assertTrue(response.schools.school.size() > 0);
    }

    public void testGetStaffBySchool() {
        for (School school : client.getSchools().schools.school) {
            StaffResponse response = client.getStaff(school.id);
            assertNotNull(response);
        }
    }

    public void testGetSectionsBySchool() {
        for (School school : client.getSchools().schools.school) {
            SectionResponse sectionResponse = client.getSectionsBySchoolId(school.id);
            assertNotNull(sectionResponse);
            assertNotNull(sectionResponse.sections);
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
            CourseResponse response = client.getCoursesBySchool(school.id);
            assertNotNull(response);
            assertNotNull(response.courses);
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

    public void testGetAllDistrictStudents() {
        StudentResponse response = client.getDistrictStudents();
        Assert.assertNotNull(response);
    }
}
