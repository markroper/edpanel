package com.scholarscore.etl.powerschool.api.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by mattg on 6/28/15.
 */
@XmlRootElement(name = "staff")
public class Staff {
    Long id;
    String local_id;
    String admin_username;
    String teacher_username;
//    Name name;
//
//    List<Addresses> addresses;
//
//    String workEmail;
//    List<Phones> phones;
//
//    List<SchoolAffiliation> schoolAffiliations;


    @Override
    public String toString() {
        return "Staff{" +
                "id=" + id +
                ", local_id='" + local_id + '\'' +
                ", admin_username='" + admin_username + '\'' +
                ", teacher_username='" + teacher_username + '\'' +
                '}';
    }
}
