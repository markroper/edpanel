package com.scholarscore.models.query;

import com.scholarscore.models.query.measure.*;
import com.scholarscore.models.query.measure.behavior.DemeritMeasure;
import com.scholarscore.models.query.measure.behavior.DetentionMeasure;
import com.scholarscore.models.query.measure.behavior.InSchoolSuspensionMeasure;
import com.scholarscore.models.query.measure.behavior.MeritMeasure;
import com.scholarscore.models.query.measure.behavior.OutOfSchoolSuspensionMeasure;
import com.scholarscore.models.query.measure.behavior.ReferralMeasure;

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
    // All measures require a specific dimension and a Measure class
    //Section Attendance
    SECTION_ABSENCE(Dimension.ATTENDANCE, SectionAbsenceMeasure.class),
    SECTION_TARDY(Dimension.ATTENDANCE, SectionTardyMeasure.class),
    //Daily Attendance
    ATTENDANCE(Dimension.ATTENDANCE, AttendanceMeasure.class),
    ABSENCE(Dimension.ATTENDANCE, DailyAbsenceMeasure.class),
    TARDY(Dimension.ATTENDANCE, DailyTardyMeasure.class),
    //Behavioral measures
    DEMERIT(Dimension.BEHAVIOR, DemeritMeasure.class),
    MERIT(Dimension.BEHAVIOR, MeritMeasure.class),
    DETENTION(Dimension.BEHAVIOR, DetentionMeasure.class),
//    PRIDE_SCORE,  // TODO: need IMeasure class and SqlSerializer for pride-score before it can be used
    REFERRAL(Dimension.BEHAVIOR, ReferralMeasure.class),
    IN_SCHOOL_SUSPENSION(Dimension.BEHAVIOR, InSchoolSuspensionMeasure.class),
    OUT_OF_SCHOOL_SUSPENSION(Dimension.BEHAVIOR, OutOfSchoolSuspensionMeasure.class),
    //Academic measures
    GPA(Dimension.GPA, GpaMeasure.class),
    CURRENT_GPA(Dimension.CURRENT_GPA, CurrentGpaMeasure.class),
    COURSE_GRADE(Dimension.STUDENT_SECTION_GRADE, CourseGradeMeasure.class),
    ASSIGNMENT_GRADE(Dimension.STUDENT_ASSIGNMENT, AssignmentGradeMeasure.class),
    HW_COMPLETION(Dimension.STUDENT_ASSIGNMENT, HomeworkCompletionMeasure.class),
    GOAL(Dimension.GOAL, GoalMeasure.class);

    private Dimension dimension;
    private Class<? extends IMeasure> measureClass;
    
    Measure(Dimension d, Class<? extends IMeasure> measureClass) {
        this.dimension = d;
        this.measureClass = measureClass;
    }

    public Dimension getDimension() {
        return dimension;
    }
    /**
     * Factory method for constructing an IMeasure instance of type measure.
     * @return
     */
    public IMeasure buildMeasure() {
        try {
            return measureClass.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            throw new QueryException("Cannot build measure class " + this.getClass().getSimpleName() + "!");
        }
   }
}