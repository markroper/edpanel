package com.scholarscore.etl.powerschool.api.model;


import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by mattg on 6/28/15.
 */
@XmlRootElement(name = "student")
public class Student {
    public Long id;

    public Long local_id;
    public String state_province_d;
    public String student_username;

    public Name name;

    Addresses physical;
    Addresses mailing;

    List<Alert> alerts;

    Contact contact;

    Demographics demographics;
    EthnicityRace ethnicityRace;

    List<StudentFee> fees;
}
