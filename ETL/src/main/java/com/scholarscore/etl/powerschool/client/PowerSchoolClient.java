package com.scholarscore.etl.powerschool.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.scholarscore.client.BaseHttpClient;
import com.scholarscore.client.HttpClientException;
import com.scholarscore.etl.powerschool.api.auth.OAuthResponse;
import com.scholarscore.etl.powerschool.api.deserializers.IDeserialize;
import com.scholarscore.etl.powerschool.api.deserializers.NaturalDeserializer;
import com.scholarscore.etl.powerschool.api.model.PsCourses;
import com.scholarscore.etl.powerschool.api.model.PsStaffs;
import com.scholarscore.etl.powerschool.api.model.PsStudents;
import com.scholarscore.etl.powerschool.api.model.assignment.PGAssignments;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PGAssignmentTypes;
import com.scholarscore.etl.powerschool.api.response.AssignmentScoresResponse;
import com.scholarscore.etl.powerschool.api.response.DistrictResponse;
import com.scholarscore.etl.powerschool.api.response.SchoolsResponse;
import com.scholarscore.etl.powerschool.api.response.SectionEnrollmentsResponse;
import com.scholarscore.etl.powerschool.api.response.SectionGradesResponse;
import com.scholarscore.etl.powerschool.api.response.SectionResponse;
import com.scholarscore.etl.powerschool.api.response.SectionScoreIdsResponse;
import com.scholarscore.etl.powerschool.api.response.StudentResponse;
import com.scholarscore.etl.powerschool.api.response.TermResponse;
import org.apache.http.HttpRequest;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * Created by mattg on 7/2/15.
 */
public class PowerSchoolClient extends BaseHttpClient implements IPowerSchoolClient {

    private static final String HEADER_AUTH_NAME = "Authorization";

    public static final String PATH_RESOURCE_DISTRICT = "/ws/v1/district";
    public static final String EXPANSION_RESOURCE_DISTRICT = "?expansions=district_race_codes,districts_of_residence,entry_codes,ethnicity_race_decline_to_specify,exit_codes,federal_race_categories,fees_payment_methods,scheduling_reporting_ethnicities,test_setup";

    public static final String PATH_RESOURCE_SCHOOL = "/ws/v1/district/school";
    public static final String EXPANSION_RESOURCE_SCHOOL = "?expansions=school_boundary,school_fees_setup";

    public static final String PATH_RESOURCE_STUDENT = "/ws/v1/school/{0}/student?expansions=addresses,alerts,contact,contact_info,demographics,ethnicity_race,fees,initial_enrollment,lunch,phones,schedule_setup,school_enrollment";
    public static final String PATH_RESOURCE_SINGLE_STUDENT = "/ws/v1/student/{0}?expansions=addresses,alerts,contact,contact_info,demographics,ethnicity_race,fees,initial_enrollment,lunch,phones,schedule_setup";
    public static final String PATH_RESOURCE_STAFF = "/ws/v1/school/{0}/staff";
    public static final String EXPANSION_RESOURCE_STAFF = "?expansions=phones,addresses,emails,school_affiliations";

    public static final String PATH_RESOURCE_COURSE = "/ws/v1/school/{0}/course";
    public static final String PATH_RESOURCE_TERMS = "/ws/v1/school/{0}/term";
    public static final String PATH_RESOURCE_SECTION = "/ws/v1/school/{0}/section";
    public static final String PATH_RESOURCE_SECTION_ENROLLMENT = "/ws/v1/section/{0}/section_enrollment";
    public static final String PATH_RESOURCE_SECTION_ASSIGNMENTS = "/ws/schema/table/PGAssignments?projection=Name,SectionID,AssignmentID,Description,DateDue,PointsPossible,Type,Weight,IncludeInFinalGrades,Abbreviation,PGCategoriesID,PublishScores,PublishState&q=SectionID=={0}";
    public static final String PATH_RESOURCE_SECTION_ASSIGNMENT_CATEGORY = "/ws/schema/table/pgcategories?q=SectionID=={0}&projection=Abbreviation,DCID,DefaultPtsPoss,Description,ID,Name,SectionID";
    public static final String PATH_RESOURCE_SECTION_SCORES = "/ws/schema/table/storedgrades?q=sectionid=={0}&projection=dcid,grade,datestored,studentid,sectionid,termid";
    public static final String PATH_RESOURCE_ASSIGNMENT_SCORES = "/ws/schema/table/SectionScoresAssignments?q=assignment=={0}&projection=*";
    public static final String PATH_RESOURCE_SECTION_SCORE_IDS = "/ws/schema/table/SectionScoresId?q=sectionid=={0}&projection=*";
    //PGScores: "/ws/schema/table/pgscores?projection=PGAssignmentsID,id,grade,dcid,comment_value,percent,percentstr,studentid"

    private static final String GRANT_TYPE_CREDS = "grant_type=client_credentials";
    private static final String URI_PATH_OATH = "/oauth/access_token";

    // Invoke a named query - the query name originates from the file myedpanel.named_queries.xml, {0} below is the table
    // name to query against
    private static final String PATH_NAMED_QUERY = "/ws/schema/query/com.edpanel.queries.{0}";
    private final String clientSecret;
    private final String clientId;

    private static final ObjectMapper mapper = new ObjectMapper();
    private OAuthResponse oauthToken;

    public PowerSchoolClient(String clientId, String clientSecret, URI uri) {
        super(uri);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        authenticate();
    }

    protected Gson createGsonClient() {
        return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    }

