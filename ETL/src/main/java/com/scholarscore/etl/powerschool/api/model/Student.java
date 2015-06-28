package com.scholarscore.etl.powerschool.api.model;


import java.util.List;

/**
 * Created by mattg on 6/28/15.
 */
public class Student {
    String clientUid;
    String action;
    Long id;

    Long localId;
    String stateProvinceId;
    String studentUsername;

    List<Address> physical;
    List<Address> mailing;

    List<Alert> alerts;

    Contact contact;

    Demographics demographics;
    EthnicityRace ethnicityRace;

    List<StudentFee> fees;
}
