package com.scholarscore.models.query;

import com.scholarscore.models.AttendanceAssignment;
import com.scholarscore.models.GradedAssignment;

/**
 * Enumerates the supported measures, or scalar values, supported within the 
 * reporting engine.  Think of these as the value aggregated in a SQL query, 
 * it should be reduced to to a numeric value, the thing being counted.
 * 
 * Each enum value contains an array of the associated available classes
 * that can be used to resolve additional information about the Measure and its
 * use.  The enum also contains an array of Dimensions that are incompatible with 
 * that Measure.
 * 
 * Invalid Dimensions & Measure combinations:
 * 1) GPA & ATTENDENCE Measures cannot be paired with TEACHER Dimension in a query 
 * because there is no valid join between either of these and the teacher table.
 * 2) TODO: identify any other invalid combinations?
 * 
 * @author markroper
 *
 */
public enum Measure {
    ATTENDANCE(new Class[]{ AttendanceAssignment.class }, new Dimension[]{ Dimension.TEACHER }),
    //Behavioral measures
    DEMERITS( new Class[]{}, new Dimension[]{} ),
    MERITS( new Class[]{}, new Dimension[]{} ),
    DETENTIONS( new Class[]{}, new Dimension[]{} ),
    HOMEWORK_CLUBS( new Class[]{}, new Dimension[]{} ),
    SUSPENSIONS( new Class[]{}, new Dimension[]{} ),
    //Academic measures
    GPA( new Class[]{}, new Dimension[]{ Dimension.TEACHER } ),
    COURSE_GRADE( new Class[]{}, new Dimension[]{} ),
    ASSIGNMENT_GRADE( new Class[]{ GradedAssignment.class }, new Dimension[]{} ), //Could be test, HW, report, presentation, & so on
    HW_COMPLETION( new Class[]{}, new Dimension[]{} );
    
    @SuppressWarnings("rawtypes")
    private Class[] availableClasses;
    private Dimension[] invalidDimensions;
    
    @SuppressWarnings("rawtypes")
    private Measure(Class[] availableFields, Dimension[] invalidDimensions) {
        this.availableClasses = availableFields;
        this.invalidDimensions = invalidDimensions;
    }

    @SuppressWarnings("rawtypes")
    public Class[] getAvailableFields() {
        return availableClasses;
    }

    public Dimension[] getInvalidDimensions() {
        return invalidDimensions;
    }
}