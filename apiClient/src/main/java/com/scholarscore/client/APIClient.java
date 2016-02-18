package com.scholarscore.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.Course;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.LoginRequest;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.grade.StudentSectionGrade;
import com.scholarscore.models.Term;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.attendance.SchoolDay;
import com.scholarscore.models.factory.AssignmentFactory;
import com.scholarscore.models.gpa.Gpa;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.User;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * TODO: Convert InternalBase to consume this API (perhaps?)
 *
 * Created by mattg on 7/3/15.
 */
public class APIClient extends BaseHttpClient implements IAPIClient {
    // warehouse is required because uri.resolve(path) erases the path
    private static final String BASE_API_ENDPOINT = "warehouse/api/v1";
    private static final String LOGIN_ENDPOINT = "/login";
    private static final String SCHOOL_ENDPOINT = "/schools";
    private static final String SCHOOL_YEAR_ENDPOINT = "/years";
    private static final String COURSE_ENDPOINT = "/schools/{0}/courses";
    private static final String TERM_ENDPOINT = "/terms";
    private static final String SECTION_ENDPOINT = "/sections";
    private static final String SECTION_ASSIGNMENT_ENDPOINT = "/assignments";
    private static final String STUDENT_ENDPOINT = "/students";
    private static final String SSIDS_ENDPOINT = "/sourcesystemids";
    private static final String STUDENT_ASSIGNMENT_ENDPOINT = "/studentassignments";
    private static final String STUDENT_SECTION_GRADE_ENDPOINT = "/grades";
    private static final String TEACHER_ENDPOINT = "/teachers";
    private static final String BEHAVIOR_ENDPOINT = "/behaviors";
    private static final String DAYS_ENDPOINT = "/days";
    private static final String ATTENDANCE_ENDPOINT = "/attendance";
    private static final String GPA_ENDPOINT = "/gpas";
    private static final String NOTIFICATION_ENDPOINT = "/notifications";
    private static final String EVALUATION_ENDPOINT = "/evaluations";

    // TODO: Create this end point
    private static final String ADMINISTRATOR_ENDPOINT = "/administrators";
    private static final String USERS_ENDPOINT = "/users";

    private final String password;
    private final String username;
    private Boolean hasAuthenticated = false;

