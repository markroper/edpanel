package com.scholarscore.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Base class for all assignment subclasses encapsulating shared attributes and behaviors.
 *
 * @author markroper
 *
 */
@Entity(name = "assignment")
@Table(name = "assignment")
@DiscriminatorColumn(name="assignmentClass", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = AttendanceAssignment.class, name="ATTENDANCE"),
    @JsonSubTypes.Type(value = GradedAssignment.class, name = "HOMEWORK"),
    @JsonSubTypes.Type(value = GradedAssignment.class, name = "QUIZ"),
    @JsonSubTypes.Type(value = GradedAssignment.class, name = "TEST"),
    @JsonSubTypes.Type(value = GradedAssignment.class, name = "MIDTERM"),
    @JsonSubTypes.Type(value = GradedAssignment.class, name = "FINAL"),
    @JsonSubTypes.Type(value = GradedAssignment.class, name = "LAB"),
    @JsonSubTypes.Type(value = GradedAssignment.class, name = "CLASSWORK"),
    @JsonSubTypes.Type(value = GradedAssignment.class, name = "USER_DEFINED")
})
public abstract class Assignment 
        extends ApiModel implements Serializable, IApiModel<Assignment> {
    private AssignmentType type;
    private Date dueDate;
    private Long availablePoints;
    protected Section section;
    protected Long sectionFK;

    /**
     * Default constructor used by the serializer
     */
    public Assignment() {

    }

    @OneToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name="section_fk", insertable = false, updatable = false)
    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    @Column(name = "section_fk")
    public Long getSectionFK() {
        return sectionFK;
    }

    public void setSectionFK(Long sectionFK) {
        this.sectionFK = sectionFK;
    }

    public Assignment(AssignmentType assignmentType) {
        this.type = assignmentType;
    }
    
    /**
     * Copy constructor used to clone entities
     * @param assignment
     */
    public Assignment(Assignment assignment) {
        super(assignment);
        this.type = assignment.type;
        this.dueDate = assignment.dueDate;
        this.availablePoints = assignment.availablePoints;
    }
    
    public void mergePropertiesIfNull(Assignment assignment) {
        super.mergePropertiesIfNull(assignment);
        if(null == assignment) {
            return;
        }
        if(null == this.type) {
            this.type = assignment.type;
        }
        if(null == this.dueDate) {
            this.dueDate = assignment.dueDate;
        }
        if(null == this.availablePoints) {
            this.availablePoints = assignment.availablePoints;
        }
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "assignment_id")
    public Long getId() {
        return super.getId();
    }

    @Override
    @Column(name = "assignment_name")
    public String getName() {
        return super.getName();
    }

    @Column(name = "type_fk")
    public AssignmentType getType() {
        return this.type;
    }
    
    public void setType(AssignmentType type) {
        this.type = type;
    }

    @Column(name = "due_date")
    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @Column(name = "available_points")
    public Long getAvailablePoints() {
        return availablePoints;
    }

    public void setAvailablePoints(Long availablePoints) {
        this.availablePoints = availablePoints;
    }

    @Override
    public boolean equals(Object obj) {
        if(! super.equals(obj)) {
            return false;
        }
        final Assignment other = (Assignment) obj;
        return Objects.equals(this.type, other.type) 
                && Objects.equals(this.dueDate, other.dueDate) 
                && Objects.equals(this.availablePoints, other.availablePoints);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(type, dueDate, availablePoints);
    }
    
}
