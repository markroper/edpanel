package com.scholarscore.api.controller.base;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.collect.ImmutableMap;
import com.scholarscore.api.controller.service.AssignmentValidatingExecutor;
import com.scholarscore.api.controller.service.CourseValidatingExecutor;
import com.scholarscore.api.controller.service.LocaleServiceUtil;
import com.scholarscore.api.controller.service.SchoolValidatingExecutor;
import com.scholarscore.api.controller.service.SchoolYearValidatingExecutor;
import com.scholarscore.api.controller.service.SectionAssignmentValidatingExecutor;
import com.scholarscore.api.controller.service.SectionValidatingExecutor;
import com.scholarscore.api.controller.service.StudentValidatingExecutor;
import com.scholarscore.api.controller.service.TermValidatingExecutor;
import com.scholarscore.models.Assignment;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.Student;

/**
 * Class that contains all common methods for servicing requests
 */
public class IntegrationBase {

    private NetMvc mockMvc;

    private final static String CHARSET_UTF8_NAME = "UTF-8";

    private final static MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
//    private final static MediaType APPLICATION_XML_UTF8 = new MediaType(MediaType.APPLICATION_XML.getType(),
//            MediaType.APPLICATION_XML.getSubtype(), StandardCharsets.UTF_8);
//    private final static MediaType MULTIPART_FORM_DATA = new MediaType(MediaType.MULTIPART_FORM_DATA.getType(),
//            MediaType.MULTIPART_FORM_DATA.getSubtype(), StandardCharsets.UTF_8);
//    
    private static final String BASE_API_ENDPOINT = "/api/v1";
    private static final String SCHOOL_ENDPOINT = "/schools";
    private static final String SCHOOL_YEAR_ENDPOINT = "/years";
    private static final String COURSE_ENDPOINT = "/courses";
    private static final String ASSIGNMENT_ENDPOINT = "/assignments";
    private static final String TERM_ENDPOINT = "/terms";
    private static final String SECTION_ENDPOINT = "/sections";
    private static final String SECTION_ASSIGNMENT_ENDPOINT = "/sectionassignments";
    private static final String STUDENT_ENDPOINT = "/students";

    public LocaleServiceUtil localeServiceUtil;
    public AssignmentValidatingExecutor assignmentValidatingExecutor;
    public CourseValidatingExecutor courseValidatingExecutor;
    public SchoolValidatingExecutor schoolValidatingExecutor;
    public SchoolYearValidatingExecutor schoolYearValidatingExecutor;
    public TermValidatingExecutor termValidatingExecutor;
    public SectionValidatingExecutor sectionValidatingExecutor;
    public SectionAssignmentValidatingExecutor sectionAssignmentValidatingExecutor;
    public StudentValidatingExecutor studentValidatingExecutor;
    
