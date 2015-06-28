package com.scholarscore.etl.powerschool.api.model;

import java.util.List;

/**
 * Created by mattg on 6/28/15.
 */
public class Staff {
    Long id;
    String localId;
    Long adminUsername;
    String teacherUsername;
    Fullname name;

    List<Address> addresses;

    String workEmail;
    List<Phone> phones;

    List<SchoolAffiliation> schoolAffiliations;


}
