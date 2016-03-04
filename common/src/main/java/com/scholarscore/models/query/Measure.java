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
    //Section Attendance
    SECTION_ABSENCE(Dimension.ATTENDANCE),
    SECTION_TARDY(Dimension.ATTENDANCE),
    //Daily Attendance
    ATTENDANCE(Dimension.ATTENDANCE),
    ABSENCE(Dimension.ATTENDANCE),
    TARDY(Dimension.ATTENDANCE),
    //Behavioral measures
    DEMERIT(Dimension.BEHAVIOR),
    MERIT(Dimension.BEHAVIOR),
    DETENTION(Dimension.BEHAVIOR),
//    PRIDE_SCORE,  // TODO: need IMeasure class and SqlSerializer for pride-score before it can be used
    REFERRAL(Dimension.BEHAVIOR),
    IN_SCHOOL_SUSPENSION(Dimension.BEHAVIOR),
    OUT_OF_SCHOOL_SUSPENSION(Dimension.BEHAVIOR),
    //Academic measures
    GPA(Dimension.GPA),
    CURRENT_GPA(Dimension.CURRENT_GPA),
    COURSE_GRADE(Dimension.STUDENT_SECTION_GRADE),
    ASSIGNMENT_GRADE(Dimension.STUDENT_ASSIGNMENT),
    HW_COMPLETION(Dimension.STUDENT_ASSIGNMENT),
    GOAL(Dimension.GOAL);

    private Dimension dimension;

    Measure(Dimension d) {
        this.dimension = d;
    }

    public Dimension getDimension() {
        return dimension;
    }
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
            case MERIT:
                return new MeritMeasure();
            case DEMERIT:
                return new DemeritMeasure();
            case DETENTION:
                return new DetentionMeasure();
            case IN_SCHOOL_SUSPENSION:
                return new InSchoolSuspensionMeasure();
            case OUT_OF_SCHOOL_SUSPENSION:
                return new OutOfSchoolSuspensionMeasure();
            case REFERRAL:
                return new ReferralMeasure();
            case ATTENDANCE:
                return new AttendanceMeasure();
            case ABSENCE:
                return new DailyAbsenceMeasure();
            case TARDY:
                return new DailyTardyMeasure();
            case SECTION_ABSENCE:
                return new SectionAbsenceMeasure();
            case SECTION_TARDY:
                return new SectionTardyMeasure();
            case CURRENT_GPA:
                return new CurrentGpaMeasure();
            default:
                throw new QueryException("Unsupported measure " + measure + "!");
        }
    }
}