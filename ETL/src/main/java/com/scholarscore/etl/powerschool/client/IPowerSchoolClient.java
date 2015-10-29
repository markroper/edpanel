package com.scholarscore.etl.powerschool.client;

import com.scholarscore.client.HttpClientException;
import com.scholarscore.etl.powerschool.api.deserializers.IDeserialize;
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

import java.io.IOException;
import java.util.List;

/**
 * Created by mattg on 7/2/15.
 */
public interface IPowerSchoolClient {
    SchoolsResponse getSchools() throws HttpClientException;

    DistrictResponse getDistrict() throws HttpClientException;

    PsStaffs getStaff(Long schoolId) throws HttpClientException;

    PsStudents getStudentsBySchool(Long schoolId) throws HttpClientException;

    StudentResponse getStudentById(Long studentId) throws HttpClientException;

    PsCourses getCoursesBySchool(Long schoolId) throws HttpClientException;

    Object getAsMap(String path) throws HttpClientException;

    TermResponse getTermsBySchoolId(Long schoolId) throws HttpClientException;

    SectionResponse getSectionsBySchoolId(Long schoolId) throws HttpClientException;
    
    SectionEnrollmentsResponse getEnrollmentBySectionId(Long sectionId) throws HttpClientException;

    SectionGradesResponse getSectionScoresBySectionId(Long sectionId) throws HttpClientException;

    PGAssignments getAssignmentsBySectionId(Long sectionId) throws HttpClientException;

    PGAssignmentTypes getAssignmentTypesBySectionId(Long sectionId) throws HttpClientException;

    AssignmentScoresResponse getStudentScoresByAssignmentId(Long assignmentId) throws HttpClientException;

    SectionScoreIdsResponse getStudentScoreIdsBySectionId(Long sectionId) throws HttpClientException;

    String executeNamedQuery(String tableName) throws IOException, HttpClientException;

    <T> List<T> namedQuery(Class<T> clazz, String tableName, IDeserialize<T> transformer) throws IOException;
}