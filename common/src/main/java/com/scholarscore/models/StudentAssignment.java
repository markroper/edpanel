package com.scholarscore.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Represents the students performance on an assignment in a specific course.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public abstract class StudentAssignment implements Serializable {
    private Assignment assignment;
    private Boolean completed;
    private IGrade grade;

    public StudentAssignment() {

    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public IGrade getGrade() {
        return grade;
    }

    public void setGrade(IGrade grade) {
        this.grade = grade;
    }


}
