package com.scholarscore.etl.powerschool.api.model;

import java.util.Date;

/**
 * Created by mattg on 6/28/15.
 */
public class SectionEnrollment {
    Long id;
    Long sectionId;
    Long studentId;
    Date entryDate;
    Date exitDate;
    Boolean dropped;
}
