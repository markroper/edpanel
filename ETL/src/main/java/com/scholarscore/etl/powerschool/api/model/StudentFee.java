package com.scholarscore.etl.powerschool.api.model;

import java.util.Date;
import java.util.List;

/**
 * Created by mattg on 6/28/15.
 */
public class StudentFee {
    public class FeeBalance {
        Long studentId;
        Long yearId;
        Long termId;
        Float debit;
        Float credit;
        String feeExemptionStatus;
        String feeExemptionStatusCode;
    }

    public class Fee {
        Long id;
        Float amount;
        Float balance;
        String description;
        Date dateCreated;
        Date dateModified;
        Date transactionDate;
        Long priority;
        Long proratableIndicator;
        Long groupTransactionId;

        Long schoolId;
        Long schoolFeeId;
        Long termId;
        Long yearId;
        String courseName;
        Long courseNumber;
        String categoryName;
        Long typeId;
        String typeName;
    }

    Long id;

    List<FeeBalance> feeBalance;
    List<Fee> fees;


}
