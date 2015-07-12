package com.scholarscore.etl.powerschool.api.model;

import java.util.Date;

/**
 * Created by mattg on 6/28/15.
 */
public class SchoolFee {
    Long id;
    Double amount;
    String description;
    String department;
    Long departmentId;
    Long proratable;
    Long startYear;
    Date creationDate;
    Date modifiiedDate;
    Date applicationDate;

    FeeType feeType;
}
