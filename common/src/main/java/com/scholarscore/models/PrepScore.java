package com.scholarscore.models;

import java.util.Date;

/**
 * User: jordan
 * Date: 10/29/15
 * Time: 3:23 PM
 */
public class PrepScore {

    // this hardcoding is for the beta. future versions will allow this value to vary and eventually
    // customize the formula entirely.
    public static final int INITIAL_PREP_SCORE = 90;

    private Date startDate;
    private Date endDate;
    private Long score;
    private Long studentId; 

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrepScore prepScore = (PrepScore) o;

        if (endDate != null ? !endDate.equals(prepScore.endDate) : prepScore.endDate != null) return false;
        if (score != null ? !score.equals(prepScore.score) : prepScore.score != null) return false;
        if (startDate != null ? !startDate.equals(prepScore.startDate) : prepScore.startDate != null) return false;
        if (studentId != null ? !studentId.equals(prepScore.studentId) : prepScore.studentId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = startDate != null ? startDate.hashCode() : 0;
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (score != null ? score.hashCode() : 0);
        result = 31 * result + (studentId != null ? studentId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PrepScore{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", score=" + score +
                ", studentId=" + studentId +
                '}';
    }
}
