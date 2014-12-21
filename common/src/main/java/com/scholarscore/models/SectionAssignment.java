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
    private Long sectionId;
    private Date assignedDate;
    private Date dueDate;
    //TODO: Grade weight
    private transient Assignment assignment;
    private Long assignmentId;
    
    public SectionAssignment() {
        super();
    }
    
    public SectionAssignment(SectionAssignment sa) {
        super(sa);
        this.sectionId = sa.sectionId;
        this.assignedDate = sa.assignedDate;
        this.dueDate = sa.dueDate;
        this.assignment = sa.assignment;
        this.assignmentId = sa.assignmentId;
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

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    @Override
    public void mergePropertiesIfNull(SectionAssignment mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);   
        if(null == this.sectionId) {
            this.sectionId = mergeFrom.sectionId;
        }
        if(null == this.assignedDate) {
            this.assignedDate = mergeFrom.assignedDate;
        }
        if(null == this.dueDate) {
            this.dueDate = mergeFrom.dueDate;
        }
        if(null == this.assignment) {
            this.assignment = mergeFrom.assignment;
        }
        if(null == this.assignmentId) {
            this.assignmentId = mergeFrom.assignmentId;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        final SectionAssignment other = (SectionAssignment) obj;
        return Objects.equals(this.sectionId, other.sectionId) && 
                Objects.equals(this.assignedDate, other.assignedDate) &&
                Objects.equals(this.dueDate, other.dueDate) &&
                Objects.equals(this.assignment, other.assignment) &&
                Objects.equals(this.assignmentId, other.assignmentId);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(sectionId, assignedDate, dueDate, assignment, assignmentId);
    }
}
