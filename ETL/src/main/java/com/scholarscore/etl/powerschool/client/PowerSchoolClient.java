package com.scholarscore.etl.powerschool.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholarscore.client.HttpClientException;
import com.scholarscore.etl.powerschool.api.auth.OAuthResponse;
import com.scholarscore.etl.powerschool.api.model.PsCourses;
import com.scholarscore.etl.powerschool.api.model.PsStaffs;
import com.scholarscore.etl.powerschool.api.model.PsStudents;
import com.scholarscore.etl.powerschool.api.model.assignment.PsAssignmentWrapper;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsAssignmentScoreWrapper;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsSectionScoreIdWrapper;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PsAssignmentTypeWrapper;
import com.scholarscore.etl.powerschool.api.model.attendance.PsAttendanceCodeWrapper;
import com.scholarscore.etl.powerschool.api.model.attendance.PsAttendanceWrapper;
import com.scholarscore.etl.powerschool.api.model.attendance.PsCalendarDayWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGradeWrapper;
import com.scholarscore.etl.powerschool.api.response.DistrictResponse;
import com.scholarscore.etl.powerschool.api.response.PsResponse;
import com.scholarscore.etl.powerschool.api.response.SchoolsResponse;
import com.scholarscore.etl.powerschool.api.response.SectionEnrollmentsResponse;
import com.scholarscore.etl.powerschool.api.response.SectionResponse;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mattg on 7/2/15.
 */
public class PowerSchoolClient extends PowerSchoolHttpClient implements IPowerSchoolClient {
    private static final Integer PAGE_SIZE = 1000;
    private static String ATTENDANCE_START_DATE = "2015-08-01";
    private static final String PAGE_SIZE_PARAM = "pagesize=" + PAGE_SIZE;
    private static final String PAGE_NUM_PARAM = "page={0}";
    private static final String BASE = "/ws/v1";
    private static final String SCHEMA_BASE = "/ws/schema/table";
    private static final String HEADER_AUTH_NAME = "Authorization";
    public static final String PATH_RESOURCE_DISTRICT = "/ws/v1/district";
    public static final String EXPANSION_RESOURCE_DISTRICT = "?expansions=district_race_codes,districts_of_residence,entry_codes,ethnicity_race_decline_to_specify,exit_codes,federal_race_categories,fees_payment_methods,scheduling_reporting_ethnicities,test_setup";
    public static final String PATH_RESOURCE_SCHOOL = "/ws/v1/district/school";
    public static final String EXPANSION_RESOURCE_SCHOOL = "?expansions=school_boundary,school_fees_setup";
    public static final String PATH_RESOURCE_STUDENT =
            BASE +
            "/school/{0}/student?pagesize=" +
            PAGE_SIZE +
            "&expansions=addresses,alerts,contact,contact_info,demographics,ethnicity_race,fees,initial_enrollment,lunch,phones,schedule_setup,school_enrollment";
    public static final String PATH_RESOURCE_SINGLE_STUDENT =
            BASE +
            "/student/{0}?expansions=addresses,alerts,contact,contact_info,demographics,ethnicity_race,fees,initial_enrollment,lunch,phones,schedule_setup";
    //Attendance related
    public static final String PATH_RESOUCE_CALENDAR_DAY =
            SCHEMA_BASE +
            "/calendar_day?" +
            PAGE_SIZE_PARAM +
            "&" + PAGE_NUM_PARAM +
            "&projection=dcid,date_value,insession,note,membershipvalue,scheduleid,schoolid,type,id" +
            "&q=schoolid=={1};date_value=gt=" + ATTENDANCE_START_DATE + ";insession==1";
    public static final String PATH_RESOURCE_ATTENDANCE =
            SCHEMA_BASE +
            "/attendance?" +
            PAGE_SIZE_PARAM +
            "&" + PAGE_NUM_PARAM +
            "&projection=*&q=studentid=={1};Att_Mode_Code!=ATT_ModeMeeting;att_date=gt="+ ATTENDANCE_START_DATE;
    public static final String PATH_RESOURCE_ATTENDANCE_CODE =
            SCHEMA_BASE +
            "/attendance_code?" +
            PAGE_SIZE_PARAM +
            "&" + PAGE_NUM_PARAM +
            "&projection=*";

