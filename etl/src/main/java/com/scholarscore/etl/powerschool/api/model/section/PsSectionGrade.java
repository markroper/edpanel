package com.scholarscore.etl.powerschool.api.model.section;

import java.util.Date;

/**
 * Created by markroper on 10/22/15.
 */
public class PsSectionGrade {
    protected String studentid;
    protected String gpa_addedvalue;
    protected Date datestored;
    protected Long tardies;
    protected String credit_type;
    protected Long termid;
    protected Long dcid;
    protected String excludefromgpa;
    protected String grade;
    protected String gradescale_name;
    protected Long sectionid;
    protected Double percent;

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getGpa_addedvalue() {
        return gpa_addedvalue;
    }

    public void setGpa_addedvalue(String gpa_addedvalue) {
        this.gpa_addedvalue = gpa_addedvalue;
    }

    public Date getDatestored() {
        return datestored;
    }

    public void setDatestored(Date datestored) {
        this.datestored = datestored;
    }

    public Long getTermid() {
        return termid;
    }

    public void setTermid(Long termid) {
        this.termid = termid;
    }

    public Long getDcid() {
        return dcid;
    }

    public void setDcid(Long dcid) {
        this.dcid = dcid;
    }

    public String getExcludefromgpa() {
        return excludefromgpa;
    }

    public void setExcludefromgpa(String excludefromgpa) {
        this.excludefromgpa = excludefromgpa;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getGradescale_name() {
        return gradescale_name;
    }

    public void setGradescale_name(String gradescale_name) {
        this.gradescale_name = gradescale_name;
    }

    public Long getSectionid() {
        return sectionid;
    }

    public void setSectionid(Long sectionid) {
        this.sectionid = sectionid;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }

    public Long getTardies() {
        return tardies;
    }

    public void setTardies(Long tardies) {
        this.tardies = tardies;
    }

    public String getCredit_type() {
        return credit_type;
    }

    public void setCredit_type(String credit_type) {
        this.credit_type = credit_type;
    }
}
