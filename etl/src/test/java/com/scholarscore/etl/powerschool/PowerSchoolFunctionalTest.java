package com.scholarscore.etl.powerschool;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.etl.powerschool.api.model.PsCourses;
import com.scholarscore.etl.powerschool.api.model.PsSchool;
import com.scholarscore.etl.powerschool.api.model.PsStaffs;
import com.scholarscore.etl.powerschool.api.model.student.PsStudents;
import com.scholarscore.etl.powerschool.api.response.PsDistrictResponse;
import com.scholarscore.etl.powerschool.api.response.PsSchoolsResponse;
import com.scholarscore.etl.powerschool.api.response.PsSectionResponse;
import com.scholarscore.etl.powerschool.api.response.PsTermResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.user.Staff;
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
@Test(groups = { "functional" })
@ContextConfiguration(locations = { "classpath:powerschool.xml" })
public class PowerSchoolFunctionalTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private IPowerSchoolClient client;

    public void testLoadSchools() throws HttpClientException {
        PsSchoolsResponse response = client.getSchools();
        assertNotNull(response);
        assertNotNull(response.schools);
        assertTrue(response.schools.school.size() > 0);
    }

    public void testGetStaffBySchool() throws HttpClientException {
        for (PsSchool school : client.getSchools().schools.school) {
            PsStaffs response = client.getStaff(school.id);
            System.out.println(response);
            assertNotNull(response);
            Collection<Staff> internalModel = response.toInternalModel();
            assertNotNull(internalModel);
        }
    }

    public void testGetSectionsBySchool() throws HttpClientException {
        for (PsSchool school : client.getSchools().schools.school) {
            PsSectionResponse psSectionResponse = client.getSectionsBySchoolId(school.id);
            assertNotNull(psSectionResponse);
            assertNotNull(psSectionResponse.sections.section);
        }
    }

    public void testGetTermBySchool() throws HttpClientException {
        for (PsSchool school : client.getSchools().schools.school) {
            PsTermResponse psTermResponse = client.getTermsBySchoolId(school.id);
            assertNotNull(psTermResponse);
        }
    }

    public void testGetCoursesBySchool() throws HttpClientException {
        for (PsSchool school : client.getSchools().schools.school) {
            PsCourses response = client.getCoursesBySchool(school.id);
            assertNotNull(response);
            // no data is in any of the 3 schools thus we cannot verify the data in powerschool
        }
    }

    public void testLoadDistrict() throws HttpClientException {
        PsDistrictResponse response = client.getDistrict();
        assertNotNull(response);
        assertNotNull(response.district);
        assertNotNull(response.district.uuid);
    }

    public void testGetAllStudentsBySchoolId() throws HttpClientException {
        for (PsSchool school : client.getSchools().schools.school) {
            PsStudents response = client.getStudentsBySchool(school.id);
            Assert.assertNotNull(response);
        }
    }
}
