package com.scholarscore.api.controller.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.ImmutableMap;
import com.scholarscore.api.controller.service.AdministratorValidatingExecutor;
import com.scholarscore.api.controller.service.AssignmentValidatingExecutor;
import com.scholarscore.api.controller.service.AttendanceValidatingExecutor;
import com.scholarscore.api.controller.service.AuthValidatingExecutor;
import com.scholarscore.api.controller.service.BehaviorValidatingExecutor;
import com.scholarscore.api.controller.service.CourseValidatingExecutor;
import com.scholarscore.api.controller.service.DashboardValidatingExecutor;
import com.scholarscore.api.controller.service.GoalValidatingExecutor;
import com.scholarscore.api.controller.service.GpaValidatingExecutor;
import com.scholarscore.api.controller.service.LocaleServiceUtil;
import com.scholarscore.api.controller.service.McasValidatingExecutor;
import com.scholarscore.api.controller.service.MessageValidatingExecutor;
import com.scholarscore.api.controller.service.NotificationValidatingExecutor;
import com.scholarscore.api.controller.service.QueryValidatingExecutor;
import com.scholarscore.api.controller.service.SchoolDayValidatingExecutor;
import com.scholarscore.api.controller.service.SchoolValidatingExecutor;
import com.scholarscore.api.controller.service.SchoolYearValidatingExecutor;
import com.scholarscore.api.controller.service.SectionValidatingExecutor;
import com.scholarscore.api.controller.service.StudentAssignmentValidatingExecutor;
import com.scholarscore.api.controller.service.StudentSectionGradeValidatingExecutor;
import com.scholarscore.api.controller.service.StudentValidatingExecutor;
import com.scholarscore.api.controller.service.SurveyResponseValidatingExecutor;
import com.scholarscore.api.controller.service.SurveyValidatingExecutor;
import com.scholarscore.api.controller.service.TeacherValidatingExecutor;
import com.scholarscore.api.controller.service.TermValidatingExecutor;
import com.scholarscore.api.controller.service.UiAttributesValidatingExecutor;
import com.scholarscore.api.controller.service.UserValidatingExecutor;
import com.scholarscore.models.LoginRequest;
import com.scholarscore.models.School;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.User;
import com.scholarscore.util.EdPanelObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class that contains all common methods for servicing requests
 */
public class IntegrationBase {
    private NetMvc mockMvc;
    private static final String BASE_URI_KEY = "httpsEndpoint";
    private final static String CHARSET_UTF8_NAME = "UTF-8";

    private final static MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
    
    private static final String BASE_API_ENDPOINT = "/api/v1";
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
    private static final String ADMINISTRATORS_ENDPOINT = "/administrators";
    private static final String QUERIES_ENDPOINT = "/queries";
    private static final String BEHAVIOR_ENDPOINT = "/behaviors";
    private static final String AUTH_ENDPOINT = "/auth";
    private static final String USERS_ENDPOINT = "/users";
    private static final String GOAL_ENDPOINT = "/goals";
    private static final String SCHOOL_DAY_ENDPOINT = "/days";
    private static final String ATTENDANCE_ENDPOINT = "/attendance";
    private static final String UI_ATTRIBUTES_ENDPOINT = "/uiattributes";
    private static final String GPA_ENDPOINT = "/gpas";
    private static final String SURVEY_ENDPOINT = "/surveys";
    private static final String SURVEY_RESPONSE_ENDPOINT = "/responses";
    private static final String RESPONDENTS_ENDOINT = "/respondents";
    private static final String NOTIFICATIONS_ENDPOINT = "/notifications";
    private static final String MESSAGE_THREADS_ENDPOINT = "/messagethreads";
    private static final String MESSAGE_ENDPOINT = "/messages";
    private static final String DASHBOARD_ENDPOINT = "/dashboards";
    private static final String MCAS_ENDPOINT = "/mcas";

