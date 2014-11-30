package com.scholarscore.models;

import com.scholarscore.models.serializers.*;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.validation.constraints.Size;

/**
 * Base class for all assignment subclasses encapsulating shared attributes and behaviors.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
@JsonDeserialize(using = AssignmentDeserializer.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Assignment implements Serializable {
    protected Long id;
    protected String type;
    @Size(min=1, max=256)
    protected String name;

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
        this.id = assignment.id;
        this.type = assignment.type;
        this.name = assignment.name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
}
