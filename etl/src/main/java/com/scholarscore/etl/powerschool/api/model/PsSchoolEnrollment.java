package com.scholarscore.etl.powerschool.api.model;

import java.util.Date;

/**
 * Created by mattg on 8/3/15.
 */
public class PsSchoolEnrollment {
//        "fteid": 2,
//                "name": "Full Day"
//    }
    public String enroll_status;
    public String enroll_status_description;
    public String enroll_status_code;
    public String grade_level;
    public Date entry_date;
    public Date exit_date;
    public Long school_number;
    public Long school_id;
    public String entry_code;
    public String district_of_residence;
    public String track;
    public String full_time_equivalency;

}
