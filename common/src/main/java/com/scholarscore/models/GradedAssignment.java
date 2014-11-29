package com.scholarscore.models;

import java.io.Serializable;
import java.util.Date;

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
    private Date assignedDate;
    private Date dueDate;

    public GradedAssignment() {
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
}
