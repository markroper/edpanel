package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a single student's grade in a specific course.  The complete boolean
 * indicates whether or not the grade is final or the course is still ongoing.
 * 
 * @author markroper
 *
 */
@Entity(name="studentSectionGrade")
@Table(name = HibernateConsts.STUDENT_SECTION_GRADE_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentSectionGrade implements Serializable, WeightedGradable, IApiModel<StudentSectionGrade> {
    protected Long id;
    protected Boolean complete;
    protected Double grade;
    protected Section section;
    protected Student student;
    
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

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name=HibernateConsts.SECTION_FK)
    @Fetch(FetchMode.JOIN)
    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = HibernateConsts.STUDENT_FK)
    @Fetch(FetchMode.JOIN)
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = HibernateConsts.STUDENT_SECTION_GRADE_ID)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = HibernateConsts.STUDENT_SECTION_GRADE_COMPLETE)
    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    @Column(name = HibernateConsts.STUDENT_SECTION_GRADE_GRADE)
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
    //@Column(name = "grade", insertable=false, updatable=false)
    @Transient
    public Double getAwardedPoints() {
        return grade;
    }

    @Override
    @JsonIgnore
    @Transient
    public Double getAvailablePoints() {
        return MAX_GRADE;
    }

    @Override
    @JsonIgnore
    @Transient
    public int getWeight() {
        return 1;
    }
}
