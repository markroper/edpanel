package com.scholarscore.models.query;

import com.scholarscore.models.query.measure.AssignmentGradeMeasure;
import com.scholarscore.models.query.measure.CourseGradeMeasure;
import com.scholarscore.models.query.measure.GpaMeasure;
import com.scholarscore.models.query.measure.HomeworkCompletionMeasure;
import com.scholarscore.models.query.measure.IMeasure;

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
    ATTENDANCE,
    //Behavioral measures
    DEMERITS,
    MERITS,
    DETENTIONS,
    HOMEWORK_CLUBS,
    SUSPENSIONS,
    //Academic measures
    GPA,
    COURSE_GRADE,
    ASSIGNMENT_GRADE,
    HW_COMPLETION;
    
    /**
     * Factory method for constructing an IMeasure instance of type measure.
     * @param measure
     * @return
     */
    public static IMeasure buildMeasure(Measure measure) {
        switch(measure) {
            case COURSE_GRADE:
                return new CourseGradeMeasure();
            case ASSIGNMENT_GRADE:
                return new AssignmentGradeMeasure();
            case HW_COMPLETION:
                return new HomeworkCompletionMeasure();
            case GPA:
                return new GpaMeasure();
            default:
                return null;   
        }
    }
}