package com.scholarscore.etl.powerschool.api.model.assignment;

import java.time.LocalDate;

/**
 * PsAssignment model
 *
 * Documentation: api-developer-guide-1.6.0/data-access/basic-read-and-write/data-dictionary.html#assignment
 *
 * Created by mattg on 6/28/15.
 */
public class PsAssignment {
    private Long id;
    private Long dcid;
    private String name;
    private String abbreviation;
    private String description;
    private LocalDate datedue;
    private String pgcategoriesid;
    private Long sectionid;
    private String includeinfinalgrades;
    private Long pointspossible;
    private String publishscores;
    private String publishstate;
    private Long publishdaysbeforedue;
    private LocalDate publishspecificdate;
    private Double weight;

    public Long getDcid() {
        return dcid;
    }

    public void setDcid(Long dcid) {
        this.dcid = dcid;
    }

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
    public LocalDate getDatedue() {
        return datedue;
    }
    public void setDatedue(LocalDate datedue) {
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
    public String getIncludeinfinalgrades() {
        return includeinfinalgrades;
    }
    public void setIncludeinfinalgrades(String includeinfinalgrades) {
        this.includeinfinalgrades = includeinfinalgrades;
    }
    public Long getPointspossible() {
        return pointspossible;
    }
    public void setPointspossible(Long pointspossible) {
        this.pointspossible = pointspossible;
    }
    public String getPublishscores() {
        return publishscores;
    }
    public void setPublishscores(String publishscores) {
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
    public LocalDate getPublishspecificdate() {
        return publishspecificdate;
    }
    public void setPublishspecificdate(LocalDate publishspecificdate) {
        this.publishspecificdate = publishspecificdate;
    }
    public Double getWeight() {
        return weight;
    }
    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
