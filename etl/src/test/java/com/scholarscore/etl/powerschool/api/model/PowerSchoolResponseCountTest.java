package com.scholarscore.etl.powerschool.api.model;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.etl.powerschool.api.model.assignment.PsAssignmentWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGradeWrapper;
import com.scholarscore.etl.powerschool.api.model.student.PsStudents;
import com.scholarscore.etl.powerschool.api.response.DistrictResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponseInner;
import com.scholarscore.etl.powerschool.api.response.SchoolsResponse;
import com.scholarscore.etl.powerschool.api.response.SectionResponse;
import com.scholarscore.etl.powerschool.api.response.TermResponse;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.School;
import com.scholarscore.models.user.User;
import jdk.nashorn.internal.ir.annotations.Ignore;
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
        DistrictResponse response = client.getDistrict();
        assertNotNull(response);
        assertNotNull(response.district);
        assertNotNull(response.district.uuid);
    }

    @Test
    public void testLoadingSchoolEntitiesAndCounts() throws HttpClientException {
        SchoolsResponse response = testLoadSchools();

        testGetStaffBySchool(response);
        testGetAllStudentsBySchoolId(response);

        testGetCoursesBySchool(response);
        testGetTermBySchool(response);
        testGetAllSectionsBySchoolId(response);
    }
    
    private void testGetAllSectionsBySchoolId(SchoolsResponse response) throws HttpClientException {
        int totalSectionCount = 0;
        for (PsSchool school : response.schools.school) {
            SectionResponse sectionResponse = client.getSectionsBySchoolId(school.id);
            assertNotNull(sectionResponse);
            assertNotNull(sectionResponse.sections.section);
            totalSectionCount += sectionResponse.sections.section.size();
            
            // get all assignments for section while we're here!
            testGetAssignmentsBySectionId(sectionResponse);
            testGetStudentSectionGradesBySectionId(sectionResponse);
        }
        assertEquals(totalSectionCount, TOTAL_SECTIONS, "Total section expected is " 
                + TOTAL_SECTIONS + " but got " + totalSectionCount );
    }
    
    private void testGetStudentSectionGradesBySectionId(SectionResponse sectionResponse) throws HttpClientException { 
        int totalStudentSectionGradesCount = 0;
        for (PsSection section : sectionResponse.sections.section) {
            PsResponse<PsSectionGradeWrapper> studentSectionGradeResponse = client.getSectionScoresBySectionId(section.id);
            assertNotNull(studentSectionGradeResponse);
            assertNotNull(studentSectionGradeResponse.record);
            totalStudentSectionGradesCount += studentSectionGradeResponse.record.size();
        }
        assertEquals(totalStudentSectionGradesCount, TOTAL_STUDENT_SECTION_GRADES, "Total StudentSectionGrades expected is " 
                + TOTAL_STUDENT_SECTION_GRADES + " but got " + totalStudentSectionGradesCount );
    }

    private SchoolsResponse testLoadSchools() throws HttpClientException {
        SchoolsResponse response = client.getSchools();
        assertNotNull(response);
        assertNotNull(response.schools);
        assertEquals(response.schools.school.size(), TOTAL_SCHOOLS);
        Collection<School> schools = response.toInternalModel();
        assertEquals(schools.size(), TOTAL_SCHOOLS, "Total schools expected is " + TOTAL_SCHOOLS + " but got " + schools.size() );
        return response;
    }
    
    private void testGetStaffBySchool(SchoolsResponse schoolsResponse) throws HttpClientException {
        int totalCountStaff = 0;
        for (PsSchool school : schoolsResponse.schools.school) {
            PsStaffs response = client.getStaff(school.id);
            assertNotNull(response);
            Collection<User> internalModel = response.toInternalModel();
            assertNotNull(internalModel);
            totalCountStaff += internalModel.size();
        }
        assertEquals(totalCountStaff, TOTAL_STAFF, "Total Staff expected is " + TOTAL_STAFF + " but got " + totalCountStaff );
    }
    
    private void testGetTermBySchool(SchoolsResponse schoolsResponse) throws HttpClientException {
        int totalCount = 0;
        for (PsSchool school : schoolsResponse.schools.school) {
            TermResponse termResponse = client.getTermsBySchoolId(school.id);
            assertNotNull(termResponse);
            assertNotNull(termResponse.terms);
            assertNotNull(termResponse.terms.term);
            assertTrue(termResponse.terms.term.size() > 0);
            totalCount += termResponse.terms.term.size();
        }
        assertEquals(totalCount, TOTAL_TERMS, "Total Terms expected is " + TOTAL_TERMS + " but got " + totalCount );
    }
    
    private void testGetCoursesBySchool(SchoolsResponse schoolsResponse) throws HttpClientException {
        int totalCount = 0;
        for (PsSchool school : schoolsResponse.schools.school) {
            PsCourses response = client.getCoursesBySchool(school.id);
            assertNotNull(response);
            // no data is in any of the 3 schools thus we cannot verify the data in powerschool
            totalCount += response.toInternalModel().size();
        }
        assertEquals(totalCount, TOTAL_COURSES, "Total Courses expected is " + TOTAL_COURSES + " but got " + totalCount);
    }
    
    private void testGetAllStudentsBySchoolId(SchoolsResponse schoolsResponse) throws HttpClientException {
        int totalCount = 0;
        for (PsSchool school : schoolsResponse.schools.school) {
            PsStudents response = client.getStudentsBySchool(school.id);
            Assert.assertNotNull(response);
            totalCount += response.toInternalModel().size();
        }
        assertEquals(totalCount, TOTAL_STUDENTS, "Total Students expected is " + TOTAL_STUDENTS + " but got " + totalCount);
    }
    
    private void testGetAssignmentsBySectionId(SectionResponse sectionResponse) throws HttpClientException {
        int assignmentCount = 0;
        for (PsSection section : sectionResponse.sections.section) {
            PsResponse<PsAssignmentWrapper> assignmentResponse = client.getAssignmentsBySectionId(section.id);
            assignmentCount += assignmentResponse.record.size();
        }
        assertEquals(assignmentCount, TOTAL_ASSIGNMENTS, "Total Assignments expected is " + TOTAL_ASSIGNMENTS + " but got " + assignmentCount);
    }

}