    public LocaleServiceUtil localeServiceUtil;
    public CourseValidatingExecutor courseValidatingExecutor;
    public SchoolValidatingExecutor schoolValidatingExecutor;
    public SchoolYearValidatingExecutor schoolYearValidatingExecutor;
    public TermValidatingExecutor termValidatingExecutor;
    public SectionValidatingExecutor sectionValidatingExecutor;
    public AssignmentValidatingExecutor sectionAssignmentValidatingExecutor;
    public StudentValidatingExecutor studentValidatingExecutor;
    public StudentAssignmentValidatingExecutor studentAssignmentValidatingExecutor;
    public StudentSectionGradeValidatingExecutor studentSectionGradeValidatingExecutor;
    public TeacherValidatingExecutor teacherValidatingExecutor;
    public AdministratorValidatingExecutor administratorValidatingExecutor;
    public QueryValidatingExecutor queryValidatingExecutor;
    public BehaviorValidatingExecutor behaviorValidatingExecutor;
    public AuthValidatingExecutor authValidatingExecutor;
    public UserValidatingExecutor userValidatingExecutor;
    public GoalValidatingExecutor goalValidatingExecutor;
    public SchoolDayValidatingExecutor schoolDayValidatingExecutor;
    public AttendanceValidatingExecutor attendanceValidatingExecutor;
    public UiAttributesValidatingExecutor uiAttributesValidatingExecutor;
    public GpaValidatingExecutor gpaValidatingExecutor;
    public SurveyValidatingExecutor surveyValidatingExecutor;
    public SurveyResponseValidatingExecutor surveyResponseValidatingExecutor;
    public NotificationValidatingExecutor notificationValidatingExecutor;
    public MessageValidatingExecutor messageValidatingExecutor;
    public DashboardValidatingExecutor dashboardValidatingExecutor;
    public McasValidatingExecutor mcasValidatinExecutor;

    public CopyOnWriteArrayList<School> schoolsCreated = new CopyOnWriteArrayList<>();
    public CopyOnWriteArrayList<Student> studentsCreated = new CopyOnWriteArrayList<>();
    public CopyOnWriteArrayList<Staff> teachersCreated = new CopyOnWriteArrayList<>();
    public CopyOnWriteArrayList<Staff> adminsCreated = new CopyOnWriteArrayList<>();

    // Locale used in testing. Supplied as command-line arguments to JVM: -Dlocale=de_DE
    // Valid values include the following:
    // US: en_US
    // German: de_DE
    // Chinese: zh_CN
    // British: en_CB
    // Canadian: en_CA
    // French Canadian: fr_CA
    // ....
    // @see http://www.oracle.com/technetwork/java/javase/javase7locales-334809.html
    public ThreadLocal<Locale> locale = new ThreadLocal<>();

    // ContentType is used in testing to either json or xml post requests
    // Supplied as command-line argument to JVM: _DcontentType=json
    // Valid values are the following:
    // json
    // xml
    //
    // (json and xml where used instead of application/json and application/json because the
    // contentType used also includes UTF8 charset)
    private String contentType;
    private ThreadLocal<URL> endpoint = new ThreadLocal<URL>();
    private URL defaultEndpoint;
    private Properties properties;
    private String usersEndpoint;
    public List<User> usersCreated = new ArrayList<>();

