package com.scholarscore.models.assignment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.scholarscore.models.ApiModel;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.IApiModel;
import com.scholarscore.models.Section;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

/**
 * Base class for all assignment subclasses encapsulating shared attributes and behaviors.
 *
 * @author markroper
 *
 */
@Entity(name = HibernateConsts.ASSIGNMENT_TABLE)
@Table(name = HibernateConsts.ASSIGNMENT_TABLE)
@DiscriminatorColumn(name="assignment_class", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AttendanceAssignment.class, name="ATTENDANCE"),
    @JsonSubTypes.Type(value = GradedAssignment.class, name = "HOMEWORK"),
    @JsonSubTypes.Type(value = GradedAssignment.class, name = "QUIZ"),
    @JsonSubTypes.Type(value = GradedAssignment.class, name = "TEST"),
    @JsonSubTypes.Type(value = GradedAssignment.class, name = "MIDTERM"),
    @JsonSubTypes.Type(value = GradedAssignment.class, name = "FINAL"),
    @JsonSubTypes.Type(value = GradedAssignment.class, name = "LAB"),
    @JsonSubTypes.Type(value = GradedAssignment.class, name = "CLASSWORK"),
    @JsonSubTypes.Type(value = GradedAssignment.class, name = "WRITTEN_WORK"),
    @JsonSubTypes.Type(value = GradedAssignment.class, name = "PARTICIPATION"),
    @JsonSubTypes.Type(value = GradedAssignment.class, name = "INTERIM_ASSESSMENT"),
    @JsonSubTypes.Type(value = GradedAssignment.class, name = "SUMMATIVE_ASSESSMENT"),
    @JsonSubTypes.Type(value = GradedAssignment.class, name = "EXAM"),
    @JsonSubTypes.Type(value = GradedAssignment.class, name = "PROJECT"),
    @JsonSubTypes.Type(value = GradedAssignment.class, name = "USER_DEFINED")
})
public abstract class Assignment 
        extends ApiModel implements Serializable, IApiModel<Assignment> {
    private AssignmentType type;
    private String userDefinedType;
    private LocalDate dueDate;
    private Long availablePoints;
    protected transient Section section;
    protected Long sectionFK;
    protected Double weight;
    protected Boolean includeInFinalGrades;
    protected String sourceSystemId;

    /**
     * Default constructor used by the serializer
     */
    public Assignment() {

    }

    @OneToOne
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
        this.userDefinedType = assignment.userDefinedType;
        this.dueDate = assignment.dueDate;
        this.availablePoints = assignment.availablePoints;
        this.weight = assignment.weight;
        this.includeInFinalGrades = assignment.includeInFinalGrades;
        this.sourceSystemId = assignment.sourceSystemId;
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
        if(null == this.weight) {
            this.weight = assignment.weight;
        }
        if(null == this.userDefinedType) {
            this.userDefinedType = assignment.userDefinedType;
        }
        if(null == this.includeInFinalGrades) {
            this.includeInFinalGrades = assignment.includeInFinalGrades;
        }
        if(null == this.sourceSystemId) {
            this.sourceSystemId = assignment.sourceSystemId;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Enumerated(EnumType.STRING)
    public AssignmentType getType() {
        return this.type;
    }
    
    public void setType(AssignmentType type) {
        this.type = type;
    }

    @Column(name = HibernateConsts.ASSIGNMENT_DUE_DATE)
    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @Column(name = HibernateConsts.ASSIGNMENT_AVAILABLE_POINTS)
    public Long getAvailablePoints() {
        return availablePoints;
    }

    public void setAvailablePoints(Long availablePoints) {
        this.availablePoints = availablePoints;
    }

    @Column(name = HibernateConsts.ASSIGNMENT_WEIGHT)
    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @Column(name = HibernateConsts.ASSIGNMENT_USER_DEFINED_TYPE)
    public String getUserDefinedType() {
        return userDefinedType;
    }

    public void setUserDefinedType(String userDefinedType) {
        this.userDefinedType = userDefinedType;
    }

    @Column(name = HibernateConsts.ASSIGNMENT_INCLUDE_IN_FINAL_GRADES)
    public Boolean getIncludeInFinalGrades() {
        return includeInFinalGrades;
    }

    public void setIncludeInFinalGrades(Boolean includeInFinalGrades) {
        this.includeInFinalGrades = includeInFinalGrades;
    }

    @Column(name = HibernateConsts.ASSIGNMENT_SOURCE_SYSTEM_ID)
    public String getSourceSystemId() {
        return sourceSystemId;
    }

    public void setSourceSystemId(String sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
    }

    @Override
    public boolean equals(Object obj) {
        if(! super.equals(obj)) {
            return false;
        }
        final Assignment other = (Assignment) obj;
        return Objects.equals(this.type, other.type) 
                && Objects.equals(this.dueDate, other.dueDate)
                && Objects.equals(this.weight, other.weight)
                && Objects.equals(this.includeInFinalGrades, other.includeInFinalGrades)
                && Objects.equals(this.userDefinedType, other.userDefinedType)
                && Objects.equals(this.sourceSystemId, other.sourceSystemId)
                && Objects.equals(this.availablePoints, other.availablePoints);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(type, dueDate, sourceSystemId, userDefinedType, weight, includeInFinalGrades, availablePoints);
    }

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static abstract class AssignmentBuilder<U extends AssignmentBuilder<U, T>, T extends Assignment> extends ApiModelBuilder<U,T>{

        private AssignmentType type;
        private LocalDate dueDate;
        private Long availablePoints;
        protected transient Section section;
        protected Long sectionFK;
        protected Double weight;
        protected String userDefinedType;
        protected Boolean includeInFinalGrades;
        protected String sourceSystemId;

        public U withIncludeInfinalGrades(final Boolean b) {
            this.includeInFinalGrades = b;
            return me();
        }

        public U withType(final AssignmentType type){
            this.type = type;
            return me();
        }

        public U withSourceSystemId(final String ssid){
            this.sourceSystemId = ssid;
            return me();
        }

        public U withUserDefinedType(final String type){
            this.userDefinedType = type;
            return me();
        }

        public U withDueDate(final LocalDate dueDate){
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

        public U withWeight(final Double weight){
            this.weight = weight;
            return me();
        }

        public T build(){
            T assignment = super.build();
            assignment.setType(type);
            assignment.setDueDate(dueDate);
            assignment.setWeight(weight);
            assignment.setSourceSystemId(sourceSystemId);
            assignment.setUserDefinedType(userDefinedType);
            assignment.setIncludeInFinalGrades(includeInFinalGrades);
            assignment.setAvailablePoints(availablePoints);
            assignment.setSection(section);
            //TODO: should we make this reciprocal?
            //section.addAssignment(assignment);
            assignment.setSectionFK(sectionFK);
            return assignment;
        }
    }
    
}
