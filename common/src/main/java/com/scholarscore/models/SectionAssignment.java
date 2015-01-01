package com.scholarscore.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * SectionAssignment is to Assignment as Section is to Course.  A SectionAssignment is 
 * a temporal instance of an assignment.  In addition to an Assignment reference, a SectionAssignment
 * has a due date, a grade weighting for the section it is in, and a reference to a section, among other
 * attributes.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SectionAssignment extends ApiModel implements Serializable, IApiModel<SectionAssignment> {
    private Date assignedDate;
    private Date dueDate;
    private Long availablePoints;
    //TODO: Grade weight
    private transient Assignment assignment;
    
    public SectionAssignment() {
        super();
    }
    
    public SectionAssignment(SectionAssignment sa) {
        super(sa);
        this.assignedDate = sa.assignedDate;
        this.dueDate = sa.dueDate;
        this.assignment = sa.assignment;
        this.availablePoints = sa.availablePoints;
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

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public Long getAvailablePoints() {
        return availablePoints;
    }

    public void setAvailablePoints(Long availablePoints) {
        this.availablePoints = availablePoints;
    }

    @Override
    public void mergePropertiesIfNull(SectionAssignment mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);   
        if(null == this.assignedDate) {
            this.assignedDate = mergeFrom.assignedDate;
        }
        if(null == this.dueDate) {
            this.dueDate = mergeFrom.dueDate;
        }
        if(null == this.assignment) {
            this.assignment = mergeFrom.assignment;
        }
        if(null == this.availablePoints) {
            this.availablePoints = mergeFrom.availablePoints;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        final SectionAssignment other = (SectionAssignment) obj;
        return  Objects.equals(this.assignedDate, other.assignedDate) &&
                Objects.equals(this.dueDate, other.dueDate) &&
                Objects.equals(this.assignment, other.assignment) &&
                Objects.equals(this.availablePoints, other.availablePoints);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(assignedDate, dueDate, assignment, availablePoints);
    }
}
