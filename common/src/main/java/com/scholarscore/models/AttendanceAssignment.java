package com.scholarscore.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

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
    public static final String ATTENDANCE = "ATTENDANCE";
    private Date date;

    public AttendanceAssignment() {
        super();
        this.type = ATTENDANCE;
    }
    
    public AttendanceAssignment(AttendanceAssignment assignment) {
        super(assignment);
        this.date = assignment.date;
        this.type = ATTENDANCE;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if(!super.equals(obj)) {
            return false;
        }
        final AttendanceAssignment other = (AttendanceAssignment) obj;
        return Objects.equals(this.date, other.date);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(date);
    }
}