    public APIClient(String username, String password, URI uri) {
        super(uri);
        this.username = username;
        this.password = password;
        try {
            authenticate();
        } catch (HttpClientException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void triggerNotificationEvaluation(Long schoolId) throws HttpClientException {
        try {
            post(null, BASE_API_ENDPOINT + NOTIFICATION_ENDPOINT + SCHOOL_ENDPOINT + "/" + schoolId + EVALUATION_ENDPOINT);
        } catch(IOException e) {
            throw new HttpClientException(e);
        }
    }

    @Override
    public School createSchool(School school) throws HttpClientException {
        EntityId id = create(school, SCHOOL_ENDPOINT);
        School response = new School(school);
        response.setId(id.getId());
        return response;
    }

    @Override
    public School getSchool(Long schoolId) throws HttpClientException {
        School response = get(School.class,
                BASE_API_ENDPOINT + SCHOOL_ENDPOINT + "/" + schoolId);
        return response;
    }

    @Override
    public School[] getSchools() throws HttpClientException {
        School[] response = get(School[].class, BASE_API_ENDPOINT + SCHOOL_ENDPOINT);
        return response;
    }

    @Override
    public School updateSchool(School school) throws IOException {
        School response = new School(school);
        patch(convertObjectToJsonBytes(school), BASE_API_ENDPOINT + SCHOOL_ENDPOINT);
        return school;
    }

    @Override
    public void deleteSchool(School school) throws HttpClientException {
        delete(BASE_API_ENDPOINT + SCHOOL_ENDPOINT + "/" + school.getId(), (String[]) null);
    }

    private void createVoidResponse(Object obj, String path) throws HttpClientException {
        String json = null;
        try {
            json = post(convertObjectToJsonBytes(obj), BASE_API_ENDPOINT + path);
        } catch (IOException e) {
            throw new HttpClientException(e);
        }
    }

    private List<Long> createListResponse(Object obj, String path) throws HttpClientException {
        String json = null;
        try {
            json = post(convertObjectToJsonBytes(obj), BASE_API_ENDPOINT + path);
        } catch (IOException e) {
            throw new HttpClientException(e);
        }
        try {
            return MAPPER.readValue(json, new TypeReference<List<Long>>() {});
        } catch (IOException e) {
            throw new HttpClientException(e);
        }
    }

    private EntityId create(Object obj, String path) throws HttpClientException {
        String jsonCreateResponse = null;
        try {
            jsonCreateResponse = post(convertObjectToJsonBytes(obj), BASE_API_ENDPOINT + path);
        } catch (IOException e) {
            throw new HttpClientException(e);
        }
        try {
            return MAPPER.readValue(jsonCreateResponse, EntityId.class);
        } catch (IOException e) {
            throw new HttpClientException(e);
        }
    }
    
    private EntityId update(Object obj, String path) throws HttpClientException {
        String jsonCreateResponse = null;
        try {
            jsonCreateResponse = patch(convertObjectToJsonBytes(obj), BASE_API_ENDPOINT + path);
        } catch (IOException e) {
            throw new HttpClientException(e);
        }
        try {
            return MAPPER.readValue(jsonCreateResponse, EntityId.class);
        } catch (IOException e) {
            throw new HttpClientException(e);
        }
    }

    @Override
    public Student createStudent(Student student) throws HttpClientException {
        EntityId id = create(student, STUDENT_ENDPOINT);
        Student response = new Student(student);
        response.setId(id.getId());
        return response;
    }

    @Override
    public Collection<Student> getStudents(Long schoolId) throws HttpClientException {
        String path = BASE_API_ENDPOINT + STUDENT_ENDPOINT;
        if(null != schoolId) {
            path += "?schoolId=" + schoolId;
        }
        Student[] students = get(Student[].class, path);
        return Arrays.asList(students);
    }

    @Override
    public Student getStudent(Long ssid) throws HttpClientException {
        Student s = get(
                Student.class,
                BASE_API_ENDPOINT + STUDENT_ENDPOINT + SSIDS_ENDPOINT + "/" + ssid);
        return s;
    }

    public void updateAdvisors(Long schoolId) throws IOException{
        post(null, BASE_API_ENDPOINT + SCHOOL_ENDPOINT + "/" + schoolId +
                STUDENT_ENDPOINT + "/advisor");
    }

    @Override
    public Student updateStudent(Long studentId, Student student) throws HttpClientException {
        if (studentId == null || studentId < 0) { return null; }
        EntityId id = update(student, STUDENT_ENDPOINT + "/" + studentId);
        Student response = new Student(student);
        response.setId(id.getId());
        return response;
    }

    public Staff createTeacher(Staff teacher) throws HttpClientException {
        EntityId id = create(teacher, TEACHER_ENDPOINT);
        Staff response = new Staff(teacher);
        response.setId(id.getId());
        return response;
    }

    @Override
    public Collection<Staff> getTeachers() throws HttpClientException {
        Staff[] teachers = get(Staff[].class, BASE_API_ENDPOINT + TEACHER_ENDPOINT);
        return Arrays.asList(teachers);
    }

    @Override
    public Collection<Staff> getAdministrators() throws HttpClientException {
        Staff[] admins = get(Staff[].class, BASE_API_ENDPOINT + ADMINISTRATOR_ENDPOINT);
        return Arrays.asList(admins);
    }

    @Override
    public Collection<Behavior> getBehaviors(Long studentId) throws HttpClientException {
        Behavior[] behaviors = get(Behavior[].class, BASE_API_ENDPOINT
                + STUDENT_ENDPOINT + "/" + studentId + BEHAVIOR_ENDPOINT);
        return Arrays.asList(behaviors);
    }

    @Override
    public Behavior createBehavior(Long studentId, Behavior behavior) throws HttpClientException {
        EntityId id = create(behavior, STUDENT_ENDPOINT + "/" + studentId + BEHAVIOR_ENDPOINT);
        Behavior response = new Behavior(behavior);
        response.setId(id.getId());
        return response;
    }

    @Override
    public Behavior updateBehavior(Long studentId, Long behaviorId, Behavior behavior) throws HttpClientException {
        if (studentId == null || studentId < 0) { return null; }
        if (behaviorId == null || behaviorId < 0) { return null; } 
        EntityId id = update(behavior, STUDENT_ENDPOINT + "/" + studentId + BEHAVIOR_ENDPOINT + "/" + behaviorId);
        Behavior response = new Behavior(behavior);
        response.setId(id.getId());
        return response;
    }

    public Staff createAdministrator(Staff administrator) throws HttpClientException {
        EntityId id = create(administrator, ADMINISTRATOR_ENDPOINT);
        Staff response = new Staff(administrator);
        response.setId(id.getId());
        return response;
    }

    @Override
    public User[] getUsers(Long schoolId) throws HttpClientException {
        User[] response = get(User[].class, BASE_API_ENDPOINT + USERS_ENDPOINT + "?enabled=&schoolId=" + schoolId);
        return response;
    }

    @Override
    public User updateUser(User user) throws IOException {
        patch(convertObjectToJsonBytes(user), BASE_API_ENDPOINT + USERS_ENDPOINT + "/" + user.getId());
        return user;
    }

    @Override
    public User replaceUser(User user) throws IOException {
        put(convertObjectToJsonBytes(user), BASE_API_ENDPOINT + USERS_ENDPOINT + "/" + user.getId());
        return user;
    }

    @Override
    public Course createCourse(Long schoolId, Course course) throws HttpClientException {
        Course response = new Course(course);
        EntityId id = create(course, getPath(COURSE_ENDPOINT, schoolId.toString()));
        response.setId(id.getId());
        return response;
    }

    @Override
    public void deleteCourse(Long schoolId, Course course) throws HttpClientException {
        String[] params = { schoolId.toString(), course.getId().toString() };
        delete(BASE_API_ENDPOINT + COURSE_ENDPOINT + "/{1}", params);
    }

    @Override
    public Course replaceCourse(Long schoolId, Course course) throws IOException {
        String[] params = { schoolId.toString(), course.getId().toString() };
        put(convertObjectToJsonBytes(course), getPath(BASE_API_ENDPOINT + COURSE_ENDPOINT + "/{1}", params));
        return course;
    }

    @Override
    public Course[] getCourses(Long schoolId) throws HttpClientException {
        String[] params = { schoolId.toString() };
        Course[] courses = get(Course[].class, getPath(BASE_API_ENDPOINT + COURSE_ENDPOINT, params));
        return courses;
    }


    @Override
    public SchoolYear createSchoolYear(Long schoolId, SchoolYear year) throws HttpClientException {
        SchoolYear response = new SchoolYear(year);
        EntityId id = create(year, SCHOOL_ENDPOINT + "/" + schoolId + SCHOOL_YEAR_ENDPOINT);
        response.setId(id.getId());
        return response;
    }

    @Override
    public void deleteSchoolYear(Long schoolId, SchoolYear year) throws HttpClientException {
        delete(BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                SCHOOL_YEAR_ENDPOINT + "/" + year.getId(), (String[]) null);
    }

    @Override
    public SchoolYear updateSchoolYear(Long schoolId, SchoolYear year) {
        SchoolYear y = new SchoolYear(year);
        try {
            patch(convertObjectToJsonBytes(year), BASE_API_ENDPOINT +
                    SCHOOL_ENDPOINT + "/" + schoolId +
                    SCHOOL_YEAR_ENDPOINT + "/" + year.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return y;
    }

    @Override
    public SchoolYear[] getSchoolYears(Long schoolId) throws HttpClientException {
        SchoolYear[] years = get(SchoolYear[].class, BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                SCHOOL_YEAR_ENDPOINT);
        return years;
    }

    @Override
    public Term createTerm(Long schoolId, Long schoolYearId, Term term) throws HttpClientException {
        Term response = new Term(term);
        EntityId id = create(term, SCHOOL_ENDPOINT + "/" + schoolId + SCHOOL_YEAR_ENDPOINT + "/" + schoolYearId + TERM_ENDPOINT);
        response.setId(id.getId());
        return response;
    }

    @Override
    public void deleteTerm(Long schoolId, Long schoolYearId, Term term) throws HttpClientException {
        delete(BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                SCHOOL_YEAR_ENDPOINT + "/" + schoolYearId +
                TERM_ENDPOINT + "/" + term.getId(), (String[]) null);
    }

    @Override
    public Term updateTerm(Long schoolId, Long schoolYearId, Term term) throws IOException {
        Term t = new Term(term);
        patch(convertObjectToJsonBytes(term), BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                SCHOOL_YEAR_ENDPOINT + "/" + schoolYearId +
                TERM_ENDPOINT + "/" + term.getId());
        return t;
    }


    @Override
    public Term[] getTerms(Long schoolId, Long schoolYearId) throws HttpClientException {
        Term[] terms = get(Term[].class, BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                SCHOOL_YEAR_ENDPOINT + "/" + schoolYearId +
                TERM_ENDPOINT);
        return terms;
    }

    @Override
    public SchoolDay createSchoolDays(Long schoolId, SchoolDay day) throws HttpClientException {
        SchoolDay response = new SchoolDay(day);
        EntityId id = create(day, SCHOOL_ENDPOINT + "/" + schoolId + DAYS_ENDPOINT);
        response.setId(id.getId());
        return response;
    }

    @Override
    public List<Long> createSchoolDays(Long schoolId, List<SchoolDay> days) throws HttpClientException {
        return createListResponse(days, SCHOOL_ENDPOINT + "/" + schoolId + DAYS_ENDPOINT + "/bulk");
    }

    @Override
    public void deleteSchoolDay(Long schoolId, SchoolDay day) throws HttpClientException {
        delete(BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                DAYS_ENDPOINT + "/" + day.getId(), (String[]) null);
    }

    @Override
    public SchoolDay updateSchoolDay(Long schoolId, SchoolDay day) throws IOException {
        SchoolDay t = new SchoolDay(day);
        patch(convertObjectToJsonBytes(day), BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                DAYS_ENDPOINT + "/" + day.getId());
        return t;
    }

    @Override
    public SchoolDay[] getSchoolDays(Long schoolId) throws HttpClientException {
        SchoolDay[] days = get(SchoolDay[].class, BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                DAYS_ENDPOINT);
        return days;
    }

    @Override
    public Attendance createAttendance(Long schoolId, Long studentId, Attendance attend) throws HttpClientException {
        EntityId id = create(attend,
                SCHOOL_ENDPOINT + "/" + schoolId +
                STUDENT_ENDPOINT + "/" + studentId +
                ATTENDANCE_ENDPOINT);
        attend.setId(id.getId());
        return attend;
    }

    @Override
    public void createAttendance(Long schoolId, Long studentId, List<Attendance> attends) throws HttpClientException {
        createVoidResponse(attends,
                SCHOOL_ENDPOINT + "/" + schoolId +
                STUDENT_ENDPOINT + "/" + studentId +
                ATTENDANCE_ENDPOINT + "/bulk");
    }

    @Override
    public void deleteAttendance(Long schoolId, Long studentId, Attendance attend) throws HttpClientException {
        delete(BASE_API_ENDPOINT + SCHOOL_ENDPOINT + "/" + schoolId +
                STUDENT_ENDPOINT + "/" + studentId +
                ATTENDANCE_ENDPOINT, (String[]) null);
    }

    @Override
    public Attendance updateAttendance(Long schoolId, Long studentId, Attendance attend) throws IOException {
        put(convertObjectToJsonBytes(attend), BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                STUDENT_ENDPOINT + "/" + studentId +
                ATTENDANCE_ENDPOINT + "/" + attend.getId());
        return attend;
    }

    @Override
    public Attendance[] getAttendance(Long schoolId, Long studentId) throws HttpClientException {
        Attendance[] attendances = get(Attendance[].class, BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                STUDENT_ENDPOINT + "/" + studentId +
                ATTENDANCE_ENDPOINT);
        return attendances;
    }

    @Override
    public Section createSection(
            Long schoolId, 
            Long schoolYearId, 
            Long termId,
            Section section) throws HttpClientException {
        Section response = new Section(section);
        EntityId id = create(section, 
                        SCHOOL_ENDPOINT + "/" + schoolId +
                        SCHOOL_YEAR_ENDPOINT + "/" + schoolYearId +
                        TERM_ENDPOINT + "/" + termId + SECTION_ENDPOINT);
        response.setId(id.getId());
        return response;
    }

    @Override
    public Section[] getSections(Long schoolId) throws HttpClientException {
        Section[] sections = get(Section[].class, BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                SECTION_ENDPOINT);
        return sections;
    }

    @Override
    public Section replaceSection(Long schoolId, Long schoolYearId, Long termId, Section section) throws IOException {
        Section t = new Section(section);
        put(convertObjectToJsonBytes(section), BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                SCHOOL_YEAR_ENDPOINT + "/" + schoolYearId +
                TERM_ENDPOINT + "/" + termId +
                SECTION_ENDPOINT + "/" + section.getId());
        return t;
    }

    @Override
    public void deleteSection(Long schoolId, Long schoolYearId, Long termId, Section section) throws HttpClientException {
        delete(BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                SCHOOL_YEAR_ENDPOINT + "/" + schoolYearId +
                TERM_ENDPOINT + "/" + termId +
                SECTION_ENDPOINT + "/" + section.getId(), (String[]) null);
    }

    @Override
    public StudentSectionGrade createStudentSectionGrade(
            Long schoolId,
            Long yearId, 
            Long termId, 
            Long sectionId, 
            Long studentId,
            StudentSectionGrade ssg) throws HttpClientException {
        StudentSectionGrade response = new StudentSectionGrade(ssg);
        EntityId id = create(ssg, 
                SCHOOL_ENDPOINT + "/" + schoolId + SCHOOL_YEAR_ENDPOINT + "/" + yearId + 
                TERM_ENDPOINT + "/" + termId + SECTION_ENDPOINT + "/" + sectionId + 
                STUDENT_SECTION_GRADE_ENDPOINT + STUDENT_ENDPOINT + "/" + studentId);
        response.setId(id.getId());
        return response;
    }

    @Override
    public void createStudentSectionGrades(Long schoolId,
                                           Long yearId,
                                           Long termId,
                                           Long sectionId,
                                           List<StudentSectionGrade> ssgs) throws HttpClientException {
        createVoidResponse(ssgs,
                SCHOOL_ENDPOINT + "/" + schoolId +
                        SCHOOL_YEAR_ENDPOINT + "/" + yearId +
                        TERM_ENDPOINT + "/" + termId +
                        SECTION_ENDPOINT + "/" + sectionId +
                        STUDENT_SECTION_GRADE_ENDPOINT);
    }

    @Override
    public StudentSectionGrade replaceStudentSectionGrade(Long schoolId,
                                                          Long yearId,
                                                          Long termId,
                                                          Long sectionId,
                                                          Long studentId, StudentSectionGrade ssg) throws IOException {
        StudentSectionGrade studentSectionGrade = new StudentSectionGrade(ssg);
        put(convertObjectToJsonBytes(ssg), BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                SCHOOL_YEAR_ENDPOINT + "/" + yearId +
                TERM_ENDPOINT + "/" + termId +
                SECTION_ENDPOINT + "/" + sectionId +
                STUDENT_SECTION_GRADE_ENDPOINT + STUDENT_ENDPOINT + "/" + studentId);
        return studentSectionGrade;
    }

    @Override
    public void deleteStudentSectionGrade(Long schoolId,
                                                         Long yearId,
                                                         Long termId,
                                                         Long sectionId,
                                                         Long studentId,
                                                         StudentSectionGrade ssg) throws HttpClientException {
        delete(BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                SCHOOL_YEAR_ENDPOINT + "/" + yearId +
                TERM_ENDPOINT + "/" + termId +
                SECTION_ENDPOINT + "/" + sectionId +
                STUDENT_SECTION_GRADE_ENDPOINT + STUDENT_ENDPOINT + "/" + studentId, (String[]) null);
    }

    @Override
    public StudentSectionGrade[] getStudentSectionGrades(Long schoolId, Long yearId, Long termId, Long sectionId)
            throws HttpClientException {
        StudentSectionGrade[] ssgs = get(
                StudentSectionGrade[].class,
                BASE_API_ENDPOINT +
                    SCHOOL_ENDPOINT + "/" + schoolId +
                    SCHOOL_YEAR_ENDPOINT + "/" + yearId +
                    TERM_ENDPOINT + "/" + termId +
                    SECTION_ENDPOINT + "/" + sectionId +
                    STUDENT_SECTION_GRADE_ENDPOINT);
        return ssgs;
    }

    @Override
    public Assignment createSectionAssignment(Long schoolId, Long yearId, Long termId, Long sectionId, Assignment a) throws HttpClientException {
        Assignment response = AssignmentFactory.cloneAssignment(a);
        EntityId id = create(a,
                SCHOOL_ENDPOINT + "/" + schoolId + SCHOOL_YEAR_ENDPOINT + "/" + yearId +
                TERM_ENDPOINT + "/" + termId + SECTION_ENDPOINT + "/" + sectionId +
                SECTION_ASSIGNMENT_ENDPOINT);
        response.setId(id.getId());
        return response;
    }

    @Override
    public Assignment replaceSectionAssignment(Long schoolId, Long yearId, Long termId, Long sectionId, Assignment ssg) throws IOException {
        put(convertObjectToJsonBytes(ssg), BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                SCHOOL_YEAR_ENDPOINT + "/" + yearId +
                TERM_ENDPOINT + "/" + termId +
                SECTION_ENDPOINT + "/" + sectionId +
                SECTION_ASSIGNMENT_ENDPOINT + "/" + ssg.getId());
        return ssg;
    }

    @Override
    public void deleteSectionAssignment(Long schoolId, Long yearId, Long termId, Long sectionId, Assignment ssg)
            throws HttpClientException {
        delete(BASE_API_ENDPOINT +
            SCHOOL_ENDPOINT + "/" + schoolId +
            SCHOOL_YEAR_ENDPOINT + "/" + yearId +
            TERM_ENDPOINT + "/" + termId +
            SECTION_ENDPOINT + "/" + sectionId +
            SECTION_ASSIGNMENT_ENDPOINT + "/" + ssg.getId(), (String[]) null);
    }

    @Override
    public Assignment[] getSectionAssignments(Long schoolId, Long yearId, Long termId, Long sectionId)
            throws HttpClientException {
        Assignment[] assigments = get(
            Assignment[].class,
            BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                SCHOOL_YEAR_ENDPOINT + "/" + yearId +
                TERM_ENDPOINT + "/" + termId +
                SECTION_ENDPOINT + "/" + sectionId +
                SECTION_ASSIGNMENT_ENDPOINT);
        return assigments;
    }

    @Override
    public StudentAssignment createStudentAssignment(Long schoolId,
                                                     Long yearId,
                                                     Long termId,
                                                     Long sectionId,
                                                     Long assignmentId,
                                                     StudentAssignment studentAssignment) throws HttpClientException {
        StudentAssignment response = new StudentAssignment(studentAssignment);
        EntityId id = create(studentAssignment,
                SCHOOL_ENDPOINT + "/" + schoolId + SCHOOL_YEAR_ENDPOINT + "/" + yearId +
                TERM_ENDPOINT + "/" + termId + SECTION_ENDPOINT + "/" + sectionId +
                SECTION_ASSIGNMENT_ENDPOINT + "/" + assignmentId + STUDENT_ASSIGNMENT_ENDPOINT);
        response.setId(id.getId());
        return response;
    }

    @Override
    public List<Long> createStudentAssignments(Long schoolId,
                                         Long yearId,
                                         Long termId,
                                         Long sectionId,
                                         Long assignmentId,
                                         List<StudentAssignment> studentAssignments) throws HttpClientException {
        return createListResponse(studentAssignments,
                SCHOOL_ENDPOINT + "/" + schoolId + SCHOOL_YEAR_ENDPOINT + "/" + yearId +
                TERM_ENDPOINT + "/" + termId + SECTION_ENDPOINT + "/" + sectionId +
                SECTION_ASSIGNMENT_ENDPOINT + "/" + assignmentId + STUDENT_ASSIGNMENT_ENDPOINT + "/bulk");
    }

    @Override
    public StudentAssignment[] getStudentAssignments(Long schoolId,
                                                     Long yearId,
                                                     Long termId,
                                                     Long sectionId,
                                                     Long assignmentId) throws HttpClientException {
        StudentAssignment[] ssgs = get(
                StudentAssignment[].class,
                BASE_API_ENDPOINT +
                        SCHOOL_ENDPOINT + "/" + schoolId +
                        SCHOOL_YEAR_ENDPOINT + "/" + yearId +
                        TERM_ENDPOINT + "/" + termId +
                        SECTION_ENDPOINT + "/" + sectionId +
                        SECTION_ASSIGNMENT_ENDPOINT + "/" + assignmentId +
                        STUDENT_ASSIGNMENT_ENDPOINT);
        return ssgs;
    }

    @Override
    public void deleteStudentAssignment(Long schoolId,
                                        Long yearId,
                                        Long termId,
                                        Long sectionId,
                                        Long assignmentId,
                                        StudentAssignment studentAssignment) throws HttpClientException {
        delete(BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                SCHOOL_YEAR_ENDPOINT + "/" + yearId +
                TERM_ENDPOINT + "/" + termId +
                SECTION_ENDPOINT + "/" + sectionId +
                SECTION_ASSIGNMENT_ENDPOINT + "/" + assignmentId +
                STUDENT_ASSIGNMENT_ENDPOINT + "/" + studentAssignment.getId(), (String[]) null);
    }

    @Override
    public void replaceStudentAssignment(Long schoolId,
                                         Long yearId,
                                         Long termId,
                                         Long sectionId,
                                         Long assignmentId,
                                         StudentAssignment studentAssignment) throws IOException {
        put(convertObjectToJsonBytes(studentAssignment), BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                SCHOOL_YEAR_ENDPOINT + "/" + yearId +
                TERM_ENDPOINT + "/" + termId +
                SECTION_ENDPOINT + "/" + sectionId +
                SECTION_ASSIGNMENT_ENDPOINT + "/" + assignmentId +
                STUDENT_ASSIGNMENT_ENDPOINT + "/" + studentAssignment.getId());
    }

    @Override
    public Gpa createGpa(Long studentId, Gpa gpa) throws HttpClientException {
        EntityId id = create(gpa, GPA_ENDPOINT + STUDENT_ENDPOINT + "/" + studentId);
        gpa.setId(id.id);
        return gpa;
    }

    @Override
    public void updateGpa(Long studentId, Gpa gpa) throws IOException {
        put(convertObjectToJsonBytes(gpa),
                BASE_API_ENDPOINT + GPA_ENDPOINT + "/" + gpa.getId() + STUDENT_ENDPOINT + "/" + studentId);
    }

    @Override
    public Gpa[] getGpas() throws HttpClientException {
        Gpa[] gpas = get(
                Gpa[].class,
                BASE_API_ENDPOINT + GPA_ENDPOINT);
        return gpas;
    }

    /**
     * A method to authenticate a user and store the returned auth cookie for subsequent requests.
     * Called by all integration test classes that are testing protected endpoints.
     */
    @Override
    protected synchronized void authenticate() throws HttpClientException {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsername(username);
        loginReq.setPassword(password);

        try {
            HttpPost post = new HttpPost();
            post.setEntity(new ByteArrayEntity(convertObjectToJsonBytes(loginReq)));
            post.setHeader(HEADER_CONTENT_TYPE_JSON);
            setupCommonHeaders(post);
            post.setURI(uri.resolve(BASE_API_ENDPOINT + LOGIN_ENDPOINT));
            HttpResponse response = httpclient.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                hasAuthenticated = true;
            }
        }
        catch (IOException e) {
            throw new HttpClientException(e);
        }
    }

    @Override
    protected synchronized Boolean isAuthenticated() {
        return hasAuthenticated;
    }

    /**
     * Description:
     * Private helper method used to convert test object to JSON
     *
     * Expected Result:
     * JSON string representation of supplied object
     */
    private byte[] convertObjectToJsonBytes(Object object) throws HttpClientException {

        if (null == object) {
            return new byte[0];
        }

        byte[] out = null;
        try {
            out = MAPPER.writeValueAsBytes(object);
            ////LOGGER.sys().info("JSON: " + new String(out, CHARSET_UTF8_NAME));
        } catch (Exception e) {
            throw new HttpClientException(e);
        }
        return out;
    }


}
