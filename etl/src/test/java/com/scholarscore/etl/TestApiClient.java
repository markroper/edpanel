package com.scholarscore.etl;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.client.IAPIClient;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.attendance.SchoolDay;
import com.scholarscore.models.gpa.Gpa;
import com.scholarscore.models.grade.StudentSectionGrade;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * User: jordan
 * Date: 3/31/16
 * Time: 2:07 PM
 */
public class TestApiClient /*extends APIClient*/ {

    private static Long userCounter = 0L;
    private static Long schoolCounter = 0L;
    private static Long schoolYearCounter = 0L;
    private static Long termCounter = 0L;
    private static Long sectionCounter = 0L;
    private static Long courseCounter = 0L;
    private static Long sectionAssignmentCounter = 0L;
    
    private static HashMap<Long, School> schools = new HashMap<>();
    private static HashMap<Long, SchoolYear> schoolYears = new HashMap<>();
    private static HashMap<Long, Staff> staff = new HashMap<>();
    private static HashMap<Long, Student> students = new HashMap<>();
    private static HashMap<Long, Term> terms = new HashMap<>();
    // courseId -> course
    private static HashMap<Long, Course> courseIdToCourse = new HashMap<>();
    // schoolId -> courses
    private static HashMap<Long, HashSet<Course>> schoolIdToCourses = new HashMap<>();
    // sectionId -> section
    private static HashMap<Long, Section> sections = new HashMap<>();
    // schoolId -> sections
    private static HashMap<Long, HashSet<Section>> schoolIdToSections = new HashMap<>();
    // sectionId -> studentSectionGrades
    private static HashMap<Long, HashSet<StudentSectionGrade>> sectionIdToStudentSectionGrades = new HashMap<>();
    private static HashSet<Gpa> gpas = new HashSet<>();
    private static HashMap<Long, Assignment> sectionAssignments = new HashMap<>();
    private static HashMap<Long, HashMap<Long, StudentAssignment>> studentAssignments = new HashMap<>();
    
    public TestApiClient() {
//        super("badusername", "badpass", getURI());
//        super(username, password, uri);
    }

    public static IAPIClient buildTestApiClient() throws HttpClientException {
        IAPIClient testApiClient = Mockito.mock(IAPIClient.class);
        
        // getSchools()
        when(testApiClient.getSchools()).thenReturn(schools.values().toArray(new School[schools.values().size()]));

        // getCourses()
        when(testApiClient.getCourses(anyLong())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Long schoolId = (Long) args[0];
            if (schoolId == null) {
                throw new RuntimeException("SchoolId cannot be null!");
            }
            HashSet<Course> courses = schoolIdToCourses.get(schoolId);
            if (courses == null) { courses = new HashSet<>(); }
            return courses.toArray(new Course[courses.size()]);
        });

        // getGpas()
        when(testApiClient.getGpas()).thenAnswer(invocation -> {
            return gpas.toArray(new Gpa[gpas.size()]);
        });
        
