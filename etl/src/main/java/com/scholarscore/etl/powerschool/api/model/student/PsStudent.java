package com.scholarscore.etl.powerschool.api.model.student;


import com.scholarscore.etl.powerschool.api.model.PsDemographics;
import com.scholarscore.etl.powerschool.api.model.PsEthnicityRace;
import com.scholarscore.etl.powerschool.api.model.PsName;
import com.scholarscore.etl.powerschool.api.model.PsSchoolEnrollment;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by mattg on 6/28/15.
 */
@XmlRootElement(name = "student")
public class PsStudent {

    public static class StudentAddress {
        public String street;
        public String city;
        public String state_province;
        public String postal_code;
    }

    public static class StudentAddresses {
        public StudentAddress physical;
        public StudentAddress mailing;
    }

    public Long id;

    public Long local_id;
    public PsName name;
    public PsDemographics demographics;
    public String state_province_id;
    public String student_username;
    public StudentAddresses addresses;

    public PsSchoolEnrollment school_enrollment;

    public PsEthnicityRace ethnicity_race;

    public PsExtensionData _extension_data;
}
