package com.scholarscore.models.assignment;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.scholarscore.models.ApiModel;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.IApiModel;
import com.scholarscore.models.Section;
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
@Entity(name = HibernateConsts.ASSIGNMENT_TABLE)
@Table(name = HibernateConsts.ASSIGNMENT_TABLE)
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
    protected transient Section section;
    protected Long sectionFK;

    /**
     * Default constructor used by the serializer
     */
    public Assignment() {

    }

    @OneToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name=HibernateConsts.SECTION_FK, insertable = false, updatable = false)
    @Transient
    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
        //TODO: should we have this here to make it reciprocal?
        //this.section.addAssignment(this);
    }

    @Column(name = HibernateConsts.SECTION_FK)
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
    @Column(name = HibernateConsts.ASSIGNMENT_ID)
    public Long getId() {
        return super.getId();
    }

    @Override
    @Column(name = HibernateConsts.ASSIGNMENT_NAME)
    public String getName() {
        return super.getName();
    }

    @Column(name = HibernateConsts.ASSIGNMENT_TYPE_FK)
    public AssignmentType getType() {
        return this.type;
    }
    
    public void setType(AssignmentType type) {
        this.type = type;
    }

    @Column(name = HibernateConsts.ASSIGNMENT_DUE_DATE)
    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @Column(name = HibernateConsts.ASSIGNMENT_AVAILABLE_POINTS)
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

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static abstract class AssignmentBuilder<U extends AssignmentBuilder<U, T>, T extends Assignment> extends ApiModelBuilder<U,T>{

        private AssignmentType type;
        private Date dueDate;
        private Long availablePoints;
        protected transient Section section;
        protected Long sectionFK;


        public U withType(final AssignmentType type){
            this.type = type;
            return me();
        }

        public U withDueDate(final Date dueDate){
            this.dueDate = dueDate;
            return me();
        }

        public U withAvailablePoints(final Long availablePoints){
            this.availablePoints = availablePoints;
            return me();
        }

        public U withSection(final Section section){
            this.section = section;
            return me();
        }

        public U withSectionFK(final Long sectionFK){
            this.sectionFK = sectionFK;
            return me();
        }

        public T build(){
            T assignment = super.build();
            assignment.setType(type);
            assignment.setDueDate(dueDate);
            assignment.setAvailablePoints(availablePoints);
            assignment.setSection(section);
            //TODO: should we make this reciprocal?
            //section.addAssignment(assignment);
            assignment.setSectionFK(sectionFK);
            return assignment;
        }
    }
    
}
