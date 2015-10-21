package com.scholarscore.etl.powerschool.client;

import com.scholarscore.etl.powerschool.api.deserializers.IDeserialize;
import com.scholarscore.etl.powerschool.api.model.Courses;
import com.scholarscore.etl.powerschool.api.model.SectionEnrollments;
import com.scholarscore.etl.powerschool.api.model.Sections;
import com.scholarscore.etl.powerschool.api.model.Staff;
import com.scholarscore.etl.powerschool.api.model.Staffs;
import com.scholarscore.etl.powerschool.api.model.Students;
import com.scholarscore.etl.powerschool.api.model.assignment.PGAssignments;
import com.scholarscore.etl.powerschool.api.response.*;

import java.util.List;

/**
 * Created by mattg on 7/2/15.
 */
public interface IPowerSchoolClient {
    SchoolsResponse getSchools();

    DistrictResponse getDistrict();

    Staffs getStaff(Long schoolId);

    Students getStudentsBySchool(Long schoolId);

    Courses getCoursesBySchool(Long schoolId);

    Object getAsMap(String path);

    TermResponse getTermsBySchoolId(Long schoolId);

    public SectionResponse getSectionsBySchoolId(Long schoolId);
    
    public SectionEnrollmentsResponse getEnrollmentBySectionId(Long sectionId);
    
    public PGAssignments getAssignmentsBySectionId(Long sectionId);

    String executeNamedQuery(String tableName);

    <T> List<T> namedQuery(Class<T> clazz, String tableName, IDeserialize<T> transformer);
}