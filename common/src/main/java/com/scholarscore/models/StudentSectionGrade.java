package com.scholarscore.models;

import java.io.Serializable;
import java.util.Objects;

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
public class StudentSectionGrade implements Serializable, IApiModel<StudentSectionGrade> {
    Long studentId;
    Long sectionId;
    Boolean complete;
    Double grade;
    
    public StudentSectionGrade() {
        
    }
    
    public StudentSectionGrade(StudentSectionGrade grade) {
        this.studentId = grade.studentId;
        this.sectionId = grade.sectionId;
        this.complete = grade.complete;
        this.grade = grade.grade;
    }
    
    @Override
    public void mergePropertiesIfNull(StudentSectionGrade mergeFrom) {
        if(null == studentId) {
            studentId = mergeFrom.studentId;
        }
        if(null == sectionId) {
            sectionId = mergeFrom.sectionId;
        }
        if(null == complete) {
            complete = mergeFrom.complete;
        }
        if(null == grade) {
            grade = mergeFrom.grade;
        }
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
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
        final StudentSectionGrade other = (StudentSectionGrade) obj;
        return Objects.equals(this.studentId, other.studentId) && 
                Objects.equals(this.sectionId, other.sectionId) &&
                Objects.equals(this.complete, other.complete) &&
                Objects.equals(this.grade, other.grade);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(studentId, sectionId, complete, grade);
    }

}
