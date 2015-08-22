package com.scholarscore.models;

import java.io.Serializable;

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

    public AttendanceAssignment() {
        super(AssignmentType.ATTENDANCE);
    }
    
    public AttendanceAssignment(AttendanceAssignment assignment) {
        super(assignment);
    }

    @Override
    public void mergePropertiesIfNull(Assignment mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if(null == mergeFrom || !(mergeFrom instanceof AttendanceAssignment)) {
            return;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        final AttendanceAssignment other = (AttendanceAssignment) obj;
        return true;
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode();
    }
}
