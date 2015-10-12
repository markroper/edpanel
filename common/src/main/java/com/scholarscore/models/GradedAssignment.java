package com.scholarscore.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;

/**
 * Represents a graded assignment such as a quiz, test, homework, lab, or other.
 * 
 * @author markroper
 * @see Assignment
 *
 */
@Entity
@Table(name = HibernateConsts.ASSIGNMENT_TABLE)
@DiscriminatorValue(value = "GradedAssignment")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GradedAssignment extends Assignment implements Serializable {
    private Date assignedDate;

    public GradedAssignment() {
        super();
    }
    
    public GradedAssignment(GradedAssignment assignment) {
        super(assignment);
        this.assignedDate = assignment.assignedDate;
    }

    @Override
    public void mergePropertiesIfNull(Assignment mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if(null == mergeFrom || !(mergeFrom instanceof GradedAssignment)) {
            return;
        }
        GradedAssignment graded = (GradedAssignment) mergeFrom;
        if(null == this.assignedDate) {
            this.assignedDate = graded.assignedDate;
        }
    }

    @Column(name = HibernateConsts.ASSIGNMENT_ASSIGNED_DATE)
    public Date getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(Date assignedDate) {
        this.assignedDate = assignedDate;
    }

    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        final GradedAssignment other = (GradedAssignment) obj;
        return Objects.equals(this.assignedDate, other.assignedDate);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(assignedDate);
    }

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public class GradedAssignmentBuilder extends AssignmentBuilder<GradedAssignment>{
        private Date assignedDate;

        public GradedAssignmentBuilder withAssignedDate(final Date assignedDate){
            this.assignedDate = assignedDate;
            return this;
        }

        public GradedAssignment build(){
            GradedAssignment gradedAssignment = super.build();
            gradedAssignment.setAssignedDate(assignedDate);
            return gradedAssignment;
        }

        @Override
        public GradedAssignment getInstance() {
            return new GradedAssignment();
        }
    }
}
