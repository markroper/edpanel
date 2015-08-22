package com.scholarscore.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.LoginRequest;
import com.scholarscore.models.School;
import com.scholarscore.models.Student;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

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
    private static final String COURSE_ENDPOINT = "/courses";
    private static final String TERM_ENDPOINT = "/terms";
    private static final String SECTION_ENDPOINT = "/sections";
    private static final String SECTION_ASSIGNMENT_ENDPOINT = "/assignments";
    private static final String STUDENT_ENDPOINT = "/students";
    private static final String STUDENT_ASSIGNMENT_ENDPOINT = "/studentassignments";
    private static final String STUDENT_SECTION_GRADE_ENDPOINT = "/grades";
    private static final String TEACHER_ENDPOINT = "/teachers";
    private static final String BEHAVIOR_ENDPOINT = "/behaviors";

    private final String password;
    private final String username;
    private Boolean hasAuthenticated = false;

    public APIClient(String username, String password, URI uri) {
        super(uri);
        this.username = username;
        this.password = password;
        authenticate();
    }

    public School createSchool(School school) {
        EntityId id = create(school, SCHOOL_ENDPOINT);
        School response = new School(school);
        response.setId(id.getId());
        return response;
    }

    private EntityId create(Object obj, String path) {
        String jsonCreateResponse = post(convertObjectToJsonBytes(obj), BASE_API_ENDPOINT + path);
        return gson.fromJson(jsonCreateResponse, EntityId.class);
    }
    
    private EntityId update(Object obj, String path) {
        String jsonCreateResponse = patch(convertObjectToJsonBytes(obj), BASE_API_ENDPOINT + path);
        return gson.fromJson(jsonCreateResponse, EntityId.class);
    }

    public School getSchool(Long schoolId) {
        School response = get(School.class,
                SCHOOL_ENDPOINT + "/" + schoolId);
        return response;
    }

    @Override
    public Student createStudent(Student student) {
        EntityId id = create(student, STUDENT_ENDPOINT);
        Student response = new Student(student);
        response.setId(id.getId());
        return response;
    }

    @Override
    public Collection<Student> getStudents() {
        Student[] students = get(Student[].class, BASE_API_ENDPOINT + STUDENT_ENDPOINT);
        for (Student student : students) {
            System.out.println("Got scholarScore student: " + student);
        }
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

    @Override
    public Collection<Behavior> getBehaviors(Long studentId) {
        throw new UnsupportedOperationException("not implemented yet");
//        Behavior[] behaviors = get(Behavior[].class, BASE_API_ENDPOINT + BEHAVIOR_ENDPOINT);
//        for (Behavior behavior : behaviors) {
//            System.out.println("Got scholarScore behavior: " + behavior);
//        }
//        return Arrays.asList(behaviors);
    }

    @Override
    public Behavior createBehavior(Long studentId, Behavior behavior) {
        EntityId id = create(behavior, STUDENT_ENDPOINT + "/" + studentId + BEHAVIOR_ENDPOINT);
        Behavior response = new Behavior(behavior);
        response.setId(id.getId());
        return response;
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
