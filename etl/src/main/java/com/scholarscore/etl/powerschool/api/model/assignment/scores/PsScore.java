package com.scholarscore.etl.powerschool.api.model.assignment.scores;

/**
 * Created by markroper on 10/22/15.
 */
public class PsScore {
    protected String score;
    protected String dcid;
    protected String comment_value;
    protected String assignment;
    protected String grade;
    protected String exempt;
    protected String fdcid;
    protected String percent;

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getDcid() {
        return dcid;
    }

    public void setDcid(String dcid) {
        this.dcid = dcid;
    }

    public String getComment_value() {
        return comment_value;
    }

    public void setComment_value(String comment_value) {
        this.comment_value = comment_value;
    }

    public String getAssignment() {
        return assignment;
    }

    public void setAssignment(String assignment) {
        this.assignment = assignment;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getExempt() {
        return exempt;
    }

    public void setExempt(String exempt) {
        this.exempt = exempt;
    }

    public String getFdcid() {
        return fdcid;
    }

    public void setFdcid(String fdcid) {
        this.fdcid = fdcid;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }
}
