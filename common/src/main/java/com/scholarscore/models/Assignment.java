package com.scholarscore.models;

import com.scholarscore.models.serializers.*;

import java.io.Serializable;
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
    protected AssignmentType type;

    /**
     * Default constructor used by the serializer
     */
    public Assignment() {

    }
    
    /**
     * Copy constructor used to clone entities
     * @param assignment
     */
    public Assignment(Assignment assignment) {
        super(assignment);
        this.type = assignment.type;
    }
    
    public void mergePropertiesIfNull(Assignment assignment) {
        super.mergePropertiesIfNull(assignment);
        if(null == assignment) {
            return;
        }
        if(null == this.type) {
            this.type = assignment.type;
        }
    }
    
    public AssignmentType getType() {
        return this.type;
    }
    
    public void setType(AssignmentType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if(! super.equals(obj)) {
            return false;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Assignment other = (Assignment) obj;
        return Objects.equals(this.type, other.type);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(type);
    }
    
}
