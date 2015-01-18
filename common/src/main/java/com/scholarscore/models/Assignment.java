package com.scholarscore.models;

import com.scholarscore.models.serializers.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Base class for all assignment subclasses encapsulating shared attributes and behaviors.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
@JsonDeserialize(using = AssignmentDeserializerFactory.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Assignment 
        extends ApiModel implements Serializable, IApiModel<Assignment> {
    private AssignmentType type;
    private Date dueDate;
    private Long availablePoints;

    /**
     * Default constructor used by the serializer
     */
    public Assignment() {

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
    
    public AssignmentType getType() {
        return this.type;
    }
    
    public void setType(AssignmentType type) {
        this.type = type;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

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
    
}
