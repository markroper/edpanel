package com.scholarscore.etl.powerschool.client;

import com.scholarscore.etl.powerschool.api.model.Courses;
import com.scholarscore.etl.powerschool.api.model.Staff;
import com.scholarscore.etl.powerschool.api.model.Staffs;
import com.scholarscore.etl.powerschool.api.model.Students;
import com.scholarscore.etl.powerschool.api.response.*;

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

    SectionResponse getSectionsBySchoolId(Long schoolId);

    String executeNamedQuery(String tableName);
}