    /**
     * Description:
     * Helper method used to initialize all test service and data needed
     * Expected Result:
     * Test services and initialization completed
     */
    @BeforeClass(alwaysRun=true)
    public void configureServices() {
        this.mockMvc = new NetMvc();
        localeServiceUtil = new LocaleServiceUtil(this);
        courseValidatingExecutor = new CourseValidatingExecutor(this);
        schoolValidatingExecutor = new SchoolValidatingExecutor(this);
        schoolYearValidatingExecutor = new SchoolYearValidatingExecutor(this);
        termValidatingExecutor = new TermValidatingExecutor(this);
        sectionValidatingExecutor = new SectionValidatingExecutor(this);
        sectionAssignmentValidatingExecutor = new AssignmentValidatingExecutor(this);
        studentValidatingExecutor = new StudentValidatingExecutor(this);
        studentAssignmentValidatingExecutor = new StudentAssignmentValidatingExecutor(this);
        studentSectionGradeValidatingExecutor = new StudentSectionGradeValidatingExecutor(this);
        administratorValidatingExecutor = new AdministratorValidatingExecutor(this);
        teacherValidatingExecutor = new TeacherValidatingExecutor(this);
        queryValidatingExecutor = new QueryValidatingExecutor(this);
        behaviorValidatingExecutor = new BehaviorValidatingExecutor(this);
        authValidatingExecutor = new AuthValidatingExecutor(this);
        userValidatingExecutor = new UserValidatingExecutor(this);
        goalValidatingExecutor = new GoalValidatingExecutor(this);
        attendanceValidatingExecutor = new AttendanceValidatingExecutor(this);
        schoolDayValidatingExecutor = new SchoolDayValidatingExecutor(this);
        uiAttributesValidatingExecutor = new UiAttributesValidatingExecutor(this);
        gpaValidatingExecutor = new GpaValidatingExecutor(this);
        surveyValidatingExecutor = new SurveyValidatingExecutor(this);
        surveyResponseValidatingExecutor = new SurveyResponseValidatingExecutor(this);
        notificationValidatingExecutor = new NotificationValidatingExecutor(this);
        messageValidatingExecutor = new MessageValidatingExecutor(this);
        dashboardValidatingExecutor = new DashboardValidatingExecutor(this);
        mcasValidatinExecutor = new McasValidatingExecutor(this);
        validateServiceConfig();
        initializeTestConfig();
        EdPanelObjectMapper.MAPPER.registerModule(new JavaTimeModule());
        EdPanelObjectMapper.MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
    
    // TODO: make these into properties    
    protected String TEST_ADMIN_USERNAME = "mattg";
    protected String TEST_ADMIN_PASSWORD = "admin";

    /**
     * A method to authenticate a user and store the returned auth cookie for subsequent requests.
     * Called by all integration test classes that are testing protected endpoints.
     */
    protected void authenticate() {
        authenticate(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD);
    }
    
    protected void authenticate(String username, String password) {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsername(username);
        loginReq.setPassword(password);
        makeRequest(
                HttpMethod.POST,
                BASE_API_ENDPOINT + LOGIN_ENDPOINT,
                null,
                null,
                loginReq,
                null);
    }
    
    /**
     * Description:
     * Helper method used to validate all services created before using them
     * Expected Result:
     * Test services initialization validated
     */
    private void validateServiceConfig() {
        Assert.assertNotNull(courseValidatingExecutor, "Unable to configure course service");
        Assert.assertNotNull(schoolValidatingExecutor, "Unable to configure school service");
        Assert.assertNotNull(schoolYearValidatingExecutor, "Unable to configure school year service");
        Assert.assertNotNull(termValidatingExecutor, "Unable to configure term service");
        Assert.assertNotNull(sectionValidatingExecutor, "Unable to configure section service");
        Assert.assertNotNull(sectionAssignmentValidatingExecutor, "Unable to configure section assignment service");
        Assert.assertNotNull(studentValidatingExecutor, "Unable to configure student service");
        Assert.assertNotNull(studentAssignmentValidatingExecutor, "Unable to configure student assignment service");
        Assert.assertNotNull(teacherValidatingExecutor, "Unable to configure teacher service");
        Assert.assertNotNull(queryValidatingExecutor, "Unable to configure query service");
        Assert.assertNotNull(authValidatingExecutor, "Unable to configure auth service");
        Assert.assertNotNull(userValidatingExecutor, "Unable to configure user service");
        Assert.assertNotNull(attendanceValidatingExecutor, "Unable to configure auth service");
        Assert.assertNotNull(schoolDayValidatingExecutor, "Unable to configure user service");
        Assert.assertNotNull(uiAttributesValidatingExecutor, "Unable to configure ui attrs service");
        Assert.assertNotNull(gpaValidatingExecutor, "Unable to configure GPA service");
        Assert.assertNotNull(surveyValidatingExecutor, "Unable to configure survey service");
        Assert.assertNotNull(surveyResponseValidatingExecutor, "Unable to configure survey response service");
        Assert.assertNotNull(notificationValidatingExecutor, "Unable to configure notification service");
        Assert.assertNotNull(messageValidatingExecutor, "Unable to configure message service");
        Assert.assertNotNull(dashboardValidatingExecutor, "Unable to configure dashboard service");
        Assert.assertNotNull(mcasValidatinExecutor, "Unable to configure MCAS service");
    }

    /**
     * Description:
     * Helper method used to remove test data created in test run
     * Expected Result:
     * All test data associated with application is removed
     */
    @AfterClass(alwaysRun = true)
    protected void removeTestData() {
        // Remove all Apps created during testing
        if (null == endpoint || null == endpoint.get()) {
            if (null != properties) {
                try {
                    defaultEndpoint = new URL(properties.getProperty(BASE_URI_KEY));
                } catch (MalformedURLException e) {
                    Assert.fail("IntegrationBase.removeTestData failed: config property 'endpoint' is not a well-formed URL", e);
                }
                endpoint.set(defaultEndpoint);
            } else {
                configureServices();
            }
        }

        localeServiceUtil.setLocale();
        contentType = System.getProperty("contentType", "json");
        for(School s : schoolsCreated) {
            makeRequest(HttpMethod.DELETE, getSchoolEndpoint(s.getId()));
        }
        for(Student s : studentsCreated) {
            makeRequest(HttpMethod.DELETE, getStudentEndpoint(s.getId()));
        }
        for(Staff t : teachersCreated) {
            makeRequest(HttpMethod.DELETE, getTeacherEndpoint(t.getId()));
        }
        for (Staff a : adminsCreated) {
            makeRequest(HttpMethod.DELETE, getAdministratorEndpoint(a.getId()));
        }
    }
    
    /**
     * Description:
     * Helper method used to initialize test config (environment, locale, etc)
     * Expected Result:
     * Test configuration completed
     */
    synchronized void initializeTestConfig() {
        // only initialize once
        localeServiceUtil.setLocale();
        contentType = System.getProperty("contentType", "json");
        if (null == properties) {
            String env = System.getProperty("env", "localhost");
            String resourceName =  env.toLowerCase() + ".config.properties";
            InputStream configStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
            if (configStream != null) {
                properties = new Properties();
                try {
                    properties.load(configStream);
                } catch (IOException e) {
                    Assert.fail("TEST SETUP FAILED: There was a problem trying to load and map the java properties defined " +
                            "in the environment specific properties file named '" + resourceName + "'.\n" +
                            e.getMessage(), e);
                }

                for (Object oName : properties.keySet()) {
                    String name = oName.toString();
                    String value = System.getProperty(name);
                    if (null != value && !value.isEmpty()) {
                        properties.setProperty(name, value);
                    }
                }
                try {
                    defaultEndpoint = new URL(properties.getProperty(BASE_URI_KEY));
                } catch (MalformedURLException e) {
                    Assert.fail("TEST SETUP FAILED: config property 'endpoint' is not a well-formed URL", e);
                }
                endpoint = new ThreadLocal<URL>() {
                    @Override
                    protected URL initialValue() {
                        return defaultEndpoint;
                    }
                };
            } else {
                Assert.fail("TEST SETUP FAILED: Resource file '" + resourceName + "' not found on classpath.");
            }
        }
    }
    
    public LocaleServiceUtil getLocaleServiceValidator() {
        return localeServiceUtil;
    }

    /**
     * Description:
     * Private helper method used to make request and return response object
     * Expected Result:
     * Generic Results object returned from request
     */
    public ResultActions makeRequest(HttpMethod method, String url) {
        return makeRequest(method, url, null, null);
    }

    /**
     * Description:
     * Private helper method used to make request and return response object
     * Expected Result:
     * Generic Results object returned from request
     */
    public ResultActions makeRequest(HttpMethod method, String url, Map<String, String> params) {
        return makeRequest(method, url, params, null);
    }

    /**
     * Description:
     * Private helper method used to make request containing request method(get, post,put, etc), url, request parameters, and content.
     * Request is performed and response is printed before being returned as a ResultActions response object
     * Expected Result:
     * Generic Results object returned from request
     */
    public ResultActions makeRequest(HttpMethod method, String url, Map<String, String> params, Object content) {
        return makeRequest(method, url, null, params, content, null);
    }

    /**
     * Builds and executes an HTTP request against the current endpoint.
     *
     * @param method HTTP method to use (GET, POST, DELETE, etc.)
     * @param url URL where the request will be made, excluding the current
     * endpoint which will be prepended to this URL
     * @param headers HTTP headers to add to the request
     * @param params query parameters to add to the request
     * @param content content body to add to the request for POST, PUT, PATCH,
     * and optionally DELETE requests
     * @return server response
     */
    ResultActions makeRequest(HttpMethod method, String url, Map<String, String> headers, Map<String, String> params, Object content, MediaType reqType) {
        if(null == reqType) {
            reqType = APPLICATION_JSON_UTF8;
        }
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.request(method, endpoint.get().toString() + url, "");
        addHeadersAndParamsToRequest(request, headers, params);

        // Set any supplied content in request (ex: App, Report, Table, etc.)
        // along with the required headers
        byte[] contentBytes = null;
        if ("json".equalsIgnoreCase(contentType)) {
            request.header("Accept", "application/json");
            request.contentType(reqType);
            contentBytes = convertObjectToJsonBytes(content);
        } else {
            Assert.fail("Invalid contentType supplied in request: " + contentType);
        }

        // Execute the request and return the response
        ResultActions retResult = null;
        try {
            retResult = mockMvc.perform(request, contentBytes).andDo(MockMvcResultHandlers.print());
        } catch (Exception e) {
            Assert.fail("Unexpected exception occurred attempting to make request", e);
        }
        return retResult;
    }

    /**
     * Builds and executes an HTTP request against the current endpoint. This
     * overload is for requests containing a multipart file upload.
     *
     * This function does NOT take an HttpMethod. MockMvcRequestBuilders#fileUpload()
     * generates an HttpPost.
     *
     * @param url URL where the request will be made, excluding the current
     * endpoint which will be prepended to this URL
     * @param headers HTTP headers to add to the request
     * @param params query parameters to add to the request
     * @param file multipart file being uploaded as part fo the request
     * @return server response
     */
    ResultActions makeRequest(String url, Map<String, String> headers, Map<String, String> params, MockMultipartFile file) {
        MockMultipartHttpServletRequestBuilder request = MockMvcRequestBuilders.fileUpload(endpoint.get() + url, "");
        addHeadersAndParamsToRequest(request, headers, params);

        // Set the Accept header (json/xml)
        if ("json".equalsIgnoreCase(contentType)) {
            request.header("Accept", "application/json");
        } else if ("xml".equalsIgnoreCase(contentType)) {
            request.header("Accept", "application/xml");
        } else {
            Assert.fail("Invalid contentType supplied in request: " + contentType);
        }

        // Add the multi-part file to the request
        request.file(file);

        // Execute the request and return the response
        ResultActions retResult = null;
        try {
            retResult = mockMvc.perform(request).andDo(MockMvcResultHandlers.print());
        } catch (Exception e) {
            Assert.fail("Unexpected exception occurred attempting to make request", e);
        }
        return retResult;
    }

    /**
     * Adds the specified headers and query params to the Spring mock request
     * builder.
     *
     * @param mockRequestBuilder spring mock request builder to add the headers
     * and params to
     * @param headers headers to add to the request
     * @param params params to add to the request
     */
    private void addHeadersAndParamsToRequest(MockHttpServletRequestBuilder mockRequestBuilder,
                                     Map<String,String> headers, Map<String,String> params) {
        // Set any supplied headers in request - ex: LOCALE
        mockRequestBuilder.header("Accept-Language", locale.get());
        mockRequestBuilder.locale(locale.get());

        // Set any additional headers
        if (null != headers) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                mockRequestBuilder.header(entry.getKey(), entry.getValue());
            }
        }

