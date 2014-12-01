package com.scholarscore.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Expresses graded assignments such a quiz, test, homework, lab, and so on.
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
    private Date dueDate;

    public GradedAssignment() {
        super();
        this.type = GRADED;
    }
    
    public GradedAssignment(GradedAssignment assignment) {
        super(assignment);
        this.assignedDate = assignment.assignedDate;
        this.dueDate = assignment.dueDate;
        this.type = GRADED;
    }

    public Date getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(Date assignedDate) {
        this.assignedDate = assignedDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if(!super.equals(obj)) {
            return false;
        }
        final GradedAssignment other = (GradedAssignment) obj;
        return Objects.equals(this.dueDate, other.dueDate) && Objects.equals(this.assignedDate, other.assignedDate);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(dueDate, assignedDate);
    }
}
