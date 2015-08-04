package com.scholarscore.etl.powerschool.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.scholarscore.client.BaseHttpClient;
import com.scholarscore.client.HttpClientException;
import com.scholarscore.etl.powerschool.api.auth.OAuthResponse;
import com.scholarscore.etl.powerschool.api.model.Staffs;
import com.scholarscore.etl.powerschool.api.model.Students;
import com.scholarscore.etl.powerschool.api.response.*;
import org.apache.http.HttpRequest;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.net.URI;

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

    public static final String PATH_RESOURCE_STAFF = "/ws/v1/school/{0}/staff";
    public static final String EXPANSION_RESOURCE_STAFF = "?expansions=phones,addresses,emails,school_affiliations";

    public static final String PATH_RESOURCE_COURSE = "/ws/v1/school/{0}/course";
    public static final String PATH_RESOURCE_TERMS = "/ws/v1/school/{0}/term";
    public static final String PATH_RESOURCE_SECTION = "/ws/v1/school/{0}/section?expansions=term";

    private static final String GRANT_TYPE_CREDS = "grant_type=client_credentials";
    private static final String URI_PATH_OATH = "/oauth/access_token";
    private final String clientSecret;
    private final String clientId;

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
                oauthToken = gson.fromJson(json, OAuthResponse.class);
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
    public Staffs getStaff(Long schoolId) {
        return getJackson(Staffs.class, PATH_RESOURCE_STAFF + EXPANSION_RESOURCE_STAFF, schoolId.toString());
    }

    protected <T> T getJackson(Class<T> clazz, String path, String ...params) {

        path = getPath(path, params);

        try {
            HttpGet get = new HttpGet();
            setupCommonHeaders(get);
            get.setURI(uri.resolve(path));
            String json = getJSON(get);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new HttpClientException(e);
        }
    }

    @Override
    public Students getStudentsBySchool(Long schoolId) {
        return getJackson(Students.class, PATH_RESOURCE_STUDENT, schoolId.toString());
    }

    @Override
    public CourseResponse getCoursesBySchool(Long schoolId) {
        return get(CourseResponse.class, PATH_RESOURCE_COURSE, schoolId.toString());
    }

    public void getSectionsBySchool(Long schoolId) {
        get(SectionResponse.class, PATH_RESOURCE_SECTION, schoolId.toString());
    }

    public Object getAsMap(String path) {
        return get(Object.class, path);
    }

    @Override
    public TermResponse getTermsBySchoolId(Long schoolId) {
        return get(TermResponse.class, PATH_RESOURCE_TERMS, schoolId.toString());
    }

    @Override
    public SectionResponse getSectionsBySchoolId(Long schoolId) {
        return get(SectionResponse.class, PATH_RESOURCE_SECTION, schoolId.toString());
    }

    protected void setupCommonHeaders(HttpRequest req) {
        super.setupCommonHeaders(req);
        if (null != oauthToken) {
            req.setHeader(new BasicHeader(HEADER_AUTH_NAME, oauthToken.token_type + " " + oauthToken.access_token));
        }
    }
}