        // getSections()
        when (testApiClient.getSections(anyLong())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Long schoolId = (Long) args[0];
            if (schoolId == null) {
                throw new RuntimeException("SchoolId cannot be null!");
            }
            HashSet<Section> schoolSections = schoolIdToSections.get(schoolId);
            if (schoolSections == null) { schoolSections = new HashSet<>(); }
            return schoolSections.toArray(new Section[schoolSections.size()]);
        });
        
        // edPanel.getStudentSectionGrades(
        // school.getId(),
        //        createdSection.getTerm().getSchoolYear().getId(),
        //        createdSection.getTerm().getId(),
        //        createdSection.getId());
        when(testApiClient.getStudentSectionGrades(anyLong(), anyLong(), anyLong(), anyLong())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Long schoolId = (Long) args[0];
            Long schoolYearId = (Long) args[1];
            Long termId = (Long) args[2];
            Long sectionId = (Long) args[3];
            if (schoolId == null) {
                throw new RuntimeException("SchoolId cannot be null!");
            }
            if (schoolYearId == null) {
                throw new RuntimeException("SchoolYearId cannot be null!");
            }
            if (termId == null) {
                throw new RuntimeException("TermId cannot be null!");
            }
            if (sectionId == null) {
                throw new RuntimeException("SectionId cannot be null!");
            }

            HashSet<StudentSectionGrade> ssgs = sectionIdToStudentSectionGrades.get(sectionId);
            if (ssgs == null) { ssgs = new HashSet<>(); } 
            return ssgs.toArray(new StudentSectionGrade[ssgs.size()]);
        });

        when(testApiClient.getStudentAssignments(anyLong(), anyLong(), anyLong(), anyLong(), anyLong())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Long schoolId = (Long) args[0];
            Long schoolYearId = (Long) args[1];
            Long termId = (Long) args[2];
            Long sectionId = (Long) args[3];
            Long assignmentId = (Long) args[4];
            if (schoolId == null) {
                throw new RuntimeException("SchoolId cannot be null!");
            }
            if (schoolYearId == null) {
                throw new RuntimeException("SchoolYearId cannot be null!");
            }
            if (termId == null) {
                throw new RuntimeException("TermId cannot be null!");
            }
            if (sectionId == null) {
                throw new RuntimeException("SectionId cannot be null!");
            }
            if (assignmentId == null) {
                throw new RuntimeException("AssignmentId cannot be null!");
            }

            // TODO ETL Tests - capture and return student assignments
            return new StudentAssignment[0];
        });

        //edPanel.getSectionAssignments(
        //        school.getId(),
        //        createdSection.getTerm().getSchoolYear().getId(),
        //        createdSection.getTerm().getId(),
        //        createdSection.getId());
        when(testApiClient.getSectionAssignments(anyLong(), anyLong(), anyLong(), anyLong())).thenAnswer(invocation -> {
            // TODO ETL Tests - capture and return section assignments
            return new Assignment[0];
        });
        
        // createSchool(School school)
        when(testApiClient.createSchool(any(School.class))).thenAnswer(invocation -> {
//            Object mock = invocation.getMock();
            Object[] args = invocation.getArguments();
            School school = (School) args[0];
            if (school == null) { throw new RuntimeException("School cannot be null!"); }

            if (school.getId() == null) {
                school.setId(schoolCounter++);
            }
            schools.put(school.getId(), school);
            return school;
        });
        
        when(testApiClient.getSchoolDays(anyLong())).thenAnswer(invocation -> {
            // TODO ETL Tests - capture and return school days
           return new SchoolDay[0]; 
        });
        
        // createSection
        when(testApiClient.createSection(anyLong(), anyLong(), anyLong(), any(Section.class))).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Long schoolId = (Long) args[0];
            Long schoolYearId = (Long) args[1];
            Long termId = (Long) args[2];
            Section section = (Section) args[3];
            if (schoolId == null) {
                throw new RuntimeException("SchoolId cannot be null!");
            }
            if (schoolYearId == null) {
                throw new RuntimeException("SchoolYearId cannot be null!");
            }
            if (termId == null) {
                throw new RuntimeException("Term cannot be null!");
            }
            if (section == null) {
                throw new RuntimeException("Section cannot be null!");
            }
            
            if (section.getId() == null) {
                section.setId(sectionCounter++);
            }
            sections.put(section.getId(), section);

            HashSet<Section> sections = schoolIdToSections.get(schoolId);
            if (sections == null) {
                sections = new HashSet<>();
            }
            sections.add(section);
            schoolIdToSections.put(schoolId, sections);
            return section;
        });

        // getSchoolYears(Long schoolId)
        when(testApiClient.getSchoolYears(anyLong())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Long schoolId = (Long) args[0];
            if (schoolId == null) { throw new RuntimeException("SchoolId cannot be null!"); }

            School school = schools.get(schoolId);
            List<SchoolYear> schoolYears = null;
            if (school != null) {
                schoolYears = school.getYears();
            }
            if (schoolYears == null) { schoolYears = new ArrayList<>(); }
            return schoolYears.toArray(new SchoolYear[schoolYears.size()]);
        });
        
        // createSchoolYear(Long schoolId, SchoolYear schoolYear);
        when(testApiClient.createSchoolYear(anyLong(), any(SchoolYear.class))).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Long schoolId = (Long) args[0];
            SchoolYear schoolYear = (SchoolYear) args[1];
            if (schoolId == null) { throw new RuntimeException("SchoolId cannot be null!"); }
            if (schoolYear == null) { throw new RuntimeException("SchoolYear cannot be null!"); }
            
            if (schoolYear.getId() == null) {
                schoolYear.setId(schoolYearCounter++);
            }
            schoolYears.put(schoolYear.getId(), schoolYear);
            return schoolYear;
        });

        // createAdministrator
        when(testApiClient.createAdministrator(any(Staff.class))).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Staff staffToCreate = (Staff) args[0];
            if (staffToCreate == null) {
                throw new RuntimeException("Staff cannot be null!");
            }
            if (staffToCreate.getId() == null) {
                staffToCreate.setId(userCounter++);
            }
            staff.put(staffToCreate.getId(), staffToCreate);
            return staffToCreate;
        });

        // createTeacher
        when(testApiClient.createTeacher(any(Staff.class))).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Staff staffToCreate = (Staff) args[0];
            if (staffToCreate == null) {
                throw new RuntimeException("Staff cannot be null!");
            }
            if (staffToCreate.getId() == null) {
                staffToCreate.setId(userCounter++);
            }
            staff.put(staffToCreate.getId(), staffToCreate);
            return staffToCreate;
        });

        // createStudent
        when(testApiClient.createStudent(any(Student.class))).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Student studentToCreate = (Student) args[0];
            if (studentToCreate == null) {
                throw new RuntimeException("Staff cannot be null!");
            }
            if (studentToCreate.getId() == null) {
                studentToCreate.setId(userCounter++);
            }
            students.put(studentToCreate.getId(), studentToCreate);
            return studentToCreate;
        });

        // createTerm(school.getId(), sourceTerm.getSchoolYear().getId(), sourceTerm);
        when(testApiClient.createTerm(anyLong(), anyLong(), any(Term.class))).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Long schoolId = (Long) args[0];
            Long schoolYearId = (Long) args[1];
            Term term = (Term) args[2];
            if (schoolId == null) {
                throw new RuntimeException("SchoolId cannot be null!");
            }
            if (schoolYearId == null) {
                throw new RuntimeException("SchoolYearId cannot be null!");
            }
            if (term == null) {
                throw new RuntimeException("Term cannot be null!");
            }

            if (term.getId() == null) {
                term.setId(termCounter++);
            }
            terms.put(term.getId(), term);
            return term;
        });
        
        // createCourse
        when(testApiClient.createCourse(anyLong(), any(Course.class))).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Long schoolId = (Long) args[0];
            Course course = (Course) args[1];
            if (schoolId == null) {
                throw new RuntimeException("SchoolId cannot be null!");
            }
            if (course == null) {
                throw new RuntimeException("Course cannot be null!");
            }
            if (course.getId() == null) {
                course.setId(courseCounter++);
            }
            courseIdToCourse.put(course.getId(), course);
  
            HashSet<Course> courses = schoolIdToCourses.get(schoolId);
            if (courses == null) { courses = new HashSet<>(); }
            courses.add(course);
            schoolIdToCourses.put(schoolId, courses);
            return course;
        });
        
        //createAssignment
        when(testApiClient.createSectionAssignment(anyLong(), anyLong(), anyLong(), anyLong(), any(Assignment.class))).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Long schoolId = (Long) args[0];
            Long schoolYearId = (Long) args[1];
            Long termId = (Long) args[2];
            Long sectionId = (Long) args[3];
            Assignment assignment = (Assignment) args[4];
            if (schoolId == null) {
                throw new RuntimeException("SchoolId cannot be null!");
            }
            if (schoolYearId == null) {
                throw new RuntimeException("SchoolYearId cannot be null!");
            }
            if (termId == null) {
                throw new RuntimeException("Term cannot be null!");
            }
            if (sectionId == null) {
                throw new RuntimeException("Section cannot be null!");
            }
            if (assignment == null) {
                throw new RuntimeException("Assignment cannot be null!");
            }
            
            if (assignment.getId() == null) {
                assignment.setId(sectionAssignmentCounter++);
            }
            sectionAssignments.put(assignment.getId(), assignment);
            return assignment;
        });
        
        return testApiClient;
    }
    
    private static URI getURI() {
        URI uri = null;
        try {
            uri = new URI("fakeURI");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return uri;
    }

//    @Override
    public void authenticate() { }

//    @Override
    protected CloseableHttpClient createClient() throws HttpClientException {

        final Answer<CloseableHttpResponse> httpResponseAnswer = invocation -> {

            Object[] args = invocation.getArguments();
            if (args == null) {
                throw new RuntimeException("problem!");
            }
            HttpUriRequest request = (HttpUriRequest)args[0];
            if (request == null || request.getURI() == null) {
                throw new RuntimeException("big problem!");
            }
            String uriString = request.getURI().toString();
//                System.out.println("SEE URI: " + uriString);

            String filename = null;

            HashMap<String, String> regexToResponseNames = new HashMap<>();

            // ALL LOWERCASE!
            final String GET_DISTRICT_REQUEST_REGEX = "/ws/v1/district(\\?.*)?";
            final String GET_ALL_SCHOOLS_REQUEST_REGEX = "/ws/v1/district/school\\?.*";
            final String STUDENT_REQUEST_REGEX = "/ws/v1/school/.*/student\\?.*";
            final String COURSE_REQUEST_REGEX = "/ws/v1/school/.*/course\\?.*";
            final String SECTION_REQUEST_REGEX = "/ws/v1/school/.*/section\\?.*";
            final String TERM_REQUEST_REGEX = "/ws/v1/school/.*/term\\?.*";
            final String STAFF_REQUEST_REGEX = "/ws/v1/school/.*/staff\\?.*";
            final String PGASSIGNMENTS_BY_SECTION_REQUEST_REGEX = "/ws/schema/table/pgassignments\\?.*"
                    + "pagesize=.*"
                    + "\\&projection=.*"
                    + "\\&q=sectionid==.*"
                    + "\\&page=.*";
            final String STORED_GRADES_BY_SECTION_REQUEST_REGEX = "/ws/schema/table/storedgrades\\?"
                    + "pagesize=.*"
                    + "\\&q=sectionid==.*"
                    + "\\&projection=.*"
                    + "\\&page=.*";

            regexToResponseNames.put(GET_DISTRICT_REQUEST_REGEX, "district");
            regexToResponseNames.put(GET_ALL_SCHOOLS_REQUEST_REGEX, "all_schools");
            regexToResponseNames.put(STUDENT_REQUEST_REGEX, "student");
            regexToResponseNames.put(COURSE_REQUEST_REGEX, "course");
            regexToResponseNames.put(SECTION_REQUEST_REGEX, "section");
            regexToResponseNames.put(TERM_REQUEST_REGEX, "term");
            regexToResponseNames.put(STAFF_REQUEST_REGEX, "staff");
            regexToResponseNames.put(PGASSIGNMENTS_BY_SECTION_REQUEST_REGEX, "pgassignments_by_section");
            regexToResponseNames.put(STORED_GRADES_BY_SECTION_REQUEST_REGEX, "storedgrades_by_section");


            if (uriString != null) {
                String lowerString = uriString.toLowerCase();
                for (String regex : regexToResponseNames.keySet()) {
                    if (lowerString.matches(regex)) {
                        String responseName = regexToResponseNames.get(regex);
                        filename = "mock-responses/" + responseName + "_response.json";
                    }
                }
            }
            if (filename == null) {
                // throw an exception because the real code will throw an NPE in this case anyway
                throw new RuntimeException("Unsupported URI String: " + uriString);
            }
            return buildMockHttpResponseWithAnswerFromFile(filename);
        };

        CloseableHttpClient mockedClient = Mockito.mock(CloseableHttpClient.class);
        try {
            when(mockedClient.execute(any(HttpUriRequest.class))).thenAnswer(httpResponseAnswer);
        } catch (IOException e) {
            throw new HttpClientException(e);
        }
        return mockedClient;
    }

    private CloseableHttpResponse buildMockHttpResponseWithAnswerFromFile(String filename) {
        CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
//        Mockito.mock(CloseableHttpResponse.class, withSettings().defaultAnswer())
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "FINE!"));

        final StringEntity stringEntity = buildStringEntity(getClasspathFileAsString(filename));
        Answer<StringEntity> stringEntityAnswer = invocation -> stringEntity;
        when(response.getEntity()).thenAnswer(stringEntityAnswer);
        return response;
    }

    private StringEntity buildStringEntity(String fileContents) {
        StringEntity stringEntity = null;
        if (fileContents != null) {
            try {
                stringEntity = new StringEntity(fileContents);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Unsupported Encoding trying to build string entity!", e);
            }
        } else {
            throw new RuntimeException("Loaded empty test file!");
        }
        return stringEntity;
    }

    private String getClasspathFileAsString(String filename) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
        String fileContents = null;
        try {
            fileContents = IOUtils.toString(is, (Charset) null);
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("arg can't read in sample file " + filename + "!", e);
        }
        return fileContents;
    }

}
