package com.scholarscore.models;

import com.scholarscore.models.serializers.*;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Base class for all assignment subclasses encapsulating shared attributes and behaviors.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
@JsonDeserialize(using = AssignmentDeserializer.class)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public abstract class Assignment implements Serializable {
    private Long id;
    private String type;
    private String name;
    private Long courseId;

    public Assignment() {

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

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
}
