package com.scholarscore.models;

import java.io.Serializable;
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
public class StudentAssignment extends ApiModel implements Serializable, IApiModel<StudentAssignment> {
    private Boolean completed;
    private Long awardedPoints;
    private transient SectionAssignment sectionAssignment;
    private transient Student student;

    public StudentAssignment() {
        super();
    }
    
    public StudentAssignment(StudentAssignment sa) {
        super(sa);
        this.sectionAssignment = sa.sectionAssignment;
        this.completed = sa.completed;
        this.awardedPoints = sa.awardedPoints;
        this.student = sa.student;
    }

    @Override
    public void mergePropertiesIfNull(StudentAssignment mergeFrom) {
        if(null == mergeFrom) {
            return;
        }
        if(null == this.sectionAssignment) {
            this.sectionAssignment = mergeFrom.sectionAssignment;
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
    }
    
    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public SectionAssignment getSectionAssignment() {
        return sectionAssignment;
    }

    public void setAssignment(SectionAssignment sectionAssignment) {
        this.sectionAssignment = sectionAssignment;
    }

    public Long getAwardedPoints() {
        return awardedPoints;
    }

    public void setAwardedPoints(Long awardedPoints) {
        this.awardedPoints = awardedPoints;
    }

    public void setSectionAssignment(SectionAssignment sectionAssignment) {
        this.sectionAssignment = sectionAssignment;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        final StudentAssignment other = (StudentAssignment) obj;
        return Objects.equals(this.sectionAssignment, other.sectionAssignment) && 
                Objects.equals(this.completed, other.completed) &&
                Objects.equals(this.awardedPoints, other.awardedPoints) &&
                Objects.equals(this.student, other.student);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(sectionAssignment, completed, awardedPoints, student);
    }
}