    public static final String PATH_RESOURCE_STAFF = BASE + "/school/{0}/staff?" + PAGE_SIZE_PARAM;
    public static final String EXPANSION_RESOURCE_STAFF = "&expansions=phones,addresses,emails,school_affiliations";
    public static final String PATH_RESOURCE_COURSE = BASE + "/school/{0}/course?" + PAGE_SIZE_PARAM;
    public static final String PATH_RESOURCE_TERMS = BASE + "/school/{0}/term?" + PAGE_SIZE_PARAM;
    public static final String PATH_RESOURCE_SECTION = BASE + "/school/{0}/section?" + PAGE_SIZE_PARAM;
    public static final String PATH_RESOURCE_SECTION_ENROLLMENT = BASE + "/section/{0}/section_enrollment";
    public static final String PATH_RESOURCE_SECTION_ASSIGNMENTS =
            "/ws/schema/table/PGAssignments?" +
            PAGE_NUM_PARAM +
            "&" + PAGE_SIZE_PARAM +
            "&projection=Name,SectionID,AssignmentID,Description,DateDue,PointsPossible,Type,Weight,IncludeInFinalGrades,Abbreviation,PGCategoriesID,PublishScores,PublishState&q=SectionID=={1}";
    public static final String PATH_RESOURCE_SECTION_ASSIGNMENT_CATEGORY =
            SCHEMA_BASE +
            "/pgcategories?q=SectionID=={1}&"+
            PAGE_NUM_PARAM +
            "&projection=Abbreviation,DCID,DefaultPtsPoss,Description,ID,Name,SectionID";
    public static final String PATH_RESOURCE_SECTION_SCORES =
            SCHEMA_BASE +
            "/storedgrades?" +
            PAGE_NUM_PARAM +
            "&" + PAGE_SIZE_PARAM +
            "&q=sectionid=={1}&projection=dcid,grade,datestored,studentid,sectionid,termid";
    public static final String PATH_RESOURCE_ASSIGNMENT_SCORES =
            SCHEMA_BASE +
            "/SectionScoresAssignments?" +
            PAGE_NUM_PARAM +
            "&" + PAGE_SIZE_PARAM +
            "&q=assignment=={1}&projection=*";
    public static final String PATH_RESOURCE_SECTION_SCORE_IDS =
            SCHEMA_BASE +
            "/SectionScoresId?" +
            PAGE_NUM_PARAM +
            "&" + PAGE_SIZE_PARAM +
            "&q=sectionid=={1}&projection=*";
    private static final String GRANT_TYPE_CREDS = "grant_type=client_credentials";
    private static final String URI_PATH_OATH = "/oauth/access_token";
    private final String clientSecret;
    private final String clientId;

    private static final ObjectMapper mapper = new ObjectMapper();
    private OAuthResponse oauthToken;

    public PowerSchoolClient(String clientId, String clientSecret, URI uri) {
        super(uri);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        authenticate();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1); // to get previous year add -1
        Date lastYear = cal.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        ATTENDANCE_START_DATE = format.format(lastYear);
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
    public SchoolsResponse getSchools() throws HttpClientException {
        return get(SchoolsResponse.class, PATH_RESOURCE_SCHOOL + EXPANSION_RESOURCE_SCHOOL);
    }

    @Override
    public DistrictResponse getDistrict() throws HttpClientException {
        return get(DistrictResponse.class, PATH_RESOURCE_DISTRICT);
    }

    @Override
    public PsStaffs getStaff(Long schoolId) throws HttpClientException {
        return getJackson(PsStaffs.class, PATH_RESOURCE_STAFF + EXPANSION_RESOURCE_STAFF, schoolId.toString());
    }

