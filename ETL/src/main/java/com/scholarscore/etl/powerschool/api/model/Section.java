package com.scholarscore.etl.powerschool.api.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by mattg on 6/28/15.
 */
@XmlRootElement(name = "section")
public class Section {
    protected Long id;
    protected Long school_id;
    protected String course_id;
    protected Long term_id;
    protected String section_number;
    protected String expression;
    protected Long staff_id;
    
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
    public String getCourse_id() {
        return course_id;
    }
    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }
    public Long getTerm_id() {
        return term_id;
    }
    public void setTerm_id(Long term_id) {
        this.term_id = term_id;
    }
    public String getSection_number() {
        return section_number;
    }
    public void setSection_number(String section_number) {
        this.section_number = section_number;
    }
    public String getExpression() {
        return expression;
    }
    public void setExpression(String expression) {
        this.expression = expression;
    }
    public Long getStaff_id() {
        return staff_id;
    }
    public void setStaff_id(Long staff_id) {
        this.staff_id = staff_id;
    }
}
