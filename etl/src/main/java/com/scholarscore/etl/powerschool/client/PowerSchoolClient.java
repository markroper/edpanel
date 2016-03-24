package com.scholarscore.etl.powerschool.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.client.HttpClientException;
import com.scholarscore.etl.powerschool.api.auth.OAuthResponse;
import com.scholarscore.etl.powerschool.api.model.PsCourses;
import com.scholarscore.etl.powerschool.api.model.PsPeriodWrapper;
import com.scholarscore.etl.powerschool.api.model.PsStaffs;
import com.scholarscore.etl.powerschool.api.model.assignment.PsAssignmentWrapper;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsAssignmentScoreWrapper;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PsSectionScoreIdWrapper;
import com.scholarscore.etl.powerschool.api.model.assignment.scores.PtFinalScoreWrapper;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PsAssignmentTypeWrapper;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PtAssignmentCategoryWrapper;
import com.scholarscore.etl.powerschool.api.model.attendance.PsAttendanceCodeWrapper;
import com.scholarscore.etl.powerschool.api.model.attendance.PsAttendanceWrapper;
import com.scholarscore.etl.powerschool.api.model.attendance.PsCalendarDayWrapper;
import com.scholarscore.etl.powerschool.api.model.cycles.PsCycleWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PsFinalGradeSetupWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PsGradeFormulaWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGradeFormulaWeightingWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PsSectionGradeWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PtSectionEnrollmentWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PtSectionMapWrapper;
import com.scholarscore.etl.powerschool.api.model.section.PtTermWrapper;
import com.scholarscore.etl.powerschool.api.model.student.PsStudents;
import com.scholarscore.etl.powerschool.api.model.student.PsTableStudentWrapper;
import com.scholarscore.etl.powerschool.api.model.student.PtPsStudentMapWrapper;
import com.scholarscore.etl.powerschool.api.model.term.PsTermBinWrapper;
import com.scholarscore.etl.powerschool.api.model.term.PtPsTermBinReportingTermWrapper;
import com.scholarscore.etl.powerschool.api.model.term.PtPsTermMapWrapper;
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
import java.time.LocalDate;

/**
 * Created by mattg on 7/2/15.
 */
public class PowerSchoolClient extends PowerSchoolHttpClient implements IPowerSchoolClient {
    private PowerSchoolPaths paths = new PowerSchoolPaths();
    protected static final Integer PAGE_SIZE = 250;
    private static final String HEADER_AUTH_NAME = "Authorization";
    private static final String PATH_RESOURCE_DISTRICT = "/ws/v1/district";
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
        paths.setCutoffDate(LocalDate.now().minusYears(1l));
        paths.setPageSize(PAGE_SIZE);
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
                oauthToken = MAPPER.readValue(json, OAuthResponse.class);
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
    public void setSyncCutoff(LocalDate date) {
        paths.setCutoffDate(date);
    }

    @Override
    public SchoolsResponse getSchools() throws HttpClientException {
        return get(SchoolsResponse.class, paths.getSchoolPath());
    }

    @Override
    public PsResponse<PsPeriodWrapper> getPeriodsBySchool(Long schoolId) throws HttpClientException {
        return get(new TypeReference<PsResponse<PsPeriodWrapper>>(){},
                paths.getPeriodPath(),
                PAGE_SIZE,
                schoolId.toString());
    }

    @Override
    public DistrictResponse getDistrict() throws HttpClientException {
        return get(DistrictResponse.class, PATH_RESOURCE_DISTRICT);
    }

    @Override
    public PsStaffs getStaff(Long schoolId) throws HttpClientException {
        return get(PsStaffs.class, paths.getStaffPath(), schoolId.toString());
    }

    @Override
    public PsStudents getStudentsBySchool(Long schoolId) throws HttpClientException {
        return get(
                new TypeReference<PsStudents>() {},
                paths.getStudentsPath(),
                PAGE_SIZE,
                schoolId.toString());
    }

    @Override
    public PsResponse<PsTableStudentWrapper> getTableStudents() throws HttpClientException {
        return get(
                new TypeReference<PsResponse<PsTableStudentWrapper>>() {},
                paths.getStudentsFromTablePath(),
                PAGE_SIZE,
                (String[]) null);
    }

