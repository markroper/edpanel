package com.scholarscore.etl.powerschool.api.model.assignment.scores;

/**
 * Created by markroper on 10/22/15.
 */
public class PsSectionScoreId {
    protected String studentid;
    protected String dcid;
    protected String id;
    protected String sectionid;

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getDcid() {
        return dcid;
    }

    public void setDcid(String dcid) {
        this.dcid = dcid;
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
}
