package com.scholarscore.etl.powerschool.api.model.term;

import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;
import java.util.Date;

/**
 * Created by mattg on 6/28/15.
 */
@XmlRootElement(name = "term")
public class PsTerm {
    protected Long id;
    protected Long school_id;
    protected Long start_year;
    protected Long portion;
    protected LocalDate start_date;
    protected LocalDate end_date;
    protected String abbreviation;
    protected String name;
    
    public PsTerm() {
        
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getSchool_id() {
        return school_id;
    }
    public void setSchool_id(Long school_id) {
        this.school_id = school_id;
    }
    public Long getStart_year() {
        return start_year;
    }
    public void setStart_year(Long start_year) {
        this.start_year = start_year;
    }
    public Long getPortion() {
        return portion;
    }
    public void setPortion(Long portion) {
        this.portion = portion;
    }
    public LocalDate getStart_date() {
        return start_date;
    }
    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }
    public LocalDate getEnd_date() {
        return end_date;
    }
    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }
    public String getAbbreviation() {
        return abbreviation;
    }
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
