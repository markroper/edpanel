package com.scholarscore.models;

import java.io.Serializable;
import java.util.Objects;

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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.user.Student;

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
public class StudentSectionGrade extends ApiModel implements Serializable, WeightedGradable, IApiModel<StudentSectionGrade> {

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
        this.student = grade.student;
        this.section = grade.section;
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
        if (student == null) {
            student = mergeFrom.student;
        }
        if (section == null) {
            section = mergeFrom.section;
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
        section.addStudentSectionGrade(this);
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
                Objects.equals(this.grade, other.grade) &&
                Objects.equals(this.student, other.student) &&
                Objects.equals(this.section, other.section);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(id, complete, grade, student, section);
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

    @Override
    public String toString() {
        return "StudentSectionGrade{" +
                "id=" + id +
                ", complete=" + complete +
                ", grade=" + grade +
                ", section=" + section +
                ", student=" + student +
                '}';
    }

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static class StudentSectionGradeBuilder extends ApiModelBuilder<StudentSectionGradeBuilder, StudentSectionGrade>{
        protected Boolean complete;
        protected Double grade;
        protected Section section;
        protected Student student;

        public StudentSectionGradeBuilder withComplete(final Boolean complete){
            this.complete = complete;
            return this;
        }

        public StudentSectionGradeBuilder withGrade(final Double grade){
            this.grade = grade;
            return this;
        }

        public StudentSectionGradeBuilder withSection(final Section section){
            this.section = section;
            return this;
        }

        public StudentSectionGradeBuilder withStudent(final Student student){
            this.student = student;
            return this;
        }

        public StudentSectionGrade build(){
            StudentSectionGrade sectionGrade = super.build();
            sectionGrade.setComplete(complete);
            sectionGrade.setGrade(grade);
            sectionGrade.setSection(section);
            section.addStudentSectionGrade(sectionGrade);
            sectionGrade.setStudent(student);
            return sectionGrade;
        }

        @Override
        protected StudentSectionGradeBuilder me() {
            return this;
        }

        @Override
        public StudentSectionGrade getInstance() {
            return null;
        }
    }
}
