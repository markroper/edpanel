package com.scholarscore.etl.powerschool.client;

import com.scholarscore.etl.powerschool.api.response.DistrictResponse;
import com.scholarscore.etl.powerschool.api.response.SchoolsResponse;
import com.scholarscore.etl.powerschool.api.response.StaffResponse;
import com.scholarscore.etl.powerschool.api.response.StudentResponse;

/**
 * Created by mattg on 7/2/15.
 */
public interface IPowerSchoolClient {
    SchoolsResponse getSchools();

    DistrictResponse getDistrict();

    StaffResponse getStaff(Long schoolId);

    StudentResponse getDistrictStudents();

    public Object getAsMap(String path);
}