package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholarscore.models.user.Student;
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
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
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
public class StudentSectionGrade extends ApiModel implements Serializable, WeightedGradable, IApiModel<StudentSectionGrade> {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    protected Boolean complete;
    protected Double grade;
    protected HashMap<Long, Score> termGrades;
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
        this.termGrades = grade.termGrades;
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
        if (termGrades == null) {
            termGrades = mergeFrom.termGrades;
        }
    }

    @JsonIgnore
    @Column(name = HibernateConsts.STUDENT_SECTION_GRADE_TERM_GRADES)
    public String getTermGradesString() {
        try {
            return MAPPER.writeValueAsString(termGrades);
        } catch (JsonProcessingException | NullPointerException e) {
            return null;
        }
    }
    @JsonIgnore
    public void setTermGradesString(String gradesString) {
        try {
            this.termGrades = MAPPER.readValue(gradesString, new TypeReference<HashMap<Long, Score>>(){});
        } catch (IOException | NullPointerException e) {
            this.termGrades = null;
        }
    }


    @Transient
    public HashMap<Long, Score> getTermGrades() {
        return termGrades;
    }

    public void setTermGrades(HashMap<Long, Score> termGrades) {
        this.termGrades = termGrades;
    }

    @ManyToOne(optional = true, fetch=FetchType.EAGER)
    @JoinColumn(name=HibernateConsts.SECTION_FK)
    @Fetch(FetchMode.JOIN)
    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
        //TODO: should we do this to make sure that if you call the setter, the section also contains this sectionGrade?
        //section.addStudentSectionGrade(this);
    }

    @ManyToOne(optional = true, fetch=FetchType.EAGER)
    @JoinColumn(name = HibernateConsts.STUDENT_FK)
    @Fetch(FetchMode.JOIN)
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
                ", section=" + (section !=null ? section.getId() : null) +
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
            sectionGrade.setStudent(student);
            return sectionGrade;
        }

        @Override
        protected StudentSectionGradeBuilder me() {
            return this;
        }

        @Override
        public StudentSectionGrade getInstance() {
            return new StudentSectionGrade();
        }
    }
}