    @Override
    public StudentResponse getStudentById(Long studentId) throws HttpClientException {
        return get(StudentResponse.class, paths.getStudentPath(), studentId.toString());
    }
    @Override
    public PsCourses getCoursesBySchool(Long schoolId) throws HttpClientException {
        return get(
                new TypeReference<PsCourses>() {},
                paths.getCoursePath(),
                PAGE_SIZE, 
                schoolId.toString());
    }

    @Override
    public PsResponse<PsCycleWrapper> getCyclesBySchool(Long schoolId) throws HttpClientException {
        return get(new TypeReference<PsResponse<PsCycleWrapper>>() {},
        paths.getCyclePath(), PAGE_SIZE, schoolId.toString());
    }

    @Override
    public SectionResponse getSectionsBySchoolId(Long schoolId) throws HttpClientException {
        return get(SectionResponse.class, paths.getSectionPath(), schoolId.toString());
    }

    @Override
    public SectionEnrollmentsResponse getEnrollmentBySectionId(Long sectionId) throws HttpClientException {
        return get(SectionEnrollmentsResponse.class, paths.getSectionEnrollmentPath(), sectionId.toString());
    }

    @Override
    public PsResponse<PsSectionGradeWrapper> getSectionScoresBySectionId(Long sectionId) throws HttpClientException {
        return get(new TypeReference<PsResponse<PsSectionGradeWrapper>>(){},
                paths.getSectionScoresPath(),
                PAGE_SIZE,
                sectionId.toString());
    }
    
    @Override
    public PsResponse<PsAssignmentWrapper> getAssignmentsBySectionId(Long sectionId) throws HttpClientException {
        return get(new TypeReference<PsResponse<PsAssignmentWrapper>>(){},
                paths.getSectionAssignmentsPath(),
                PAGE_SIZE,
                sectionId.toString());
    }

    @Override
    public PsResponse<PsAssignmentTypeWrapper> getAssignmentCategoriesBySectionId(Long sectionId) throws HttpClientException {
        return get(new TypeReference<PsResponse<PsAssignmentTypeWrapper>>(){},
                paths.getSectionAssignmentCategories(),
                PAGE_SIZE,
                sectionId.toString());
    }

    @Override
    public PsResponse<PtAssignmentCategoryWrapper> getPowerTeacherAssignmentCategory() throws HttpClientException {
        return get(new TypeReference<PsResponse<PtAssignmentCategoryWrapper>>(){},
                paths.getPowerTeacherAssignmentCategories(),
                PAGE_SIZE,
                (String[]) null);
    }

    @Override
    public PsResponse<PsAssignmentScoreWrapper> getStudentScoresByAssignmentId(Long assignmentId) throws HttpClientException {
        return get(
                new TypeReference<PsResponse<PsAssignmentScoreWrapper>>(){},
                paths.getAssignmentScores(),
                PAGE_SIZE,
                assignmentId.toString());
    }

    @Override
    public PsResponse<PsSectionScoreIdWrapper> getStudentScoreIdsBySectionId(Long sectionId) throws HttpClientException {
        return get(new TypeReference<PsResponse<PsSectionScoreIdWrapper>>() {},
                paths.getSectionScoreIds(),
                PAGE_SIZE,
                sectionId.toString());
    }

    @Override
    public PsResponse<PsCalendarDayWrapper> getSchoolCalendarDays(Long schoolId) throws HttpClientException {
        return get(
                new TypeReference<PsResponse<PsCalendarDayWrapper>>(){},
                paths.getCalendarDayPath(),
                PAGE_SIZE,
                schoolId.toString());
    }

    @Override
    public PsResponse<PsAttendanceWrapper> getStudentAttendance(Long studentId) throws HttpClientException {
        return get(
                new TypeReference<PsResponse<PsAttendanceWrapper>>(){},
                paths.getAttendancePath(),
                PAGE_SIZE,
                studentId.toString());
    }

    @Override
    public PsResponse<PsAttendanceCodeWrapper> getAttendanceCodes() throws HttpClientException {
        return get(
                new TypeReference<PsResponse<PsAttendanceCodeWrapper>>(){},
                paths.getAttendanceCodePath(),
                PAGE_SIZE,
                (String[]) null);
    }

    @Override
    public PsResponse<PsFinalGradeSetupWrapper> getFinalGradeSetups() throws HttpClientException {
        return get(new TypeReference<PsResponse<PsFinalGradeSetupWrapper>>() {},
                paths.getSectionGradesSetupPath(),
                PAGE_SIZE,
                (String[]) null);
    }

