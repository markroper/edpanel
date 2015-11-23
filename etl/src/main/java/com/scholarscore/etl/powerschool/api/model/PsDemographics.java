package com.scholarscore.etl.powerschool.api.model;

import java.time.LocalDate;

/**
 * Created by mattg on 6/28/15.
 */
public class PsDemographics {
    public String gender;
    public LocalDate birth_date;
    public LocalDate district_entry_date;
    public Long projected_gradulation_date;
    public String ssn;
}
