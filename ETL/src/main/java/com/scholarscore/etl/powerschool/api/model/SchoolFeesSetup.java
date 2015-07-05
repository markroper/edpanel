package com.scholarscore.etl.powerschool.api.model;

import java.util.List;

/**
 * Created by mattg on 6/28/15.
 */
public class SchoolFeesSetup {
    Long exemptionStatus;
    List<FeeType> feeTypes;
    List<SchoolFee> schoolFees;
}