    @Override
    public PsResponse<PtSectionMapWrapper> getPowerTeacherSectionMapping(Long sourceSectionId) throws HttpClientException {
        return get(new TypeReference<PsResponse<PtSectionMapWrapper>>() {},
                paths.getPowerTeacherSectionPath(sourceSectionId),
                PAGE_SIZE,
                (String[]) null);
    }

    @Override
    public PsResponse<PtSectionMapWrapper> getPowerTeacherSectionMappings() throws HttpClientException {
        return get(new TypeReference<PsResponse<PtSectionMapWrapper>>() {},
                paths.getPowerTeacherSectionMappingPath(),
                PAGE_SIZE,
                (String[]) null);
    }

    @Override
    public PsResponse<PtPsTermMapWrapper> getPowerTeacherTermMappings() throws HttpClientException {
        return get(new TypeReference<PsResponse<PtPsTermMapWrapper>>() {},
                paths.getPowerTeacherTermMappingPath(),
                PAGE_SIZE,
                (String[]) null);
    }

    @Override
    public PsResponse<PtPsTermBinReportingTermWrapper> getPowerTeacherTermBinMappings() throws HttpClientException {
        return get(new TypeReference<PsResponse<PtPsTermBinReportingTermWrapper>>() {},
                paths.getPowerTeacherTermnBinMappingPath(),
                PAGE_SIZE,
                (String[]) null);
    }

    @Override
    public PsResponse<PtPsStudentMapWrapper> getPowerTeacherStudentMappings() throws HttpClientException {
        return get(new TypeReference<PsResponse<PtPsStudentMapWrapper>>() {},
                paths.getPowerTeacherStudentMappings(),
                PAGE_SIZE,
                (String[]) null);
    }

    @Override
    public PsResponse<PtSectionEnrollmentWrapper> getPowerTeacherSectionEnrollments(Long ptSectionId) throws HttpClientException {
        return get(new TypeReference<PsResponse<PtSectionEnrollmentWrapper>>() {},
                paths.getPowerTeacherSectionEnrollment(ptSectionId),
                PAGE_SIZE,
                (String[]) null);
    }

    @Override
    public PsResponse<PtFinalScoreWrapper> getPowerTeacherFinalScore(Long ptSectionEnrollmentId) throws HttpClientException {
        return get(new TypeReference<PsResponse<PtFinalScoreWrapper>>() {},
                paths.getPowerTeacherFinalScores(ptSectionEnrollmentId),
                PAGE_SIZE,
                (String[]) null);
    }

    @Override
    public PsResponse<PtTermWrapper> getPowerTeacherTerm(Long powerTeacherTermId) throws HttpClientException {
        return get(new TypeReference<PsResponse<PtTermWrapper>>() {},
                paths.getPowerTeacherTermPath(powerTeacherTermId),
                PAGE_SIZE,
                (String[]) null);
    }

    @Override
    public PsResponse<PsGradeFormulaWrapper> getGradeFormula(Long gradeFormulaId) throws HttpClientException {
        return get(new TypeReference<PsResponse<PsGradeFormulaWrapper>>() {},
                paths.getSectionGradeFormula(gradeFormulaId),
                PAGE_SIZE,
                (String[]) null);
    }

    @Override
    public PsResponse<PsSectionGradeFormulaWeightingWrapper> getGradeFormulaWeights(Long gradeFormulaId) throws HttpClientException {
        return get(new TypeReference<PsResponse<PsSectionGradeFormulaWeightingWrapper>>() {},
                paths.getSectionGradeFormulaWeights(gradeFormulaId),
                PAGE_SIZE,
                (String[]) null);
    }

    public Object getAsMap(String path) throws HttpClientException {
        return get(Object.class, path);
    }

    @Override
    public TermResponse getTermsBySchoolId(Long schoolId) throws HttpClientException {
        return get(TermResponse.class, paths.getTermPath(), schoolId.toString());
    }

    @Override
    public PsResponse<PsTermBinWrapper> getTermBins() throws HttpClientException {
        return get(new TypeReference<PsResponse<PsTermBinWrapper>>() {},
                paths.getTermBinPath(),
                PAGE_SIZE,
                (String[]) null);
    }

    protected void setupCommonHeaders(HttpRequest req) {
        super.setupCommonHeaders(req);
        if (null != oauthToken) {
            req.setHeader(new BasicHeader(HEADER_AUTH_NAME, oauthToken.token_type + " " + oauthToken.access_token));
        }
    }
}
