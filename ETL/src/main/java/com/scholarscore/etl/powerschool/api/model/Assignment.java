package com.scholarscore.etl.powerschool.api.model;

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
    private Date assignmentDueDate;
    private String category;
    private Long extraCreditPoints;
    private Boolean includeFinalGrades;
    private Long pointsPossible;
    private Boolean publishScores;
    private String publishState;
    private Long publishDaysBeforeDue;
    private Date publishSpecificDate;
    private String scoringType;
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

    public Date getAssignmentDueDate() {
        return assignmentDueDate;
    }

    public void setAssignmentDueDate(Date assignmentDueDate) {
        this.assignmentDueDate = assignmentDueDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getExtraCreditPoints() {
        return extraCreditPoints;
    }

    public void setExtraCreditPoints(Long extraCreditPoints) {
        this.extraCreditPoints = extraCreditPoints;
    }

    public Boolean getIncludeFinalGrades() {
        return includeFinalGrades;
    }

    public void setIncludeFinalGrades(Boolean includeFinalGrades) {
        this.includeFinalGrades = includeFinalGrades;
    }

    public Long getPointsPossible() {
        return pointsPossible;
    }

    public void setPointsPossible(Long pointsPossible) {
        this.pointsPossible = pointsPossible;
    }

    public Boolean getPublishScores() {
        return publishScores;
    }

    public void setPublishScores(Boolean publishScores) {
        this.publishScores = publishScores;
    }

    public String getPublishState() {
        return publishState;
    }

    public void setPublishState(String publishState) {
        this.publishState = publishState;
    }

    public Long getPublishDaysBeforeDue() {
        return publishDaysBeforeDue;
    }

    public void setPublishDaysBeforeDue(Long publishDaysBeforeDue) {
        this.publishDaysBeforeDue = publishDaysBeforeDue;
    }

    public Date getPublishSpecificDate() {
        return publishSpecificDate;
    }

    public void setPublishSpecificDate(Date publishSpecificDate) {
        this.publishSpecificDate = publishSpecificDate;
    }

    public String getScoringType() {
        return scoringType;
    }

    public void setScoringType(String scoringType) {
        this.scoringType = scoringType;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Assignment)) return false;

        Assignment that = (Assignment) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        if (getAbbreviation() != null ? !getAbbreviation().equals(that.getAbbreviation()) : that.getAbbreviation() != null)
            return false;
        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
            return false;
        if (getAssignmentDueDate() != null ? !getAssignmentDueDate().equals(that.getAssignmentDueDate()) : that.getAssignmentDueDate() != null)
            return false;
        if (getCategory() != null ? !getCategory().equals(that.getCategory()) : that.getCategory() != null)
            return false;
        if (getExtraCreditPoints() != null ? !getExtraCreditPoints().equals(that.getExtraCreditPoints()) : that.getExtraCreditPoints() != null)
            return false;
        if (getIncludeFinalGrades() != null ? !getIncludeFinalGrades().equals(that.getIncludeFinalGrades()) : that.getIncludeFinalGrades() != null)
            return false;
        if (getPointsPossible() != null ? !getPointsPossible().equals(that.getPointsPossible()) : that.getPointsPossible() != null)
            return false;
        if (getPublishScores() != null ? !getPublishScores().equals(that.getPublishScores()) : that.getPublishScores() != null)
            return false;
        if (getPublishState() != null ? !getPublishState().equals(that.getPublishState()) : that.getPublishState() != null)
            return false;
        if (getPublishDaysBeforeDue() != null ? !getPublishDaysBeforeDue().equals(that.getPublishDaysBeforeDue()) : that.getPublishDaysBeforeDue() != null)
            return false;
        if (getPublishSpecificDate() != null ? !getPublishSpecificDate().equals(that.getPublishSpecificDate()) : that.getPublishSpecificDate() != null)
            return false;
        if (getScoringType() != null ? !getScoringType().equals(that.getScoringType()) : that.getScoringType() != null)
            return false;
        return !(getWeight() != null ? !getWeight().equals(that.getWeight()) : that.getWeight() != null);

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getAbbreviation() != null ? getAbbreviation().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getAssignmentDueDate() != null ? getAssignmentDueDate().hashCode() : 0);
        result = 31 * result + (getCategory() != null ? getCategory().hashCode() : 0);
        result = 31 * result + (getExtraCreditPoints() != null ? getExtraCreditPoints().hashCode() : 0);
        result = 31 * result + (getIncludeFinalGrades() != null ? getIncludeFinalGrades().hashCode() : 0);
        result = 31 * result + (getPointsPossible() != null ? getPointsPossible().hashCode() : 0);
        result = 31 * result + (getPublishScores() != null ? getPublishScores().hashCode() : 0);
        result = 31 * result + (getPublishState() != null ? getPublishState().hashCode() : 0);
        result = 31 * result + (getPublishDaysBeforeDue() != null ? getPublishDaysBeforeDue().hashCode() : 0);
        result = 31 * result + (getPublishSpecificDate() != null ? getPublishSpecificDate().hashCode() : 0);
        result = 31 * result + (getScoringType() != null ? getScoringType().hashCode() : 0);
        result = 31 * result + (getWeight() != null ? getWeight().hashCode() : 0);
        return result;
    }
}
