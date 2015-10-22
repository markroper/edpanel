package com.scholarscore.etl.powerschool.client;

import com.scholarscore.etl.powerschool.api.deserializers.IDeserialize;
import com.scholarscore.etl.powerschool.api.model.PsCourses;
import com.scholarscore.etl.powerschool.api.model.PsStaffs;
import com.scholarscore.etl.powerschool.api.model.PsStudents;
import com.scholarscore.etl.powerschool.api.model.assignment.PGAssignments;
import com.scholarscore.etl.powerschool.api.model.assignment.type.PGAssignmentTypes;
import com.scholarscore.etl.powerschool.api.response.*;

import java.util.List;

/**
 * Created by mattg on 7/2/15.
 */
public interface IPowerSchoolClient {
    SchoolsResponse getSchools();

    DistrictResponse getDistrict();

    PsStaffs getStaff(Long schoolId);

    PsStudents getStudentsBySchool(Long schoolId);

    PsCourses getCoursesBySchool(Long schoolId);

    Object getAsMap(String path);

    TermResponse getTermsBySchoolId(Long schoolId);

    public SectionResponse getSectionsBySchoolId(Long schoolId);
    
    public SectionEnrollmentsResponse getEnrollmentBySectionId(Long sectionId);
    
    public PGAssignments getAssignmentsBySectionId(Long sectionId);

    public PGAssignmentTypes getAssignmentTypesBySectionId(Long sectionId);

    String executeNamedQuery(String tableName);

    <T> List<T> namedQuery(Class<T> clazz, String tableName, IDeserialize<T> transformer);
}