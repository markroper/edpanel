package com.scholarscore.etl.powerschool.api.model.assignment;

import java.util.Date;

/**
 * Assignment model
 *
 * Documentation: api-developer-guide-1.6.0/data-access/basic-read-and-write/data-dictionary.html#assignment
 *
 * Created by mattg on 6/28/15.
 */
public class Assignment {
    private Long id;
    private String name;
    private String abbreviation;
    private String description;
    private Date datedue;
    private String pgcategoriesid;
    private Long sectionid;
    private Boolean includeinfinalgrades;
    private Long pointspossible;
    private Boolean publishscores;
    private String publishstate;
    private Long publishdaysbeforedue;
    private Date publishspecificdate;
    private Long weight;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAbbreviation() {
        return abbreviation;
    }
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Date getDatedue() {
        return datedue;
    }
    public void setDatedue(Date datedue) {
        this.datedue = datedue;
    }
    public String getPgcategoriesid() {
        return pgcategoriesid;
    }
    public void setPgcategoriesid(String pgcategoriesid) {
        this.pgcategoriesid = pgcategoriesid;
    }
    public Long getSectionid() {
        return sectionid;
    }
    public void setSectionid(Long sectionid) {
        this.sectionid = sectionid;
    }
    public Boolean getIncludeinfinalgrades() {
        return includeinfinalgrades;
    }
    public void setIncludeinfinalgrades(Boolean includeinfinalgrades) {
        this.includeinfinalgrades = includeinfinalgrades;
    }
    public Long getPointspossible() {
        return pointspossible;
    }
    public void setPointspossible(Long pointspossible) {
        this.pointspossible = pointspossible;
    }
    public Boolean getPublishscores() {
        return publishscores;
    }
    public void setPublishscores(Boolean publishscores) {
        this.publishscores = publishscores;
    }
    public String getPublishstate() {
        return publishstate;
    }
    public void setPublishstate(String publishstate) {
        this.publishstate = publishstate;
    }
    public Long getPublishdaysbeforedue() {
        return publishdaysbeforedue;
    }
    public void setPublishdaysbeforedue(Long publishdaysbeforedue) {
        this.publishdaysbeforedue = publishdaysbeforedue;
    }
    public Date getPublishspecificdate() {
        return publishspecificdate;
    }
    public void setPublishspecificdate(Date publishspecificdate) {
        this.publishspecificdate = publishspecificdate;
    }
    public Long getWeight() {
        return weight;
    }
    public void setWeight(Long weight) {
        this.weight = weight;
    }
}
