package com.scholarscore.etl.powerschool.api.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by mattg on 6/28/15.
 */
@XmlRootElement(name = "section_enrollment")
public class PsSectionEnrollment {
    protected Long id;
    protected Long section_id;
    protected Long student_id;
    protected Date entry_date;
    protected Date exit_date;
    protected Boolean dropped;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getSection_id() {
        return section_id;
    }
    public void setSection_id(Long section_id) {
        this.section_id = section_id;
    }
    public Long getStudent_id() {
        return student_id;
    }
    public void setStudent_id(Long student_id) {
        this.student_id = student_id;
    }
    public Date getEntry_date() {
        return entry_date;
    }
    public void setEntry_date(Date entry_date) {
        this.entry_date = entry_date;
    }
    public Date getExit_date() {
        return exit_date;
    }
    public void setExit_date(Date exit_date) {
        this.exit_date = exit_date;
    }
    public Boolean getDropped() {
        return dropped;
    }
    public void setDropped(Boolean dropped) {
        this.dropped = dropped;
    }
}
