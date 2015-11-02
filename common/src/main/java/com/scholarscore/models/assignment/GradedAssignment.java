package com.scholarscore.models.assignment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.HibernateConsts;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Represents a graded assignment such as a quiz, test, homework, lab, or other.
 * 
 * @author markroper
 * @see Assignment
 *
 */
@Entity
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
    public static class GradedAssignmentBuilder extends AssignmentBuilder<GradedAssignmentBuilder, GradedAssignment>{
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
        protected GradedAssignmentBuilder me() {
            return this;
        }

        @Override
        public GradedAssignment getInstance() {
            return new GradedAssignment();
        }
    }
}
