package com.scholarscore.etl.powerschool;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.etl.powerschool.client.PowerSchoolClient;
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
import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * User: jordan
 * Date: 2/2/16
 * Time: 3:09 PM
 */
public class TestPowerSchoolClient extends PowerSchoolClient {

    public TestPowerSchoolClient() {
        super(true, "fakeClientId", "fakeSecret", getURI(), "", "MA_PrimDisabil", "MA_EngProficiency", "Weighted_Added_Value");

        System.out.println("TestPowerSchoolClient initialized.");
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

    @Override
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
                
                // -- HIGH LEVEL API (/ws/v1/) requests -- 
                final String V1_BASE = "/ws/v1/";
                final String GET_DISTRICT_REQUEST_REGEX = V1_BASE + "district(\\?.*)?";
                final String GET_ALL_SCHOOLS_REQUEST_REGEX = V1_BASE + "district/school\\?.*";
                final String STUDENT_REQUEST_REGEX =  V1_BASE +"school/.*/student\\?.*";
                final String COURSE_REQUEST_REGEX =  V1_BASE + "school/.*/course\\?.*";
                final String SECTION_REQUEST_REGEX =  V1_BASE + "school/.*/section\\?.*";
                final String TERM_REQUEST_REGEX =  V1_BASE + "school/.*/term\\?.*";
                final String STAFF_REQUEST_REGEX =  V1_BASE + "school/.*/staff\\?.*";

                // -- LOW LEVEL API (/ws/schema/) requests --
                final String TABLE_BASE = "/ws/schema/table/";
                final String PGASSIGNMENTS_BY_SECTION_REQUEST_REGEX = TABLE_BASE + "pgassignments\\?.*"
                        + "pagesize=.*"
                        + "&projection=.*"
                        + "&q=sectionid==.*"
                        + "&page=.*";
                final String STORED_GRADES_BY_SECTION_REQUEST_REGEX = TABLE_BASE + "storedgrades\\?" 
                        + "pagesize=.*" 
                        + "&q=sectionid==.*" 
                        + "&projection=.*" 
                        + "&page=.*";
                final String CYCLE_DAY_BY_SCHOOL_REQUEST_REGEX = TABLE_BASE + "cycle_day\\?"
                        + "projection=.*" 
                        + "&q=schoolid==.*" 
                        + "&page=.*";
                final String PERIOD_BY_SCHOOL_REQUEST_REGEX = TABLE_BASE + "period\\?" 
                        + "pagesize=.*" 
                        + "&projection=.*" 
                        + "&q=schoolid==.*" 
                        + "&page=.*";
                final String ALL_STUDENTS_REQUEST_REGEX = TABLE_BASE + "students\\?" 
                        + "projection=.*" 
                        + "&pagesize=.*" 
                        + "&page=.*";
                final String ALL_SECTIONS_REQUEST_REGEX = TABLE_BASE + "sections\\?"
                        + "projection=.*"
                        + "&pagesize=.*"
                        + "&page=.*";
                final String ALL_CLASSRANKS_REQUEST_REGEX = TABLE_BASE + "classrank\\?"
                       // + "q=gpa!=0;gpa!=" 
                        + "q=gpa\\!=0;dateranked=.*" 
                        + "&projection=.*"
                        + "&pagesize=.*" 
                        + "&page=.*";
                final String SYNC_SECTION_MAP_REQUEST_REGEX = TABLE_BASE + "sync_sectionmap\\?"
                        + "pagesize=.*"
                        + "&projection=.*"
                        + "&page=.*";
                final String SYNC_STUDENT_MAP_REQUEST_REGEX = TABLE_BASE + "sync_studentmap\\?"
                        + "projection=.*"
                        + "&pagesize=.*"
                        + "&page=.*";
                final String PSM_FINAL_GRADE_SETUP_REQUEST_REGEX = TABLE_BASE + "psm_finalgradesetup\\?" 
                        + "pagesize=.*" 
                        + "&projection=.*" 
                        + "&page=.*";
                final String PSM_ASSIGNMENT_CATEGORY_REQUEST_REGEX = TABLE_BASE + "psm_assignmentcategory\\?" 
                        + "projection=.*" 
                        + "&pagesize=.*" 
                        + "&page=.*";
                final String PSM_REPORTING_TERM_REQUEST_REGEX = TABLE_BASE + "psm_reportingterm\\?" 
                        + "pagesize=.*" 
                        + "&projection=.*" 
                        + "&q=id==.*"
                        + "&page=1";
                final String PSM_GRADING_FORMULA_WEIGHTING_REQUEST_REGEX = TABLE_BASE + "psm_gradingformulaweighting\\?" 
                        + "pagesize=.*" 
                        + "&projection=.*" 
                        + "&q=ParentGradingFormulaID==.*" 
                        + "&page=.*";
                final String PSM_SECTION_ENROLLMENT_REQUEST_REGEX = TABLE_BASE + "psm_sectionenrollment\\?" 
                        + "q=sectionid==.*" 
                        + "&projection=.*" 
                        + "&pagesize=.*" 
                        + "&page=.*";
                final String PSM_FINALSCORE_REQUEST_REGEX = TABLE_BASE + "psm_finalscore\\?" 
                        + "q=sectionenrollmentid==.*" 
                        + "&projection=.*" 
                        + "&pagesize=.*" 
                        + "&page=.*";
                final String PG_CATEGORIES_BY_SECTION_REQUEST_REGEX = TABLE_BASE + "pgcategories\\?" 
                        + "q=SectionID==.*" 
                        + "&pagesize=.*" 
                        + "&projection=.*"
                        + "&page=.*";
                final String SECTION_SCORE_ID_BY_SECTION_REQUEST_REGEX = TABLE_BASE + "sectionscoresid\\?" 
                        + "pagesize=.*" 
                        + "&q=sectionid==.*" 
                        + "&projection=.*" 
                        + "&page=.*";
                final String SECTION_SCORE_ASSIGNMENTS_BY_ASSIGNMENT_REQUEST_REGEX = TABLE_BASE + "sectionscoresassignments\\?" 
                        + "pagesize=.*&q=assignment==.*" 
                        + "&projection=.*" 
                        + "&page=.*";
                final String CALENDAR_DAY_BY_SCHOOL_REQUEST_REGEX = TABLE_BASE + "calendar_day\\?" 
                        + "pagesize=.*" 
                        + "&projection=.*" 
                        + "&q=schoolid==.*;insession==1" 
                        + "&page=.*";
                final String ATTENDANCE_CODE_REQUEST_REGEX = TABLE_BASE + "attendance_code\\?" 
                        + "pagesize=.*" 
                        + "&projection=.*" 
                        + "&page=.*";
                final String ATTENDANCE_BY_STUDENT = TABLE_BASE + "attendance\\?"
                        + "pagesize=.*"
                        + "&projection=.*"
                        + "&q=studentid==.*;att_date=gt=.*"
                        + "&page=.*";
                
                regexToResponseNames.put(GET_DISTRICT_REQUEST_REGEX, "district");
                regexToResponseNames.put(GET_ALL_SCHOOLS_REQUEST_REGEX, "all_schools");
                regexToResponseNames.put(STUDENT_REQUEST_REGEX, "student");
                regexToResponseNames.put(COURSE_REQUEST_REGEX, "course");
                regexToResponseNames.put(SECTION_REQUEST_REGEX, "section");
                regexToResponseNames.put(TERM_REQUEST_REGEX, "term");
                regexToResponseNames.put(STAFF_REQUEST_REGEX, "staff");
                regexToResponseNames.put(PGASSIGNMENTS_BY_SECTION_REQUEST_REGEX, "pgassignments_by_section");
                regexToResponseNames.put(STORED_GRADES_BY_SECTION_REQUEST_REGEX, "storedgrades_by_section");
                regexToResponseNames.put(CYCLE_DAY_BY_SCHOOL_REQUEST_REGEX, "cycleday_by_school");
                regexToResponseNames.put(PERIOD_BY_SCHOOL_REQUEST_REGEX, "period_by_school");
                regexToResponseNames.put(ALL_STUDENTS_REQUEST_REGEX, "all_students");
                regexToResponseNames.put(ALL_SECTIONS_REQUEST_REGEX, "all_sections_dcid_id");
                regexToResponseNames.put(SYNC_SECTION_MAP_REQUEST_REGEX, "sync_sectionmap");
                regexToResponseNames.put(SYNC_STUDENT_MAP_REQUEST_REGEX, "sync_studentmap");
                regexToResponseNames.put(PSM_FINAL_GRADE_SETUP_REQUEST_REGEX, "psm_finalgradesetup");
                regexToResponseNames.put(PSM_ASSIGNMENT_CATEGORY_REQUEST_REGEX, "psm_assignmentcategory");
                regexToResponseNames.put(PSM_REPORTING_TERM_REQUEST_REGEX, "psm_reportingterm");
                regexToResponseNames.put(PSM_GRADING_FORMULA_WEIGHTING_REQUEST_REGEX, "psm_gradingformula");
                regexToResponseNames.put(PSM_SECTION_ENROLLMENT_REQUEST_REGEX, "psm_sectionenrollment");
                regexToResponseNames.put(PSM_FINALSCORE_REQUEST_REGEX, "psm_finalscore");
                regexToResponseNames.put(PG_CATEGORIES_BY_SECTION_REQUEST_REGEX, "pg_categories_by_section");
                regexToResponseNames.put(SECTION_SCORE_ID_BY_SECTION_REQUEST_REGEX, "sectionscoreid_by_section");
                regexToResponseNames.put(SECTION_SCORE_ASSIGNMENTS_BY_ASSIGNMENT_REQUEST_REGEX, "section_scores_by_assignment");
                // kinda cheating -- making these empty for now
                regexToResponseNames.put(ALL_CLASSRANKS_REQUEST_REGEX, "all_classranks_empty");
                regexToResponseNames.put(CALENDAR_DAY_BY_SCHOOL_REQUEST_REGEX, "calendar_day_empty");
                regexToResponseNames.put(ATTENDANCE_CODE_REQUEST_REGEX, "attendance_code");
                regexToResponseNames.put(ATTENDANCE_BY_STUDENT, "attendance_by_student");
                
                HashMap<String, String> params = new HashMap<>();
                if (uriString != null) {
                    String lowerString = uriString.toLowerCase();
                    for (String regex : regexToResponseNames.keySet()) {
                        if (lowerString.matches(regex.toLowerCase())) {
                            String pageString = "";
                            String parsedPageNum = parseGetParamValue(lowerString, "page");
                            if (parsedPageNum != null && Integer.parseInt(parsedPageNum) > 1) {
                                pageString = "_p" + parsedPageNum;
                            }
                            
                            // one equals will be added normally, override here and add two as the value we're looking for is within query
                            String sectionId = parseGetParamValue(lowerString, "sectionid==");
                            if (sectionId != null) { params.put("\\{" + "SECTION_ID" + "\\}", sectionId); }
                            
                            String responseName = regexToResponseNames.get(regex);
                            filename = "mock-responses/" + responseName + pageString + "_response.json";
                        }
                    }
                }
                if (filename == null) {
                    // throw an exception because the real code will throw an NPE in this case anyway
                    throw new RuntimeException("Unsupported URI String: " + uriString);
                }
                return buildMockHttpResponseWithAnswerFromFile(filename, params);
            };

