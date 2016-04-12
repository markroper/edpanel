package com.scholarscore.etl.powerschool;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.etl.EntitySyncResult;
import com.scholarscore.etl.IEtlEngine;
import com.scholarscore.etl.PowerSchoolSyncResult;
import com.scholarscore.etl.SyncResult;
import com.scholarscore.etl.powerschool.client.IPowerSchoolClient;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.attendance.SchoolDay;
import com.scholarscore.models.grade.StudentSectionGrade;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * User: jordan
 * Date: 3/31/16
 * Time: 1:57 PM
 */
@Test(groups = { "unit" })
@ContextConfiguration(locations = { "classpath:mock-powerschool.xml" })
public class EtlEngineUnitTest extends AbstractTestNGSpringContextTests {

    @Autowired
    @Qualifier("testPowerSchoolClient")
    private IPowerSchoolClient client;

    @Autowired
    @Qualifier("testEdPanelClient")
    private IAPIClient edPanel;

    @Autowired
    @Qualifier("etlEngine")
    private IEtlEngine etlEngine;
    
    @Test
    public void testEtlSpringMapping() { 
        assertNotNull(client);
        assertNotNull(edPanel);
        assertNotNull(etlEngine);
    }
    
    @Test
    public void testEtlMigration() throws HttpClientException { 
        SyncResult result = etlEngine.syncDistrict();
        PowerSchoolSyncResult powerSchoolSyncResult = null;
        if (result instanceof PowerSchoolSyncResult) {
            powerSchoolSyncResult = (PowerSchoolSyncResult) result;
        } else throw new RuntimeException("Cannot cast result to PowerschoolSyncResult");

        final int EXPECTED_SCHOOLS_CREATED = 4;
        final int EXPECTED_TERMS_CREATED = 7;
        final int EXPECTED_COURSES_CREATED = 27;
        final int EXPECTED_SECTIONS_CREATED = 81;

        // verify the results reported match what we expect
        checkResults(powerSchoolSyncResult.getSchools(), "schools", SyncType.CREATED, EXPECTED_SCHOOLS_CREATED);
        checkResults(powerSchoolSyncResult.getTerms(), "terms", SyncType.CREATED, EXPECTED_TERMS_CREATED);
        checkResults(powerSchoolSyncResult.getCourses(), "courses", SyncType.CREATED, EXPECTED_COURSES_CREATED);
        checkResults(powerSchoolSyncResult.getSections(), "sections", SyncType.CREATED, EXPECTED_SECTIONS_CREATED);
        
        // verify that what was actually written back to edpanel is the same as what's expected
        // TODO ETL: add specific counts for calls to ensure they don't subtly change due to bugs
        verify(edPanel, atLeastOnce()).getStudents(anyLong());
        verify(edPanel, atLeastOnce()).getTeachers();
        verify(edPanel, atLeastOnce()).getAdministrators();

        verify(edPanel, atLeastOnce()).getSchools();
        verify(edPanel, atLeastOnce()).getSchoolYears(anyLong());
        verify(edPanel, atLeastOnce()).getCourses(anyLong());
        verify(edPanel, atLeastOnce()).getSections(anyLong());
        verify(edPanel, atLeastOnce()).getGpas();

        // these should be called when the remaining empty responses are replaced with populated ones
//        verify(edPanel, atLeastOnce()).getStudentAssignments(anyLong(),anyLong(),anyLong(),anyLong(),anyLong());
//        verify(edPanel, atLeastOnce()).getSectionAssignments(anyLong(), anyLong(), anyLong(), anyLong());
//        verify(edPanel, atLeastOnce()).createSectionAssignment(anyLong(), anyLong(), anyLong(), anyLong(), any(Assignment.class));
        verify(edPanel, atLeastOnce()).getStudentSectionGrades(anyLong(), anyLong(), anyLong(), anyLong());
        verify(edPanel, atLeastOnce()).getSchoolDays(anyLong());
        
        verify(edPanel, atLeastOnce()).createStudent(any(Student.class));
        verify(edPanel, atLeastOnce()).createTeacher(any(Staff.class));
        verify(edPanel, atLeastOnce()).createAdministrator(any(Staff.class));
        verify(edPanel, atLeastOnce()).getAttendance(anyLong(), anyLong());
        
        // schools aren't bulk created, so we can expect this endpoint to be called the same # of times as 'schools created' reported above
        verify(edPanel, times(EXPECTED_SCHOOLS_CREATED)).createSchool(any(School.class));
        verify(edPanel, atLeastOnce()).createSchoolYear(anyLong(), any(SchoolYear.class));
        verify(edPanel, atLeastOnce()).createTerm(anyLong(), anyLong(), any(Term.class));
        verify(edPanel, atLeastOnce()).createCourse(anyLong(), any(Course.class));
        verify(edPanel, atLeastOnce()).createSection(anyLong(), anyLong(), anyLong(), any(Section.class));
        verify(edPanel, atLeastOnce()).createStudentSectionGrades(anyLong(), anyLong(), anyLong(), anyLong(), anyListOf(StudentSectionGrade.class));
        verify(edPanel, atLeastOnce()).createSchoolDays(anyLong(), anyListOf(SchoolDay.class));
        verify(edPanel, atLeastOnce()).createAttendances(anyLong(), anyLong(), anyListOf(Attendance.class));        
        
        // apparently this update method gets called, even during first run
        verify(edPanel, atLeastOnce()).updateAdvisors(anyLong());
        
        // uncommenting this line throws an exception if any edpanel method is called but not verified above
//        Mockito.verifyNoMoreInteractions(edPanel);
    }
    
    enum SyncType { CREATED, UPDATED }
    
    private void checkResults(EntitySyncResult result, String entityName, SyncType type, int expected) {
        int entityResults = -1;
        switch (type) {
            case CREATED:
                entityResults = result.getCreated().size();
                break;
            case UPDATED:
                entityResults = result.getUpdated().size();
                break;
        }
        assertEquals("Expected " + expected + " " + entityName + " in PowerSchoolResults, but found " + entityResults + " instead",
                entityResults, expected);

    }
    
}
