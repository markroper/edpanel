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
        super(AssignmentType.ATTENDANCE);
    }
    
    public AttendanceAssignment(AttendanceAssignment assignment) {
        super(assignment);
        this.date = assignment.date;
    }

    @Override
    public void mergePropertiesIfNull(Assignment mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if(null == mergeFrom || !(mergeFrom instanceof AttendanceAssignment)) {
            return;
        }
        AttendanceAssignment attendance = (AttendanceAssignment) mergeFrom;
        if(null == this.date) {
            this.date = attendance.date;
        }
    }
    
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    @Override
    public boolean equals(Object obj) {
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
