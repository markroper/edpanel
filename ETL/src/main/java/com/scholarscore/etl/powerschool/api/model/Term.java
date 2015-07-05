package com.scholarscore.etl.powerschool.api.model;

import java.util.Date;

/**
 * Created by mattg on 6/28/15.
 */
public class Term {
    Long id;
    Long schoolId;
    Long startYear;
    Long portion;
    Date startDate;
    Date endDate;
    String abbreviation;
    String name;
}
