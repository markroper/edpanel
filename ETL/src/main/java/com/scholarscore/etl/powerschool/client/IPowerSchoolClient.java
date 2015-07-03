package com.scholarscore.etl.powerschool.client;

import com.scholarscore.etl.powerschool.api.response.*;

/**
 * Created by mattg on 7/2/15.
 */
public interface IPowerSchoolClient {
    SchoolsResponse getSchools();

    DistrictResponse getDistrict();

    StaffResponse getStaff(Long schoolId);

    StudentResponse getDistrictStudents();

    CourseResponse getCoursesBySchool(Long schoolId);

    public Object getAsMap(String path);
}