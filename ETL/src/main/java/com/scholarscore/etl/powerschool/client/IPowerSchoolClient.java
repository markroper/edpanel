package com.scholarscore.etl.powerschool.client;

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
import com.scholarscore.etl.powerschool.api.response.SectionResponse;
import com.scholarscore.etl.powerschool.api.response.SectionScoreIdsResponse;
import com.scholarscore.etl.powerschool.api.response.SectionScoresResponse;
import com.scholarscore.etl.powerschool.api.response.StudentResponse;
import com.scholarscore.etl.powerschool.api.response.TermResponse;

import java.util.List;

/**
 * Created by mattg on 7/2/15.
 */
public interface IPowerSchoolClient {
    SchoolsResponse getSchools();

    DistrictResponse getDistrict();

    PsStaffs getStaff(Long schoolId);

    PsStudents getStudentsBySchool(Long schoolId);

    StudentResponse getStudentById(Long studentId);

    PsCourses getCoursesBySchool(Long schoolId);

    Object getAsMap(String path);

    TermResponse getTermsBySchoolId(Long schoolId);

    SectionResponse getSectionsBySchoolId(Long schoolId);
    
    SectionEnrollmentsResponse getEnrollmentBySectionId(Long sectionId);

    SectionScoresResponse getSectionScoresBySecionId(Long sectionId);

    PGAssignments getAssignmentsBySectionId(Long sectionId);

    PGAssignmentTypes getAssignmentTypesBySectionId(Long sectionId);

    AssignmentScoresResponse getStudentScoresByAssignmentId(Long assignmentId);

    SectionScoreIdsResponse getStudentScoreIdsBySectionId(Long sectionId);

    String executeNamedQuery(String tableName);

    <T> List<T> namedQuery(Class<T> clazz, String tableName, IDeserialize<T> transformer);
}