package com.scholarscore.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.Course;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.LoginRequest;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.StudentSectionGrade;
import com.scholarscore.models.Term;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.factory.AssignmentFactory;
import com.scholarscore.models.user.Administrator;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.models.user.User;
import com.scholarscore.models.user.UserType;
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
    private static final String STUDENT_ASSIGNMENT_ENDPOINT = "/studentassignments";
    private static final String STUDENT_SECTION_GRADE_ENDPOINT = "/grades";
    private static final String TEACHER_ENDPOINT = "/teachers";
    private static final String BEHAVIOR_ENDPOINT = "/behaviors";

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
        authenticate();
    }

    @Override
    public School createSchool(School school) {
        EntityId id = create(school, SCHOOL_ENDPOINT);
        School response = new School(school);
        response.setId(id.getId());
        return response;
    }

    @Override
    public School getSchool(Long schoolId) {
        School response = get(School.class,
                BASE_API_ENDPOINT + SCHOOL_ENDPOINT + "/" + schoolId);
        return response;
    }

    @Override
    public School[] getSchools() {
        School[] response = get(School[].class, BASE_API_ENDPOINT + SCHOOL_ENDPOINT);
        return response;
    }

    @Override
    public School updateSchool(School school) {
        School response = new School(school);
        try {
            patch(convertObjectToJsonBytes(school), BASE_API_ENDPOINT + SCHOOL_ENDPOINT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return school;
    }

    @Override
    public void deleteSchool(School school) {
        delete(BASE_API_ENDPOINT + SCHOOL_ENDPOINT + "/" + school.getId(), null);
    }

    private void createVoidResponse(Object obj, String path) {
        String json = null;
        try {
            json = post(convertObjectToJsonBytes(obj), BASE_API_ENDPOINT + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private EntityId create(Object obj, String path) {
        String jsonCreateResponse = null;
        try {
            jsonCreateResponse = post(convertObjectToJsonBytes(obj), BASE_API_ENDPOINT + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return mapper.readValue(jsonCreateResponse, EntityId.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private EntityId update(Object obj, String path) {
        String jsonCreateResponse = null;
        try {
            jsonCreateResponse = patch(convertObjectToJsonBytes(obj), BASE_API_ENDPOINT + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return mapper.readValue(jsonCreateResponse, EntityId.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Student createStudent(Student student) {
        EntityId id = create(student, STUDENT_ENDPOINT);
        Student response = new Student(student);
        response.setId(id.getId());
        return response;
    }

    @Override
    public Collection<Student> getStudents(Long schoolId) {
        String path = BASE_API_ENDPOINT + STUDENT_ENDPOINT;
        if(null != schoolId) {
            path += "?schoolId=" + schoolId;
        }
        Student[] students = get(Student[].class, path);
        return Arrays.asList(students);
    }
    
    @Override
    public Student updateStudent(Long studentId, Student student) {
        if (studentId == null || studentId < 0) { return null; }
        EntityId id = update(student, STUDENT_ENDPOINT + "/" + studentId);
        Student response = new Student(student);
        response.setId(id.getId());
        return response;
    }

    public Teacher createTeacher(Teacher teacher) {
        EntityId id = create(teacher, TEACHER_ENDPOINT);
        Teacher response = new Teacher(teacher);
        response.setId(id.getId());
        return response;
    }

    @Override
    public Collection<Teacher> getTeachers() {
        Teacher[] teachers = get(Teacher[].class, BASE_API_ENDPOINT + TEACHER_ENDPOINT);
        return Arrays.asList(teachers);
    }

    @Override
    public Collection<Administrator> getAdministrators() {
        Administrator[] admins = get(Administrator[].class, BASE_API_ENDPOINT + ADMINISTRATOR_ENDPOINT);
        return Arrays.asList(admins);
    }

    @Override
    public Collection<Behavior> getBehaviors(Long studentId) {
        Behavior[] behaviors = get(Behavior[].class, BASE_API_ENDPOINT
                + STUDENT_ENDPOINT + "/" + studentId + BEHAVIOR_ENDPOINT);
        return Arrays.asList(behaviors);
    }

    @Override
    public Behavior createBehavior(Long studentId, Behavior behavior) {
        EntityId id = create(behavior, STUDENT_ENDPOINT + "/" + studentId + BEHAVIOR_ENDPOINT);
        Behavior response = new Behavior(behavior);
        response.setId(id.getId());
        return response;
    }

    @Override
    public Behavior updateBehavior(Long studentId, Long behaviorId, Behavior behavior) {
        if (studentId == null || studentId < 0) { return null; }
        if (behaviorId == null || behaviorId < 0) { return null; } 
        EntityId id = update(behavior, STUDENT_ENDPOINT + "/" + studentId + BEHAVIOR_ENDPOINT + "/" + behaviorId);
        Behavior response = new Behavior(behavior);
        response.setId(id.getId());
        return response;
    }

    public Administrator createAdministrator(Administrator administrator) {
        EntityId id = create(administrator, ADMINISTRATOR_ENDPOINT);
        Administrator response = new Administrator(administrator);
        response.setId(id.getId());
        return response;
    }

    @Override
    public User createUser(User usr) {
        EntityId id = create(usr, USERS_ENDPOINT);
        User response = UserType.clone(usr);
        response.setId(id.getId());
        return response;
    }

    @Override
    public User[] getUsers(Long schoolId) {
        User[] response = get(User[].class, BASE_API_ENDPOINT + USERS_ENDPOINT + "?enabled=&schoolId=" + schoolId);
        return response;
    }

    @Override
    public User updateUser(User user) {
        try {
            patch(convertObjectToJsonBytes(user), BASE_API_ENDPOINT + USERS_ENDPOINT + "/" + user.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public User replaceUser(User user) {
        try {
            put(convertObjectToJsonBytes(user), BASE_API_ENDPOINT + USERS_ENDPOINT + "/" + user.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public Course createCourse(Long schoolId, Course course) {
        Course response = new Course(course);
        EntityId id = create(course, getPath(COURSE_ENDPOINT, schoolId.toString()));
        response.setId(id.getId());
        return response;
    }

    @Override
    public void deleteCourse(Long schoolId, Course course) {
        String[] params = { schoolId.toString(), course.getId().toString() };
        delete(BASE_API_ENDPOINT + COURSE_ENDPOINT + "/{1}", params);
    }

    @Override
    public Course replaceCourse(Long schoolId, Course course) {
        String[] params = { schoolId.toString(), course.getId().toString() };
        try {
            put(convertObjectToJsonBytes(course), getPath(BASE_API_ENDPOINT + COURSE_ENDPOINT + "/{1}", params));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return course;
    }

    @Override
    public Course[] getCourses(Long schoolId) {
        String[] params = { schoolId.toString() };
        Course[] courses = get(Course[].class, getPath(BASE_API_ENDPOINT + COURSE_ENDPOINT, params));
        return courses;
    }


    @Override
    public SchoolYear createSchoolYear(Long schoolId, SchoolYear year) {
        SchoolYear response = new SchoolYear(year);
        EntityId id = create(year, SCHOOL_ENDPOINT + "/" + schoolId + SCHOOL_YEAR_ENDPOINT);
        response.setId(id.getId());
        return response;
    }

    @Override
    public void deleteSchoolYear(Long schoolId, SchoolYear year) {
        delete(BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                SCHOOL_YEAR_ENDPOINT + "/" + year.getId(), null);
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
    public SchoolYear[] getSchoolYears(Long schoolId) {
        SchoolYear[] years = get(SchoolYear[].class, BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                SCHOOL_YEAR_ENDPOINT);
        return years;
    }

    @Override
    public Term createTerm(Long schoolId, Long schoolYearId, Term term) {
        Term response = new Term(term);
        EntityId id = create(term, SCHOOL_ENDPOINT + "/" + schoolId + SCHOOL_YEAR_ENDPOINT + "/" + schoolYearId + TERM_ENDPOINT);
        response.setId(id.getId());
        return response;
    }

    @Override
    public void deleteTerm(Long schoolId, Long schoolYearId, Term term) {
        delete(BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                SCHOOL_YEAR_ENDPOINT + "/" + schoolYearId +
                TERM_ENDPOINT + "/" + term.getId(), null);
    }

    @Override
    public Term updateTerm(Long schoolId, Long schoolYearId, Term term) {
        Term t = new Term(term);
        try {
            patch(convertObjectToJsonBytes(term), BASE_API_ENDPOINT +
                    SCHOOL_ENDPOINT + "/" + schoolId +
                    SCHOOL_YEAR_ENDPOINT + "/" + schoolYearId +
                    TERM_ENDPOINT + "/" + term.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }


    @Override
    public Term[] getTerms(Long schoolId, Long schoolYearId) {
        Term[] terms = get(Term[].class, BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                SCHOOL_YEAR_ENDPOINT + "/" + schoolYearId +
                TERM_ENDPOINT);
        return terms;
    }
    
    @Override
    public Section createSection(
            Long schoolId, 
            Long schoolYearId, 
            Long termId,
            Section section) {
        Section response = new Section(section);
        EntityId id = create(section, 
                        SCHOOL_ENDPOINT + "/" + schoolId +
                        SCHOOL_YEAR_ENDPOINT + "/" + schoolYearId +
                        TERM_ENDPOINT + "/" + termId + SECTION_ENDPOINT);
        response.setId(id.getId());
        return response;
    }

    @Override
    public Section[] getSections(Long schoolId) {
        Section[] sections = get(Section[].class, BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                SECTION_ENDPOINT);
        return sections;
    }

    @Override
    public Section replaceSection(Long schoolId, Long schoolYearId, Long termId, Section section) {
        Section t = new Section(section);
        try {
            put(convertObjectToJsonBytes(section), BASE_API_ENDPOINT +
                    SCHOOL_ENDPOINT + "/" + schoolId +
                    SCHOOL_YEAR_ENDPOINT + "/" + schoolYearId +
                    TERM_ENDPOINT + "/" + termId +
                    SECTION_ENDPOINT + "/" + section.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }

    @Override
    public void deleteSection(Long schoolId, Long schoolYearId, Long termId, Section section) {
        delete(BASE_API_ENDPOINT +
                SCHOOL_ENDPOINT + "/" + schoolId +
                SCHOOL_YEAR_ENDPOINT + "/" + schoolYearId +
                TERM_ENDPOINT + "/" + termId +
                SECTION_ENDPOINT + "/" + section.getId(), null);
    }

    @Override
    public StudentSectionGrade createStudentSectionGrade(
            Long schoolId,
            Long yearId, 
            Long termId, 
            Long sectionId, 
            Long studentId,
            StudentSectionGrade ssg) {
        StudentSectionGrade response = new StudentSectionGrade(ssg);
        EntityId id = create(ssg, 
                SCHOOL_ENDPOINT + "/" + schoolId + SCHOOL_YEAR_ENDPOINT + "/" + yearId + 
                TERM_ENDPOINT + "/" + termId + SECTION_ENDPOINT + "/" + sectionId + 
                STUDENT_SECTION_GRADE_ENDPOINT + STUDENT_ENDPOINT + "/" + studentId);
        response.setId(id.getId());
        return response;
    }

    @Override
    public Assignment createSectionAssignment(Long schoolId, Long yearId, Long termId, Long sectionId, Assignment a) {
        Assignment response = AssignmentFactory.cloneAssignment(a);
        EntityId id = create(a,
                SCHOOL_ENDPOINT + "/" + schoolId + SCHOOL_YEAR_ENDPOINT + "/" + yearId +
                TERM_ENDPOINT + "/" + termId + SECTION_ENDPOINT + "/" + sectionId +
                SECTION_ASSIGNMENT_ENDPOINT);
        response.setId(id.getId());
        return response;
    }

    @Override
    public StudentAssignment createStudentAssignment(Long schoolId,
                                                     Long yearId,
                                                     Long termId,
                                                     Long sectionId,
                                                     Long assignmentId,
                                                     StudentAssignment studentAssignment) {
        StudentAssignment response = new StudentAssignment(studentAssignment);
        EntityId id = create(studentAssignment,
                SCHOOL_ENDPOINT + "/" + schoolId + SCHOOL_YEAR_ENDPOINT + "/" + yearId +
                TERM_ENDPOINT + "/" + termId + SECTION_ENDPOINT + "/" + sectionId +
                SECTION_ASSIGNMENT_ENDPOINT + "/" + assignmentId + STUDENT_ASSIGNMENT_ENDPOINT);
        response.setId(id.getId());
        return response;
    }

    @Override
    public void createStudentAssignments(Long schoolId,
                                         Long yearId,
                                         Long termId,
                                         Long sectionId,
                                         Long assignmentId,
                                         List<StudentAssignment> studentAssignments) {
        createVoidResponse(studentAssignments,
                SCHOOL_ENDPOINT + "/" + schoolId + SCHOOL_YEAR_ENDPOINT + "/" + yearId +
                TERM_ENDPOINT + "/" + termId + SECTION_ENDPOINT + "/" + sectionId +
                SECTION_ASSIGNMENT_ENDPOINT + "/" + assignmentId + STUDENT_ASSIGNMENT_ENDPOINT + "/bulk");
    }

    /**
     * A method to authenticate a user and store the returned auth cookie for subsequent requests.
     * Called by all integration test classes that are testing protected endpoints.
     */
    @Override
    protected synchronized void authenticate() {
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
    private byte[] convertObjectToJsonBytes(Object object) {

        if (null == object) {
            return new byte[0];
        }

        byte[] out = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            out = mapper.writeValueAsBytes(object);
            ////LOGGER.sys().info("JSON: " + new String(out, CHARSET_UTF8_NAME));
        } catch (Exception e) {
            throw new HttpClientException(e);
        }
        return out;
    }
}
