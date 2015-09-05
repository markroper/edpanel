package com.scholarscore.etl.powerschool.api.model;


import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by mattg on 6/28/15.
 */
@XmlRootElement(name = "student")
public class Student {

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
    public Name name;
    public Demographics demographics;
    public String state_province_d;
    public String student_username;
    public StudentAddresses addresses;

    public SchoolEnrollment school_enrollment;

    public EthnicityRace ethnicity_race;
//    public List<StudentFee> fees;
}