        // Set any supplied query params in request - ex: UserId or Ticket
        if (null != params) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                mockRequestBuilder.param(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Description:
     * Private helper method used to validate the supplied response contained the expected controller, status, method. The
     * response content is then converted to the supplied reference type and returned.
     * Expected Result:
     * Expected information updated and generic reference type from response returned.
     */
    public <T> T validateResponse(ResultActions response, 
            final TypeReference<T> type) {
        try {
            int status = response.andReturn().getResponse().getStatus();
            // Validate Http status and handler info
            // If Https status isn't 200, then determine ErrorCode and return
            if (status != HttpStatus.OK.value()) {
                String content = response.andReturn().getResponse().getContentAsString();
                if (content.contains("<html>")) {
                    Assert.fail("Unexpected HTML error page returned attempting to validate response: \n"
                        + content);
                } else {
                    Assert.fail("Unexpected error code returned attempting to validate response \n"
                    + "Expected 200 but got " + status + " with content: " + content);
                }
            }
        } catch (Exception e) {
            Assert.fail("Unexpected exception occurred attempting to validate response", e);
        }

        // Get parse objects from response
        return mapResponse(response, type);
    }

    /**
     * Maps the response body to an instance of the specified type.
     *
     * @param resultActions server response
     * @param type type to map the response to
     * @return response body mapped to an instance of the specified type
     */
    public <T> T mapResponse(ResultActions resultActions, final TypeReference<T> type) {
        // Instantiate the correct ObjectMapper based on the content type
        ObjectMapper mapper;
        String testContentType = contentType.toLowerCase();
        switch (testContentType) {
            case "json":
                mapper = EdPanelObjectMapper.MAPPER;
                break;
            case "xml":
                mapper = new XmlMapper();
                break;
            default:
                throw new IllegalStateException("Unsupported content type: " + contentType);
        }

        // Configure the mapper
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setLocale(locale.get());
        mapper.registerModule(new JavaTimeModule());

        // Map the response content to the specified type
        T value = null;
        String content = "";
        try {
            MockHttpServletResponse resp = resultActions.andReturn().getResponse();
            resp.setCharacterEncoding(CHARSET_UTF8_NAME);

            content = resp.getContentAsString();
            if (StringUtils.hasLength(content)) {
                value = mapper.readValue(content, type);
            }
        } catch (Exception e) {
            String className = ((Class<?>) type.getType()).getName();
            Assert.fail("Unable to map ServiceResponse to instance of " +
                    className + " for content: " + content, e);
        }
        return value;
    }

    /**
     * Description:
     * Private helper method to set request parameter
     * Expected Result:
     * Create HashMap containing supplied info
     */
    Map<String, String> setReqParams(String name, String value) {
        return ImmutableMap.of(name, value);
    }

    /**
     * Creates a URL path out of the object by performing "/" + o.toString()
     * @param o object that should be on path
     * @return string to be used as path
     */
    public String pathify(Object o) {
        return "/" + o;
    }

    public String getTeacherEndpoint() {
        return BASE_API_ENDPOINT + TEACHER_ENDPOINT;
    }
    
    public String getTeacherEndpoint(Long teacherId) {
        return getTeacherEndpoint() + pathify(teacherId);
    }

    public String getAdministratorEndpoint() {
        return BASE_API_ENDPOINT + ADMINISTRATORS_ENDPOINT;
    }

    public String getAdministratorEndpoint(Long adminId) {
        return getAdministratorEndpoint() + pathify(adminId);
    }

    public String getStudentEndpoint() {
        return BASE_API_ENDPOINT + STUDENT_ENDPOINT;
    }
    
    public String getStudentEndpoint(Long studentId) {
        return getStudentEndpoint() + pathify(studentId);
    }
    
    public String getStudentGpaEndpoint(Long studentId, Integer gpaScale) {
        return getStudentEndpoint(studentId) + GPA_ENDPOINT + pathify(gpaScale);
    }
    
    /**
     * returns the school endpoint
     * @return
     */
    public String getSchoolEndpoint() {
        return BASE_API_ENDPOINT + SCHOOL_ENDPOINT;
    }
    
    /**
     * returns the school endpoint
     * @param schoolId
     * @return
     */
    public String getSchoolEndpoint(Long schoolId) {
        return getSchoolEndpoint() + pathify(schoolId);
    }
    
    public String getUiAttributesEndpoint(Long schoolId) {
        return getSchoolEndpoint(schoolId) + UI_ATTRIBUTES_ENDPOINT;
    }
    
    public String getSchoolDayEndpoint(Long schoolId) {
        return getSchoolEndpoint(schoolId) + SCHOOL_DAY_ENDPOINT;
    }
    
    public String getSchoolDayEndpoint(Long schoolId, Long schoolDayId) {
        return getSchoolDayEndpoint(schoolId) + pathify(schoolDayId);
    }
    
    public String getAttendanceEndpoint(Long schoolId, Long studentId) {
        return getSchoolEndpoint(schoolId) + STUDENT_ENDPOINT + pathify(studentId) + ATTENDANCE_ENDPOINT;
    }
    
    public String getAttendanceEndpoint(Long schoolId, Long studentId, Long attendanceId) {
        return getAttendanceEndpoint(schoolId, studentId) + pathify(attendanceId);
    }
    
    /**
     * return the school year endpoint without a school year ID
     * @param schoolId
     * @return
     */
    public String getSchoolYearEndpoint(Long schoolId) {
        return getSchoolEndpoint(schoolId) + SCHOOL_YEAR_ENDPOINT;
    }
    
    /**
     * Return the school year endpoint with a school year ID appended
     * @param schoolId
     * @param schoolYearId
     * @return
     */
    public String getSchoolYearEndpoint(Long schoolId, Long schoolYearId) {
        return getSchoolYearEndpoint(schoolId) + pathify(schoolYearId); 
    }
    
    public String getQueryEndpoint(Long schoolId, Long queryId) {
        return getQueryEndpoint(schoolId) + pathify(queryId);
    }
    
    public String getQueryEndpoint(Long schoolId) {
        return getSchoolEndpoint(schoolId) + QUERIES_ENDPOINT;
    }
    
    /**
     * Generates and return the course endpoint
     * @param schoolId
     * @return
     */
    public String getCourseEndpoint(Long schoolId) {
        return getSchoolEndpoint(schoolId) + COURSE_ENDPOINT;
    }
    
    /**
     * Generates and returns the course endpoint including courseId
     * @param schoolId
     * @param courseId
     * @return
     */
    public String getCourseEndpoint(Long schoolId, Long courseId) {
        return getCourseEndpoint(schoolId) + pathify(courseId);
    }
    
    /**
     * Generates and returns a term endpoint
     * @param schoolId
     * @param schoolYearId
     * @return
     */
    public String getTermEndpoint(Long schoolId, Long schoolYearId) {
        return getSchoolYearEndpoint(schoolId, schoolYearId) + TERM_ENDPOINT;
    }
    
    /**
     * Generates and returns a term endpoint including termId
     * @param schoolId
     * @param schoolYearId
     * @param termId
     * @return
     */
    public String getTermEndpoint(Long schoolId, Long schoolYearId, Long termId) {
        return getTermEndpoint(schoolId, schoolYearId) + pathify(termId);
    }
    
    /**
     * generates and returns a section endpoint without a section ID
     * @param schoolId
     * @param schoolYearId
     * @param termId
     * @return
     */
    public String getSectionEndpoint(Long schoolId, Long schoolYearId, Long termId) {
        return getTermEndpoint(schoolId, schoolYearId, termId) + SECTION_ENDPOINT;
    }
    
    /**
     * Generates and returns a section endpoint with a section ID
     * @param schoolId
     * @param schoolYearId
     * @param termId
     * @param sectionId
     * @return
     */
    public String getSectionEndpoint(Long schoolId, Long schoolYearId, Long termId, Long sectionId) {
        return getSectionEndpoint(schoolId, schoolYearId, termId) + pathify(sectionId);
    }
    
    /**
     * Generates and returns a section assignment endpoint without a section ID
     * @param schoolId
     * @param schoolYearId
     * @param termId
     * @param sectionId
     * @return
     */
    public String getSectionAssignmentEndpoint(Long schoolId, Long schoolYearId, Long termId, Long sectionId) {
        return getSectionEndpoint(schoolId, schoolYearId, termId, sectionId) + SECTION_ASSIGNMENT_ENDPOINT;
    }
    
    /**
     * Generates and returns a section assignment endpoint with a section ID
     * @param schoolId
     * @param schoolYearId
     * @param termId
     * @param sectionId
     * @param sectionAssignmentId
     * @return
     */
    public String getSectionAssignmentEndpoint(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long sectionAssignmentId) {
        return getSectionAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId) + pathify(sectionAssignmentId);
    }
    
