package com.scholarscore.models.query;

/**
 * Enumerates the supported measures, or scalar values, supported within the 
 * reporting engine.  Think of these as the value aggregated in a SQL query, 
 * it should be reduceable to to a numeric value, the thing being counted.
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
    ASSIGNMENT_GRADE, //Could be test, HW, report, presentation, & so on
    HW_COMPLETION;
}