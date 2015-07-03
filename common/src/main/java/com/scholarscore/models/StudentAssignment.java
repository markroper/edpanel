package com.scholarscore.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represents the student's performance on an assignment in a specific course.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentAssignment extends ApiModel implements Serializable, WeightedGradable, IApiModel<StudentAssignment> {
    private Boolean completed;
    private Date completionDate;
    private Long awardedPoints;
    private transient Assignment assignment;
    private transient Student student;

    public StudentAssignment() {
        super();
    }
    
    public StudentAssignment(StudentAssignment sa) {
        super(sa);
        this.assignment = sa.assignment;
        this.completed = sa.completed;
        this.awardedPoints = sa.awardedPoints;
        this.student = sa.student;
        this.completionDate = sa.completionDate;
    }

    @Override
    public void mergePropertiesIfNull(StudentAssignment mergeFrom) {
        if(null == mergeFrom) {
            return;
        }
        if(null == this.assignment) {
            this.assignment = mergeFrom.assignment;
        }
        if(null == this.completed) {
            this.completed = mergeFrom.completed;
        }
        if(null == this.awardedPoints) {
            this.awardedPoints = mergeFrom.awardedPoints;
        } 
        if(null == this.student) {
            this.student = mergeFrom.student;
        }
        if(null == this.completionDate) {
            this.completionDate = mergeFrom.completionDate;
        }
    }
    
    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    @Override
    public Long getAwardedPoints() {
        return awardedPoints != null ? awardedPoints : null;
    }

    @Override
    public Long getAvailablePoints() {
        Long availablePoints = assignment != null ? assignment.getAvailablePoints() : null;
        return (availablePoints != null ? availablePoints : null );
    }

    @Override
    public int getWeight() {
        // Today, these weights live in GradeFormula and can't be 
        // directly grabbed from StudentAssignment.
        return 1;
    }

    public void setAwardedPoints(Long awardedPoints) {
        this.awardedPoints = awardedPoints;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        final StudentAssignment other = (StudentAssignment) obj;
        return Objects.equals(this.assignment, other.assignment) && 
                Objects.equals(this.completed, other.completed) &&
                Objects.equals(this.awardedPoints, other.awardedPoints) &&
                Objects.equals(this.student, other.student) &&
                Objects.equals(this.completionDate, other.completionDate);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(assignment, completed, awardedPoints, student, completionDate);
    }
}