    public String getStudentSectionGradeEndpoint(Long schoolId, Long schoolYearId, Long termId, Long sectionId, Long studentId) {
        return getStudentSectionGradeEndpoint(schoolId, schoolYearId, termId, sectionId) + STUDENT_ENDPOINT + pathify(studentId);
    }
    
    public String getStudentSectionGradeEndpoint(Long schoolId, Long schoolYearId, Long termId, Long sectionId) {
        return getSectionEndpoint(schoolId, schoolYearId, termId, sectionId) + STUDENT_SECTION_GRADE_ENDPOINT;
    }
    
    public String getStudentAssignmentEndpoint(Long schoolId, Long schoolYearId, Long termId, Long sectionId, 
            Long sectionAssignmentId, Long studentAssignmentId) {
        return getStudentAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, sectionAssignmentId) 
                + pathify(studentAssignmentId);
    }
    
    public String getStudentAssignmentEndpoint(Long schoolId, Long schoolYearId, Long termId, Long sectionId, 
            Long sectionAssignmentId) {
        return getSectionAssignmentEndpoint(schoolId, schoolYearId, termId, sectionId, sectionAssignmentId) 
                + STUDENT_ASSIGNMENT_ENDPOINT;
    }

    public String getGpaEndpoint() {
        return BASE_API_ENDPOINT + GPA_ENDPOINT;
    }
    public String getGpaEndpoint(Long studentId) {
        return getGpaEndpoint() + STUDENT_ENDPOINT + pathify(studentId);
    }
    
    public String getGpaEndpoint(Long studentId, Long gpaId) {
        return getGpaEndpoint() + pathify(gpaId) + STUDENT_ENDPOINT + pathify(studentId);
    }

    public String getBehaviorEndpoint(Long studentId) {
        return getStudentEndpoint(studentId) + BEHAVIOR_ENDPOINT;
    }

    public String getBehaviorEndpoint(Long studentId, Long gpa) {
        return getBehaviorEndpoint(studentId) + pathify(gpa);
    }

    public String getGoalEndpoint(Long studentId) {
        return getStudentEndpoint(studentId) + GOAL_ENDPOINT;
    }

    public String getGoalEndpoint(Long studentId, Long goalId) {
        return getGoalEndpoint(studentId) + pathify(goalId);
    }

    /**
     * Serializes an object into JSON or XML depending on the currently set
     * contentType.
     *
     * @param object object to serialize
     * @return serialized JSON or XML bytes
     */
    public byte[] convertObjectToBytes(Object object) {
        byte[] bytes = null;

        String testContentType = contentType.toLowerCase();
        switch (testContentType) {
            case "json":
                bytes = convertObjectToJsonBytes(object);
                break;
            default:
                throw new IllegalStateException("Unsupported content type: " + contentType);
        }

        return bytes;
    }

    /**
     * Description:
     * Private helper method used to convert test object to JSON
     * Expected Result:
     * JSON string representation of supplied object
     */
    private byte[] convertObjectToJsonBytes(Object object) {

        if (null == object) {
            return new byte[0];
        }

        byte[] out = null;
        try {
            EdPanelObjectMapper.MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            out = EdPanelObjectMapper.MAPPER.writeValueAsBytes(object);
            String bytes = new String(out);
            ////LOGGER.sys().info("JSON: " + new String(out, CHARSET_UTF8_NAME));
        } catch (Exception e) {
            ////LOGGER.sys().error("Unable to convert object to JSON " + e.getMessage(), e);
            Assert.fail("Unable to convert object to JSON " + e.getMessage(), e);
        }
        return out;
    }

    /**
     * Returns the default endpoint for this set of tests.
     *
     * @return default endpoint
     */
    public URL getDefaultEndpoint() {
        return defaultEndpoint;
    }

    /**
     * Get the current user end point
     * @return
     */
    public String getCurrentUserEndpoint() {
        return BASE_API_ENDPOINT + AUTH_ENDPOINT;
    }

    public String getUsersEndpoint() {
        return BASE_API_ENDPOINT + USERS_ENDPOINT;
    }

    public String getAdminsEndpoint() {
        return BASE_API_ENDPOINT + ADMINISTRATORS_ENDPOINT;
    }

    public String getUsersEndpoint(Long userId) {
        return BASE_API_ENDPOINT + USERS_ENDPOINT + pathify(userId);
    }

    public String getDashboardEndpoint(Long schoolId) {
        return getSchoolEndpoint(schoolId) + DASHBOARD_ENDPOINT;
    }

    public String getDashboardEndpoint(Long schoolId, Long dashId) {
        return getDashboardEndpoint(schoolId) + pathify(dashId);
    }

    public String getSurveyEndpoint() {
        return BASE_API_ENDPOINT + SURVEY_ENDPOINT;
    }

    public String getSurveyEndpoint(Long surveyId) {
        return getSurveyEndpoint() + pathify(surveyId);
    }

    public String getSurveyEndpointWithUserId(Long userId) {
        return getSurveyEndpoint() + USERS_ENDPOINT + pathify(userId);
    }

    public String getSurveyEndpointWithSchoolId(Long schoolId) {
        return getSurveyEndpoint() + SCHOOL_ENDPOINT + pathify(schoolId);
    }

    public String getSurveyResponseEndpoint(Long surveyId) {
        return getSurveyEndpoint(surveyId) + SURVEY_RESPONSE_ENDPOINT;
    }

    public String getSurveyResponseEndpoint(Long surveyId, Long respId) {
        return getSurveyResponseEndpoint(surveyId) + pathify(respId);
    }

    public String getSurveyResponseByRespondentEndpoint(Long respondentId) {
        return getSurveyEndpoint() + RESPONDENTS_ENDOINT + pathify(respondentId) + SURVEY_RESPONSE_ENDPOINT;
    }

    public String getNotificationEndpoint() {
        return BASE_API_ENDPOINT + NOTIFICATIONS_ENDPOINT;
    }

    public String getNotificationEndpoint(Long notificationId) {
        return getNotificationEndpoint() + pathify(notificationId);
    }

    public String getMessageThreadsEndpoint() {
        return BASE_API_ENDPOINT + MESSAGE_THREADS_ENDPOINT;
    }

    public String getMessageThreadsEndpoint(Long threadId) {
        return getMessageThreadsEndpoint() + pathify(threadId);
    }

    public String getMessageEndpoint(Long threadId) {
        return getMessageThreadsEndpoint(threadId) + MESSAGE_ENDPOINT;
    }

    public String getMessageEndpoint(Long threadId, Long messageId) {
        return getMessageEndpoint(threadId) + pathify(messageId);
    }

    public String getUnreadMessagesForUserEndpoint(Long threadId, Long userId) {
        return getMessageThreadsEndpoint(threadId) + "/participants/" + userId + MESSAGE_ENDPOINT;
    }

    public String getUnreadMessagesForUserEndpont(Long userId) {
        return getMessageThreadsEndpoint() + "/participants/" + userId + MESSAGE_ENDPOINT;
    }

    public String getMarkMessageReadEndpoint(Long threadId, Long messageId, Long userId) {
        return getMessageEndpoint(threadId, messageId) + "/participants/" + userId + "/readreciepts";
    }

    public String getMcasEndpoint() {
        return BASE_API_ENDPOINT + MCAS_ENDPOINT;
    }

    public String getMcasEndpoint(Long schoolId, Long studentId) {
        return getSchoolEndpoint(schoolId) + STUDENT_ENDPOINT + pathify(studentId) + MCAS_ENDPOINT;
    }

    public String getMcasEndpoint(Long schoolId, Long studentId, Long mcasId) {
        return getMcasEndpoint(schoolId, studentId) + pathify(mcasId);
    }


    protected void invalidateCookie() { mockMvc.setjSessionId(null); }
    
    protected LocalDate getNow() {
        return LocalDate.now();
    }


}