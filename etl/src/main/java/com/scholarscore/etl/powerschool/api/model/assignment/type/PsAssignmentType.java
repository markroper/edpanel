package com.scholarscore.etl.powerschool.api.model.assignment.type;

/**
 * Created by markroper on 10/22/15.
 */
public class PsAssignmentType {
    protected String dcid;
    protected String name;
    protected String description;
    protected String id;
    protected String sectionid;
    protected String abbreviation;
    protected String defaultptsposs;

    public String getDcid() {
        return dcid;
    }

    public void setDcid(String dcid) {
        this.dcid = dcid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSectionid() {
        return sectionid;
    }

    public void setSectionid(String sectionid) {
        this.sectionid = sectionid;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getDefaultptsposs() {
        return defaultptsposs;
    }

    public void setDefaultptsposs(String defaultptsposs) {
        this.defaultptsposs = defaultptsposs;
    }

}
