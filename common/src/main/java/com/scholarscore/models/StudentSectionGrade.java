package com.scholarscore.models;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represents a single student's grade in a specific course.  The complete boolean
 * indicates whether or not the grade is final or the course is still ongoing.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentSectionGrade implements Serializable, WeightedGradable, IApiModel<StudentSectionGrade> {
    protected Long id;
    protected Boolean complete;
    protected Double grade;
    
    // teachers can grade assignments however they want, 
    // though currently each course must have a final grade out of 100
    private static final Double MAX_GRADE = 100D;
    
    public StudentSectionGrade() {
        
    }
    
    public StudentSectionGrade(StudentSectionGrade grade) {
        this.id = grade.id;
        this.complete = grade.complete;
        this.grade = grade.grade;
    }
    
    @Override
    public void mergePropertiesIfNull(StudentSectionGrade mergeFrom) {
        if(null == id) {
            id = mergeFrom.id;
        }
        if(null == complete) {
            complete = mergeFrom.complete;
        }
        if(null == grade) {
            grade = mergeFrom.grade;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final StudentSectionGrade other = (StudentSectionGrade) obj;
        return Objects.equals(this.id, other.id) && 
                Objects.equals(this.complete, other.complete) &&
                Objects.equals(this.grade, other.grade);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(id, complete, grade);
    }

    @Override
    @JsonIgnore
    public Double getAwardedPoints() {
        return grade;
    }

    @Override
    @JsonIgnore
    public Double getAvailablePoints() {
        return MAX_GRADE;
    }

    @Override
    @JsonIgnore
    public int getWeight() {
        return 1;
    }
}
