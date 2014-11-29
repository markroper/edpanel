package com.scholarscore.models;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Expresses attendance to a single class on a specific date as a subclass of Assignment.
 * 
 * @author markroper
 * @see com.scholarscore.models.Assignment
 *
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttendanceAssignment extends Assignment implements Serializable {
    private Date date;

    public AttendanceAssignment() {

    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
