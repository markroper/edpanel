package com.scholarscore.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Expresses a course as a collection of specific assignments.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Course implements Serializable, IApiModel<Course> {
    private Long id;
    private Map<String, List<Assignment>> assignmentsByType;

    public Course() {
    }

    @Override
    public void mergePropertiesIfNull(Course mergeFrom) {
        if(null == mergeFrom) {
            return;
        }
        if(null == this.id) {
            this.id = mergeFrom.id;
        }
        if(null == this.assignmentsByType) {
            this.assignmentsByType = mergeFrom.assignmentsByType;
        } 
    }
    
    public Map<String, List<Assignment>> getAssignmentsByType() {
        return assignmentsByType;
    }

    public void setAssignmentsByType(Map<String, List<Assignment>> assignmentsByType) {
        this.assignmentsByType = assignmentsByType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