    public void authenticate() {
        try {
            HttpPost post = new HttpPost();
            post.setHeader(new BasicHeader(HEADER_CONTENT_TYPE_NAME, HEADER_CONTENT_TYPE_X_FORM_URLENCODED));
            post.setEntity(new StringEntity(GRANT_TYPE_CREDS));
            setupCommonHeaders(post);
            post.addHeader(BasicScheme.authenticate(
                    new UsernamePasswordCredentials(clientId, clientSecret),
                    "UTF-8", false));
            post.setURI(uri.resolve(URI_PATH_OATH));
            String json = getJSON(post);
            if (null != json) {
                oauthToken = mapper.readValue(json, OAuthResponse.class);
                if (null == oauthToken || null == oauthToken.access_token) {
                    throw new PowerSchoolClientException("Unable to authenticate with power school, response: " + json);
                }
            }
        } catch (IOException e) {
            throw new PowerSchoolClientException(e);
        }
    }

    @Override
    protected Boolean isAuthenticated() {
        // we're either authenticated, or we throw an exception during the constructor call...
        return true;
    }

    @Override
    public SchoolsResponse getSchools() {
        return get(SchoolsResponse.class, PATH_RESOURCE_SCHOOL + EXPANSION_RESOURCE_SCHOOL);
    }

    @Override
    public DistrictResponse getDistrict() {
        return get(DistrictResponse.class, PATH_RESOURCE_DISTRICT);
    }

    /**
     * Get a collection of staff by school
     *
     * @param schoolId
     *      The school identifier to retrieve the staff from within
     *
     * @return
     */
    @Override
    public PsStaffs getStaff(Long schoolId) {
        return getJackson(PsStaffs.class, PATH_RESOURCE_STAFF + EXPANSION_RESOURCE_STAFF, schoolId.toString());
    }

    protected <T> T getJackson(Class<T> clazz, String path, String ...params) {

        path = getPath(path, params);

        try {
            HttpGet get = new HttpGet();
            setupCommonHeaders(get);
            get.setURI(uri.resolve(path));
            String json = getJSON(get);
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new HttpClientException(e);
        }
    }

    public String executeNamedQuery(String tableName) {
        String path = getPath(PATH_NAMED_QUERY, tableName);
        String result = null;
        try {
            result = post("{ }".getBytes(), path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Generic transformer that converts a named query (table sturcture) into a object of type T which must extend
     * ApiModel.
     *
     * @param clazz
     *      The class to return
     *
     * @param tableName
     *      The table name to query
     *
     * @param deserializer
     *      The implementation of the deserializer which does the transformation direct to the API model
     *
     * @return
     *      A list of type T, for example if the table is a room, then each T is a room but running a named query
     *      returns multiple rooms thus this method returns List<T>
     */
    @Override
    public <T> List<T> namedQuery(Class<T> clazz, String tableName, IDeserialize<T> deserializer) {
        String json = executeNamedQuery(tableName);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Object.class, new NaturalDeserializer());
        Gson gson = gsonBuilder.create();

        Object natural = gson.fromJson(json, Object.class);

        return deserializer.deserialize(clazz, natural);
    }

    @Override
    public PsStudents getStudentsBySchool(Long schoolId) {
        return getJackson(PsStudents.class, PATH_RESOURCE_STUDENT, schoolId.toString());
    }

    @Override
    public StudentResponse getStudentById(Long studentId) {
        return get(StudentResponse.class, PATH_RESOURCE_SINGLE_STUDENT, studentId.toString());
    }
    @Override
    public PsCourses getCoursesBySchool(Long schoolId) {
        return getJackson(PsCourses.class, PATH_RESOURCE_COURSE, schoolId.toString());
    }

    @Override
    public SectionResponse getSectionsBySchoolId(Long schoolId) {
        return get(SectionResponse.class, PATH_RESOURCE_SECTION, schoolId.toString());
    }

    @Override
    public SectionEnrollmentsResponse getEnrollmentBySectionId(Long sectionId) {
        return get(SectionEnrollmentsResponse.class, PATH_RESOURCE_SECTION_ENROLLMENT, sectionId.toString());
    }

    @Override
    public SectionGradesResponse getSectionScoresBySectionId(Long sectionId) {
        return get(SectionGradesResponse.class, PATH_RESOURCE_SECTION_SCORES, sectionId.toString());
    }
    
    @Override
    public PGAssignments getAssignmentsBySectionId(Long sectionId) {
        return get(PGAssignments.class, PATH_RESOURCE_SECTION_ASSIGNMENTS, sectionId.toString()); 
    }

    @Override
    public PGAssignmentTypes getAssignmentTypesBySectionId(Long sectionId) {
        return get(PGAssignmentTypes.class, PATH_RESOURCE_SECTION_ASSIGNMENT_CATEGORY, sectionId.toString());
    }

    @Override
    public AssignmentScoresResponse getStudentScoresByAssignmentId(Long assignmentId) {
        return get(AssignmentScoresResponse.class, PATH_RESOURCE_ASSIGNMENT_SCORES, 3, assignmentId.toString());
    }

    @Override
    public SectionScoreIdsResponse getStudentScoreIdsBySectionId(Long sectionId) {
        return get(SectionScoreIdsResponse.class, PATH_RESOURCE_SECTION_SCORE_IDS, sectionId.toString());
    }

    public Object getAsMap(String path) {
        return get(Object.class, path);
    }

    @Override
    public TermResponse getTermsBySchoolId(Long schoolId) {
        return get(TermResponse.class, PATH_RESOURCE_TERMS, schoolId.toString());
    }

    protected void setupCommonHeaders(HttpRequest req) {
        super.setupCommonHeaders(req);
        if (null != oauthToken) {
            req.setHeader(new BasicHeader(HEADER_AUTH_NAME, oauthToken.token_type + " " + oauthToken.access_token));
        }
    }
}
