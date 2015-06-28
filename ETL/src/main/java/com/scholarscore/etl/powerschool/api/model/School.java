package com.scholarscore.etl.powerschool.api.model;

import java.util.List;

/**
 * Created by mattg on 6/28/15.
 */
public class School {
    Long id;
    String name;
    String schoolNumber;
    String stateProvinceId;
    Long lowGrade;
    Long highGrade;
    Long alternateSchoolNumber;
    List<Address> addresses;

    Person assistantPrincipal;

    Phone phone;

    Person principal;

    Boundary boundary;

    SchoolFeesSetup schoolFeesSetup;
}