        CloseableHttpClient mockedClient = Mockito.mock(CloseableHttpClient.class);
        try {
            when(mockedClient.execute(any(HttpUriRequest.class))).thenAnswer(httpResponseAnswer);
        } catch (IOException e) {
            throw new HttpClientException(e);
        }
        return mockedClient;
    }
    
    private String parseGetParamValue(String wholeString, String paramToFind) { 
        if (wholeString == null || paramToFind == null) { return null; }
        String param = paramToFind;
        if (param.lastIndexOf("=") != param.length()-1) {
            // equals not found at end, add!
            param += "=";
        }
        if (wholeString.toLowerCase().contains(param)) {
            return wholeString.toLowerCase().replaceAll(".*" + param + "([a-zA-Z0-9]+).*", "$1");
        } else { 
            return null; 
        }
    }

    private CloseableHttpResponse buildMockHttpResponseWithAnswerFromFile(String filename) {
        return buildMockHttpResponseWithAnswerFromFile(filename, null);
    }

    private CloseableHttpResponse buildMockHttpResponseWithAnswerFromFile(String filename, HashMap<String, String> regexAndReplacement) {
        CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
//        Mockito.mock(CloseableHttpResponse.class, withSettings().defaultAnswer())
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "FINE!"));

        String fileContents = getClasspathFileAsString(filename);
        if (regexAndReplacement != null && regexAndReplacement.size() > 0) {
            // substitute params 
            for (String regex : regexAndReplacement.keySet()) {
                fileContents = fileContents.replaceAll(regex, regexAndReplacement.get(regex));
            }
        }
        final StringEntity stringEntity = buildStringEntity(fileContents);
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

    @Override
    public void authenticate() { }
    
}
