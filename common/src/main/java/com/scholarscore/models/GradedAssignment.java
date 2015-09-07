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
@Table(name = ColumnConsts.ASSIGNMENT_TABLE)
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

    @Column(name = ColumnConsts.ASSIGNMENT_ASSIGNED_DATE)
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
}
