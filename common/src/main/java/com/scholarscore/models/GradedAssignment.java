package com.scholarscore.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represents a graded assignment such as a quiz, test, homework, lab, or other.
 * 
 * @author markroper
 * @see Assignment
 *
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GradedAssignment extends Assignment implements Serializable {
    public static final String GRADED = "GRADED";
    private Date assignedDate;

    public GradedAssignment() {
        super();
        this.type = AssignmentType.GRADED;
    }
    
    public GradedAssignment(GradedAssignment assignment) {
        super(assignment);
        this.assignedDate = assignment.assignedDate;
        this.type = AssignmentType.GRADED;
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
