package com.scholarscore.models;

import java.io.Serializable;

/**
 * Created by cwallace on 12/23/15.
 */
public class Cycle extends ApiModel implements Serializable, IApiModel<Cycle> {

    private Long dcid;
    private String dayName;
    private String letter;
    private Long schoolNumber;
    private Long dayNumber;
    private Long yearId;
    private String abbreviation;

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public Long getDcid() {
        return dcid;
    }

    public void setDcid(Long dcid) {
        this.dcid = dcid;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public Long getSchoolNumber() {
        return schoolNumber;
    }

    public void setSchoolNumber(Long schoolId) {
        this.schoolNumber = schoolId;
    }

    public Long getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(Long dayNumber) {
        this.dayNumber = dayNumber;
    }

    public Long getYearId() {
        return yearId;
    }

    public void setYearId(Long yearId) {
        this.yearId = yearId;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public void mergePropertiesIfNull(Cycle mergeFrom) {

    }
}