    protected <T> T getJackson(Class<T> clazz, String path, String ...params) throws HttpClientException {

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

    @Override
    public PsStudents getStudentsBySchool(Long schoolId) throws HttpClientException {
        return getJackson(PsStudents.class, PATH_RESOURCE_STUDENT, schoolId.toString());
    }

    @Override
    public StudentResponse getStudentById(Long studentId) throws HttpClientException {
        return get(StudentResponse.class, PATH_RESOURCE_SINGLE_STUDENT, studentId.toString());
    }
    @Override
    public PsCourses getCoursesBySchool(Long schoolId) throws HttpClientException {
        return getJackson(PsCourses.class, PATH_RESOURCE_COURSE, schoolId.toString());
    }

    @Override
    public SectionResponse getSectionsBySchoolId(Long schoolId) throws HttpClientException {
        return get(SectionResponse.class, PATH_RESOURCE_SECTION, schoolId.toString());
    }

    @Override
    public SectionEnrollmentsResponse getEnrollmentBySectionId(Long sectionId) throws HttpClientException {
        return get(SectionEnrollmentsResponse.class, PATH_RESOURCE_SECTION_ENROLLMENT, sectionId.toString());
    }

    @Override
    public PsResponse<PsSectionGradeWrapper> getSectionScoresBySectionId(Long sectionId) throws HttpClientException {
        return get(new TypeReference<PsResponse<PsSectionGradeWrapper>>(){},
                PATH_RESOURCE_SECTION_SCORES,
                PAGE_SIZE,
                sectionId.toString());
    }
    
    @Override
    public PsResponse<PsAssignmentWrapper> getAssignmentsBySectionId(Long sectionId) throws HttpClientException {
        return get(new TypeReference<PsResponse<PsAssignmentWrapper>>(){},
                PATH_RESOURCE_SECTION_ASSIGNMENTS,
                PAGE_SIZE,
                sectionId.toString());
    }

    @Override
    public PsResponse<PsAssignmentTypeWrapper> getAssignmentTypesBySectionId(Long sectionId) throws HttpClientException {
        return get(new TypeReference<PsResponse<PsAssignmentTypeWrapper>>(){},
                PATH_RESOURCE_SECTION_ASSIGNMENT_CATEGORY,
                PAGE_SIZE,
                sectionId.toString());
    }

    @Override
    public PsResponse<PsAssignmentScoreWrapper> getStudentScoresByAssignmentId(Long assignmentId) throws HttpClientException {
        return get(
                new TypeReference<PsResponse<PsAssignmentScoreWrapper>>(){},
                PATH_RESOURCE_ASSIGNMENT_SCORES,
                PAGE_SIZE,
                assignmentId.toString());
    }

    @Override
    public PsResponse<PsSectionScoreIdWrapper> getStudentScoreIdsBySectionId(Long sectionId) throws HttpClientException {
        return get(new TypeReference<PsResponse<PsSectionScoreIdWrapper>>() {},
                PATH_RESOURCE_SECTION_SCORE_IDS,
                PAGE_SIZE,
                sectionId.toString());
    }

    @Override
    public PsResponse<PsCalendarDayWrapper> getSchoolCalendarDays(Long schoolId) throws HttpClientException {
        return get(
                new TypeReference<PsResponse<PsCalendarDayWrapper>>(){},
                PATH_RESOUCE_CALENDAR_DAY,
                PAGE_SIZE,
                schoolId.toString());
    }

    @Override
    public PsResponse<PsAttendanceWrapper> getStudentAttendance(Long studentId) throws HttpClientException {
        return get(
                new TypeReference<PsResponse<PsAttendanceWrapper>>(){},
                PATH_RESOURCE_ATTENDANCE,
                PAGE_SIZE,
                studentId.toString());
    }

    @Override
    public PsResponse<PsAttendanceCodeWrapper> getAttendanceCodes() throws HttpClientException {
        return get(
                new TypeReference<PsResponse<PsAttendanceCodeWrapper>>(){},
                PATH_RESOURCE_ATTENDANCE_CODE,
                PAGE_SIZE,
                (String[]) null);
    }

    public Object getAsMap(String path) throws HttpClientException {
        return get(Object.class, path);
    }

    @Override
    public TermResponse getTermsBySchoolId(Long schoolId) throws HttpClientException {
        return get(TermResponse.class, PATH_RESOURCE_TERMS, schoolId.toString());
    }

    protected void setupCommonHeaders(HttpRequest req) {
        super.setupCommonHeaders(req);
        if (null != oauthToken) {
            req.setHeader(new BasicHeader(HEADER_AUTH_NAME, oauthToken.token_type + " " + oauthToken.access_token));
        }
    }
}
