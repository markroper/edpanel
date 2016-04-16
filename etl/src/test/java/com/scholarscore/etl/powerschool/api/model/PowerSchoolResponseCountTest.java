package com.scholarscore.etl.powerschool.api.model;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.etl.powerschool.api.model.assignment.PsAssignmentWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGradeWrapper;
import com.scholarscore.etl.powerschool.api.model.student.PsStudents;
import com.scholarscore.etl.powerschool.api.response.PsDistrictResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.PsSchoolsResponse;
import com.scholarscore.etl.powerschool.api.response.PsSectionResponse;
import com.scholarscore.etl.powerschool.api.response.PsTermResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.School;
import com.scholarscore.models.user.Staff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.testng.Assert.*;

/**
 * These tests can be run without a DB instance, running server, or valid connection to powerschool
 * 
 * User: jordan
 * Date: 2/2/16
 * Time: 3:44 PM
 */
@Test(groups = { "sanity" })
@ContextConfiguration(locations = { "classpath:mock-powerschool.xml" })
public class PowerSchoolResponseCountTest extends AbstractTestNGSpringContextTests {

    private final int TOTAL_SCHOOLS = 4;
    private final int TOTAL_STUDENTS = 460;
    private final int TOTAL_STAFF = 120;
    private final int TOTAL_TERMS = 28;
    private final int TOTAL_COURSES = 108;
    private final int TOTAL_SECTIONS = 324;
    private final int TOTAL_ASSIGNMENTS = 12879;
    private final int TOTAL_STUDENT_SECTION_GRADES = 7938;
    private final int TOTAL_STUDENT_SECTION_ASSIGNMENTS = 1;
    private final int TOTAL_ATTENDANCES = 1;
    
    @Autowired
    @Qualifier("testPowerSchoolClient")
    private IPowerSchoolClient client;

    @Test
    public void testLoadDistrict() throws HttpClientException {
        PsDistrictResponse response = client.getDistrict();
        assertNotNull(response);
        assertNotNull(response.district);
        assertNotNull(response.district.uuid);
    }

    @Test
    public void testLoadingSchoolEntitiesAndCounts() throws HttpClientException {
        PsSchoolsResponse response = testLoadSchools();

        testGetStaffBySchool(response);
        testGetAllStudentsBySchoolId(response);

        testGetCoursesBySchool(response);
        testGetTermBySchool(response);
        testGetAllSectionsBySchoolId(response);
    }
    
    private void testGetAllSectionsBySchoolId(PsSchoolsResponse response) throws HttpClientException {
        int totalSectionCount = 0;
        for (PsSchool school : response.schools.school) {
            PsSectionResponse psSectionResponse = client.getSectionsBySchoolId(school.id);
            assertNotNull(psSectionResponse);
            assertNotNull(psSectionResponse.sections.section);
            totalSectionCount += psSectionResponse.sections.section.size();
            
            // get all assignments for section while we're here!
            testGetAssignmentsBySectionId(psSectionResponse);
            testGetStudentSectionGradesBySectionId(psSectionResponse);
        }
        assertEquals(totalSectionCount, TOTAL_SECTIONS, "Total section expected is " 
                + TOTAL_SECTIONS + " but got " + totalSectionCount );
    }
    
    private void testGetStudentSectionGradesBySectionId(PsSectionResponse psSectionResponse) throws HttpClientException { 
        int totalStudentSectionGradesCount = 0;
        for (PsSection section : psSectionResponse.sections.section) {
            PsResponse<PsSectionGradeWrapper> studentSectionGradeResponse = client.getSectionScoresBySectionId(section.id);
            assertNotNull(studentSectionGradeResponse);
            assertNotNull(studentSectionGradeResponse.record);
            totalStudentSectionGradesCount += studentSectionGradeResponse.record.size();
        }
        assertEquals(totalStudentSectionGradesCount, TOTAL_STUDENT_SECTION_GRADES, "Total StudentSectionGrades expected is " 
                + TOTAL_STUDENT_SECTION_GRADES + " but got " + totalStudentSectionGradesCount );
    }

    private PsSchoolsResponse testLoadSchools() throws HttpClientException {
        PsSchoolsResponse response = client.getSchools();
        assertNotNull(response);
        assertNotNull(response.schools);
        assertEquals(response.schools.school.size(), TOTAL_SCHOOLS);
        Collection<School> schools = response.toInternalModel();
        assertEquals(schools.size(), TOTAL_SCHOOLS, "Total schools expected is " + TOTAL_SCHOOLS + " but got " + schools.size() );
        return response;
    }
    
    private void testGetStaffBySchool(PsSchoolsResponse psSchoolsResponse) throws HttpClientException {
        int totalCountStaff = 0;
        for (PsSchool school : psSchoolsResponse.schools.school) {
            PsStaffs response = client.getStaff(school.id);
            assertNotNull(response);
            Collection<Staff> internalModel = response.toInternalModel();
            assertNotNull(internalModel);
            totalCountStaff += internalModel.size();
        }
        assertEquals(totalCountStaff, TOTAL_STAFF, "Total Staff expected is " + TOTAL_STAFF + " but got " + totalCountStaff );
    }
    
    private void testGetTermBySchool(PsSchoolsResponse psSchoolsResponse) throws HttpClientException {
        int totalCount = 0;
        for (PsSchool school : psSchoolsResponse.schools.school) {
            PsTermResponse psTermResponse = client.getTermsBySchoolId(school.id);
            assertNotNull(psTermResponse);
            assertNotNull(psTermResponse.terms);
            assertNotNull(psTermResponse.terms.term);
            assertTrue(psTermResponse.terms.term.size() > 0);
            totalCount += psTermResponse.terms.term.size();
        }
        assertEquals(totalCount, TOTAL_TERMS, "Total Terms expected is " + TOTAL_TERMS + " but got " + totalCount );
    }
    
    private void testGetCoursesBySchool(PsSchoolsResponse psSchoolsResponse) throws HttpClientException {
        int totalCount = 0;
        for (PsSchool school : psSchoolsResponse.schools.school) {
            PsCourses response = client.getCoursesBySchool(school.id);
            assertNotNull(response);
            // no data is in any of the 3 schools thus we cannot verify the data in powerschool
            totalCount += response.toInternalModel().size();
        }
        assertEquals(totalCount, TOTAL_COURSES, "Total Courses expected is " + TOTAL_COURSES + " but got " + totalCount);
    }
    
    private void testGetAllStudentsBySchoolId(PsSchoolsResponse psSchoolsResponse) throws HttpClientException {
        int totalCount = 0;
        for (PsSchool school : psSchoolsResponse.schools.school) {
            PsStudents response = client.getStudentsBySchool(school.id);
            Assert.assertNotNull(response);
            totalCount += response.toInternalModel().size();
        }
        assertEquals(totalCount, TOTAL_STUDENTS, "Total Students expected is " + TOTAL_STUDENTS + " but got " + totalCount);
    }
    
    private void testGetAssignmentsBySectionId(PsSectionResponse psSectionResponse) throws HttpClientException {
        int assignmentCount = 0;
        for (PsSection section : psSectionResponse.sections.section) {
            PsResponse<PsAssignmentWrapper> assignmentResponse = client.getAssignmentsBySectionId(section.id);
            assignmentCount += assignmentResponse.record.size();
        }
        assertEquals(assignmentCount, TOTAL_ASSIGNMENTS, "Total Assignments expected is " + TOTAL_ASSIGNMENTS + " but got " + assignmentCount);
    }

}
