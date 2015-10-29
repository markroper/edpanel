package com.scholarscore.models.assignment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.ApiModel;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.IApiModel;
import com.scholarscore.models.WeightedGradable;
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
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Represents the student's performance on an assignment in a specific course.
 * 
 * @author markroper
 *
 */
@Entity(name = HibernateConsts.STUDENT_ASSIGNMENT_TABLE)
@Table(name = HibernateConsts.STUDENT_ASSIGNMENT_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentAssignment extends ApiModel implements Serializable, WeightedGradable, IApiModel<StudentAssignment> {
    private Boolean completed;
    private Date completionDate;
    private Double awardedPoints;
    private Assignment assignment;
    private Student student;

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

    @Column(name = HibernateConsts.STUDENT_ASSIGNMENT_COMPLETED)
    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name=HibernateConsts.ASSIGNMENT_FK)
    @Fetch(FetchMode.JOIN)
    public Assignment getAssignment() {
        return assignment;
    }

    @Override
    @Column(name = HibernateConsts.STUDENT_ASSIGNMENT_AWARDED_POINTS)
    public Double getAwardedPoints() {
        return awardedPoints != null ? awardedPoints : null;
    }

    @Override
    @JsonIgnore
    @Transient
    public Long getAvailablePoints() {
        Long availablePoints = assignment != null ? assignment.getAvailablePoints() : null;
        return (availablePoints != null ? availablePoints : null );
    }

    @Override
    @JsonIgnore
    @Transient
    public int getWeight() {
        // Today, these weights live in GradeFormula and can't be 
        // directly grabbed from StudentAssignment.
        //TODO: then why is this here?  remove me
        return 1;
    }

    public void setAwardedPoints(Double awardedPoints) {
        this.awardedPoints = awardedPoints;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name=HibernateConsts.STUDENT_FK)
    @Fetch(FetchMode.JOIN)
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Column(name = HibernateConsts.STUDENT_ASSIGNMENT_COMPLETION_DATE)
    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = HibernateConsts.STUDENT_ASSIGNMENT_ID)
    public Long getId() {
        return super.getId();
    }

    @Override
    @Column(name = HibernateConsts.STUDENT_ASSIGNMENT_NAME)
    public String getName() {
        return super.getName();
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

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static class StudentAssignmentBuilder extends ApiModelBuilder<StudentAssignmentBuilder, StudentAssignment> {
        private Boolean completed;
        private Date completionDate;
        private Double awardedPoints;
        private Assignment assignment;
        private Student student;

        public StudentAssignmentBuilder withCompleted(final Boolean completed){
            this.completed = completed;
            return this;
        }

        public StudentAssignmentBuilder withCompletionDate(final Date completionDate){
            this.completionDate = completionDate;
            return this;
        }

        public StudentAssignmentBuilder withAwardedPoints(final Double awardedPoints){
            this.awardedPoints = awardedPoints;
            return this;
        }

        public StudentAssignmentBuilder withAssignment(final Assignment assignment){
            this.assignment = assignment;
            return this;
        }

        public StudentAssignmentBuilder withStudent(final Student student){
            this.student = student;
            return this;
        }

        public StudentAssignment build(){
            StudentAssignment studentAssignment = super.build();
            studentAssignment.setCompleted(completed);
            studentAssignment.setCompletionDate(completionDate);
            studentAssignment.setAwardedPoints(awardedPoints);
            studentAssignment.setAssignment(assignment);
            studentAssignment.setStudent(student);
            return studentAssignment;
        }

        @Override
        protected StudentAssignmentBuilder me() {
            return this;
        }

        @Override
        public StudentAssignment getInstance() {
            return new StudentAssignment();
        }
    }
}