    public ConcurrentHashMap<Long, Map<Long, List<Assignment>>> assignmentsCreated = new ConcurrentHashMap<>();
    public CopyOnWriteArrayList<School> schoolsCreated = new CopyOnWriteArrayList<>();
    public ConcurrentHashMap<Long, List<SchoolYear>> schoolYearsCreated = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Long, List<Course>> coursesCreated = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Long, List<Section>> sectionsCreated = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Long, Student> studentsCreated = new ConcurrentHashMap<>();

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
        assignmentValidatingExecutor = new AssignmentValidatingExecutor(this);
        courseValidatingExecutor = new CourseValidatingExecutor(this);
        schoolValidatingExecutor = new SchoolValidatingExecutor(this);
        schoolYearValidatingExecutor = new SchoolYearValidatingExecutor(this);
        termValidatingExecutor = new TermValidatingExecutor(this);
        sectionValidatingExecutor = new SectionValidatingExecutor(this);
        sectionAssignmentValidatingExecutor = new SectionAssignmentValidatingExecutor(this);
        studentValidatingExecutor = new StudentValidatingExecutor(this);
        validateServiceConfig();
        initializeTestConfig();
    }

    /**
     * Description:
     * Helper method used to validate all services created before using them
     * Expected Result:
     * Test services initialization validated
     */
    private void validateServiceConfig() {
        Assert.assertNotNull(assignmentValidatingExecutor, "Unable to configure assignment service");
        Assert.assertNotNull(courseValidatingExecutor, "Unable to configure course service");
        Assert.assertNotNull(schoolValidatingExecutor, "Unable to configure school service");
        Assert.assertNotNull(schoolYearValidatingExecutor, "Unable to configure school year service");
        Assert.assertNotNull(termValidatingExecutor, "Unable to configure term service");
        Assert.assertNotNull(sectionValidatingExecutor, "Unable to configure section service");
        Assert.assertNotNull(sectionAssignmentValidatingExecutor, "Unable to configure section assignment service");
        Assert.assertNotNull(studentValidatingExecutor, "Unable to configure student service");
    }

    /**
     * Description:
     * Helper method used to remove test data created in test run
     * Expected Result:
     * All test data associated with application is removed
     */
    @AfterSuite(groups = { "integration" })
    protected void removeTestData() {
        // Remove all Apps created during testing
        //Long startTime = new Date().getTime();
        if (null == endpoint || null == endpoint.get()) {
            if (null != properties) {
                try {
                    defaultEndpoint = new URL(properties.getProperty("endpoint"));
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

        // Remove all assignments created during testing
        for (Map.Entry<Long, Map<Long, List<Assignment>>> entry : assignmentsCreated.entrySet()) {
            Long schoolId = entry.getKey();
            Map<Long, List<Assignment>> courseMap = entry.getValue();
            for(Map.Entry<Long, List<Assignment>> courseEntry : courseMap.entrySet()) {
                for(Assignment assignment : courseEntry.getValue()) {
                    cleanupAssignment(schoolId, courseEntry.getKey(), assignment);
                }
            }
        }
        
        for(Map.Entry<Long, List<Section>> sectionEntry : sectionsCreated.entrySet()) {
            for(Section section : sectionEntry.getValue()) {
                cleanupSection(sectionEntry.getKey(), section);
            }
        }
        
        for(Map.Entry<Long, List<Course>> courseEntry : coursesCreated.entrySet()) {
            for(Course course : courseEntry.getValue()) {
                cleanupCourse(courseEntry.getKey(), course);
            }
        }
        
        for(Map.Entry<Long, List<SchoolYear>> schoolYearEntry : schoolYearsCreated.entrySet()) {
            for(SchoolYear schoolYear : schoolYearEntry.getValue()) {
                cleanupSchoolYear(schoolYearEntry.getKey(), schoolYear);
            }
        }

        for(Map.Entry<Long, Student> studentEntry : studentsCreated.entrySet()) {
            cleanupStudent(studentEntry.getKey());
        }
        
        for(Iterator<School> it = schoolsCreated.iterator(); it.hasNext();) {
            cleanupSchool(it.next());
        }
        //Long completionTime = new Date().getTime();
    }

     /**
     * Deletes an assignment without validating the response. Should only be called
     * to cleanup test data after tests have finished running.
     *
     * @param Assignment the assignment to delete
     */
    private void cleanupAssignment(Long schoolId, Long courseId, Assignment assignment) {
        // Make call to controller and do not validate the response
        makeRequest(HttpMethod.DELETE, getAssignmentEndpoint(schoolId, courseId, assignment.getId()));
    }

    private void cleanupSchool(School school) {
        makeRequest(HttpMethod.DELETE, getSchoolEndpoint(school.getId()));
    }
    
    private void cleanupCourse(Long schoolId, Course course) {
        makeRequest(HttpMethod.DELETE, getCourseEndpoint(schoolId, course.getId()));
    }
    
    private void cleanupSchoolYear(Long schoolId, SchoolYear schoolYear) {
        makeRequest(HttpMethod.DELETE, getSchoolYearEndpoint(schoolId, schoolYear.getId()));
    }
    private void cleanupSection(Long schoolId, Section section) {
        makeRequest(HttpMethod.DELETE, getSectionEndpoint(schoolId, section.getYearId(), section.getTermId(), section.getId()));
    }
    private void cleanupStudent(Long studentId) {
        makeRequest(HttpMethod.DELETE, getStudentEndpoint(studentId));
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
                    defaultEndpoint = new URL(properties.getProperty("endpoint"));
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

    public AssignmentValidatingExecutor getAssignmentServiceValidator() {
        return assignmentValidatingExecutor;
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
        return makeRequest(method, url, null, params, content);
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
    ResultActions makeRequest(HttpMethod method, String url, Map<String, String> headers, Map<String, String> params, Object content) {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.request(method, endpoint.get().toString() + url, "");
        addHeadersAndParamsToRequest(request, headers, params);

        // Set any supplied content in request (ex: App, Report, Table, etc.)
        // along with the required headers
        byte[] contentBytes = null;
        if ("json".equalsIgnoreCase(contentType)) {
            request.header("Accept", "application/json");
            request.contentType(APPLICATION_JSON_UTF8);
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
            // Validate Http status and handler info
            // If Https status isn't 200, then determine ErrorCode and return
            if (response.andReturn().getResponse().getStatus() != HttpStatus.OK.value()) {
                String content = response.andReturn().getResponse().getContentAsString();
                if (content.contains("<html>")) {
                    Assert.fail("Unexpected HTML error page returned attempting to validate response: \n"
                        + content);
                } else {
                    Assert.fail("Unexpected error code returned attempting to validate response");
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
                mapper = new ObjectMapper();
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

    public String getStudentEndpoint() {
        return BASE_API_ENDPOINT + STUDENT_ENDPOINT;
    }
    
    public String getStudentEndpoint(Long studentId) {
        return getStudentEndpoint() + pathify(studentId);
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
    
    /**
     * Description:
     * Private helper method to get string to connect to application endpoint
     * Expected Result:
     * String for application endpoint
     */
    public String getAssignmentEndpoint(Long schoolId, Long courseId) {
        return getCourseEndpoint(schoolId, courseId) + ASSIGNMENT_ENDPOINT;
    }

    /**
     * Description:
     * Private helper method to get string to connect to application endpoint
     * Expected Result:
     * String for application endpoint containing supplied appId
     */
    public String getAssignmentEndpoint(Long schoolId, Long courseId, Long assignmentId) {
        return getAssignmentEndpoint(schoolId, courseId) + pathify(assignmentId);
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
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            out = mapper.writeValueAsBytes(object);
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
}