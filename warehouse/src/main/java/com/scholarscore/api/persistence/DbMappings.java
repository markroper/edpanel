package com.scholarscore.api.persistence;

import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;
import com.scholarscore.models.query.Measure;
import com.scholarscore.models.query.MeasureField;
import com.scholarscore.models.query.dimension.*;
import com.scholarscore.models.query.measure.AttendanceMeasure;
import com.scholarscore.models.query.measure.behavior.BehaviorMeasure;
import com.scholarscore.models.query.measure.CourseGradeMeasure;
import com.scholarscore.models.query.measure.CurrentGpaMeasure;
import com.scholarscore.models.query.measure.GpaMeasure;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class DbMappings {
    
   public static final Map<Measure, String> MEASURE_TO_TABLE_NAME = 
           new HashMap<Measure, String>() {{
               put(Measure.COURSE_GRADE, HibernateConsts.SECTION_GRADE_TABLE);
               put(Measure.ASSIGNMENT_GRADE, HibernateConsts.STUDENT_ASSIGNMENT_TABLE);
               put(Measure.HW_COMPLETION, HibernateConsts.STUDENT_ASSIGNMENT_TABLE);
               put(Measure.DEMERIT, HibernateConsts.BEHAVIOR_TABLE);
               put(Measure.MERIT, HibernateConsts.BEHAVIOR_TABLE);
               put(Measure.IN_SCHOOL_SUSPENSION, HibernateConsts.BEHAVIOR_TABLE);
               put(Measure.OUT_OF_SCHOOL_SUSPENSION, HibernateConsts.BEHAVIOR_TABLE);
               put(Measure.DETENTION, HibernateConsts.BEHAVIOR_TABLE);
               put(Measure.REFERRAL, HibernateConsts.BEHAVIOR_TABLE);
               put(Measure.ATTENDANCE, HibernateConsts.ATTENDANCE_TABLE);
               put(Measure.ABSENCE, HibernateConsts.ATTENDANCE_TABLE);
               put(Measure.TARDY, HibernateConsts.ATTENDANCE_TABLE);
               put(Measure.GPA, HibernateConsts.GPA_TABLE);
               put(Measure.CURRENT_GPA, HibernateConsts.GPA_TABLE);
           }};
   public static final Map<Measure, String> MEASURE_TO_COL_NAME = 
           new HashMap<Measure, String>() {{
               put(Measure.COURSE_GRADE, HibernateConsts.STUDENT_SECTION_GRADE_GRADE);
               put(Measure.ASSIGNMENT_GRADE, HibernateConsts.STUDENT_ASSIGNMENT_AWARDED_POINTS);
               put(Measure.HW_COMPLETION, HibernateConsts.STUDENT_ASSIGNMENT_COMPLETED);
           }};
   public static final Map<MeasureField, String> MEASURE_FIELD_TO_COL_NAME = 
           new HashMap<MeasureField, String>() {{
               // gpa
               put(new MeasureField(Measure.GPA, GpaMeasure.DATE), HibernateConsts.GPA_CALCULATION_DATE);
               put(new MeasureField(Measure.GPA, GpaMeasure.STUDENT), HibernateConsts.STUDENT_FK);
               put(new MeasureField(Measure.GPA, GpaMeasure.TYPE), HibernateConsts.GPA_TYPE);
               put(new MeasureField(Measure.GPA, CurrentGpaMeasure.GPA), HibernateConsts.GPA_SCORE);
               put(new MeasureField(Measure.CURRENT_GPA, GpaMeasure.DATE), HibernateConsts.GPA_CALCULATION_DATE);
               put(new MeasureField(Measure.CURRENT_GPA, GpaMeasure.STUDENT), HibernateConsts.STUDENT_FK);
               put(new MeasureField(Measure.CURRENT_GPA, GpaMeasure.TYPE), HibernateConsts.GPA_TYPE);
               put(new MeasureField(Measure.CURRENT_GPA, CurrentGpaMeasure.GPA), HibernateConsts.GPA_SCORE);

               put(new MeasureField(Measure.COURSE_GRADE, CourseGradeMeasure.GRADE), HibernateConsts.SECTION_GRADE_GRADE);
               // attendance (true/false for if section should be included)
               populateAttendanceTypeMeasureToColNames(this, Measure.ATTENDANCE, true);
               populateAttendanceTypeMeasureToColNames(this, Measure.ABSENCE, false);
               populateAttendanceTypeMeasureToColNames(this, Measure.SECTION_ABSENCE, true);
               populateAttendanceTypeMeasureToColNames(this, Measure.SECTION_TARDY, true);
               populateAttendanceTypeMeasureToColNames(this, Measure.TARDY, false);
               
               // behaviors
               populateBehaviorTypeMeasureToColNames(this, Measure.DEMERIT);
               populateBehaviorTypeMeasureToColNames(this, Measure.MERIT);
               populateBehaviorTypeMeasureToColNames(this, Measure.DETENTION);
               populateBehaviorTypeMeasureToColNames(this, Measure.REFERRAL);
               populateBehaviorTypeMeasureToColNames(this, Measure.OUT_OF_SCHOOL_SUSPENSION);
               populateBehaviorTypeMeasureToColNames(this, Measure.IN_SCHOOL_SUSPENSION);
           }};

    private static final void populateBehaviorTypeMeasureToColNames(HashMap<MeasureField, String> toPopulate, Measure behaviorMeasure) {
        toPopulate.put(new MeasureField(behaviorMeasure, BehaviorMeasure.ID), HibernateConsts.BEHAVIOR_ID);
        toPopulate.put(new MeasureField(behaviorMeasure, BehaviorMeasure.STUDENT_FK), HibernateConsts.STUDENT_FK);
        toPopulate.put(new MeasureField(behaviorMeasure, BehaviorMeasure.TEACHER_FK), HibernateConsts.TEACHER_FK);
        toPopulate.put(new MeasureField(behaviorMeasure, BehaviorMeasure.DATE), HibernateConsts.BEHAVIOR_DATE);
        toPopulate.put(new MeasureField(behaviorMeasure, BehaviorMeasure.POINTS), HibernateConsts.BEHAVIOR_POINT_VALUE);
        toPopulate.put(new MeasureField(behaviorMeasure, BehaviorMeasure.CATEGORY), HibernateConsts.BEHAVIOR_CATEGORY);
        toPopulate.put(new MeasureField(behaviorMeasure, BehaviorMeasure.ROSTER), HibernateConsts.BEHAVIOR_ROSTER);
    }

    private static final void populateAttendanceTypeMeasureToColNames(HashMap<MeasureField, String> toPopulate, Measure attendanceMeasure, boolean includeSection) {
        toPopulate.put(new MeasureField(attendanceMeasure, AttendanceMeasure.ID), HibernateConsts.ATTENDANCE_ID);
        toPopulate.put(new MeasureField(attendanceMeasure, AttendanceMeasure.DATE), HibernateConsts.SCHOOL_DAY_DATE);
        toPopulate.put(new MeasureField(attendanceMeasure, AttendanceMeasure.SCHOOL_FK), HibernateConsts.SCHOOL_FK);
        toPopulate.put(new MeasureField(attendanceMeasure, AttendanceMeasure.STATUS), HibernateConsts.ATTENDANCE_STATUS);
        toPopulate.put(new MeasureField(attendanceMeasure, AttendanceMeasure.STATUS_DESCRIPTION), HibernateConsts.ATTENDANCE_DESCRIPTION);
        toPopulate.put(new MeasureField(attendanceMeasure, AttendanceMeasure.STUDENT_FK), HibernateConsts.STUDENT_FK);
        toPopulate.put(new MeasureField(attendanceMeasure, AttendanceMeasure.TYPE), HibernateConsts.ATTENDANCE_TYPE);
        if (includeSection) {
            toPopulate.put(new MeasureField(attendanceMeasure, AttendanceMeasure.SECTION_FK), HibernateConsts.SECTION_FK);
        }
    }

    public static final Map<Dimension, String> DIMENSION_TO_TABLE_NAME = 
            new HashMap<Dimension, String>() {{
                put(Dimension.SCHOOL, HibernateConsts.SCHOOL_TABLE);
                put(Dimension.COURSE, HibernateConsts.COURSE_TABLE);
                put(Dimension.SECTION, HibernateConsts.SECTION_TABLE);
                put(Dimension.TERM, HibernateConsts.TERM_TABLE);
                put(Dimension.STUDENT, HibernateConsts.STUDENT_TABLE);
                put(Dimension.TEACHER, HibernateConsts.STAFF_TABLE);
                put(Dimension.ADMINISTRATOR, HibernateConsts.STAFF_TABLE);
                put(Dimension.STAFF, HibernateConsts.STAFF_TABLE);
                put(Dimension.YEAR, HibernateConsts.SCHOOL_YEAR_TABLE);
                put(Dimension.ASSIGNMENT, HibernateConsts.ASSIGNMENT_TABLE);
                put(Dimension.USER, HibernateConsts.USERS_TABLE);
                put(Dimension.BEHAVIOR, HibernateConsts.BEHAVIOR_TABLE);
                put(Dimension.STUDENT_ASSIGNMENT, HibernateConsts.STUDENT_ASSIGNMENT_TABLE);
                put(Dimension.STUDENT_SECTION_GRADE, HibernateConsts.STUDENT_SECTION_GRADE_TABLE);
                put(Dimension.SECTION_GRADE, HibernateConsts.SECTION_GRADE_TABLE);
                put(Dimension.SCHOOL_DAY, HibernateConsts.SCHOOL_DAY_TABLE);
                put(Dimension.ATTENDANCE, HibernateConsts.ATTENDANCE_TABLE);
            }};
    
    public static final Map<String, Dimension> TABLE_NAME_TO_DIMENSION = buildReverseMap(DIMENSION_TO_TABLE_NAME);
    
    public static final Map<DimensionField, String> DIMENSION_TO_COL_NAME = 
            new HashMap<DimensionField, String>(){{
        //Student dimension field to db column name lookup
        put(new DimensionField(Dimension.STUDENT, StudentDimension.SPED), HibernateConsts.STUDENT_SPED);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.SPED_DETAIL), HibernateConsts.STUDENT_SPED_DETAIL);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.ELL), HibernateConsts.STUDENT_ELL);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.ELL_DETAIL), HibernateConsts.STUDENT_ELL_DETAIL);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.AGE), HibernateConsts.STUDENT_BIRTH_DATE);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.GENDER), HibernateConsts.STUDENT_GENDER);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.NAME), HibernateConsts.STUDENT_NAME);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.ID), HibernateConsts.STUDENT_USER_FK);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.ETHNICITY), HibernateConsts.STUDENT_FEDERAL_ETHNICITY);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.RACE), HibernateConsts.STUDENT_FEDERAL_RACE);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.HOME_ADDRESS), STUDENT_HOME_STREET_COL);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.CITY_OF_RESIDENCE), STUDENT_HOME_CITY_COL);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.SCHOOL), HibernateConsts.SCHOOL_FK);
        //Teacher dimension field to DB column name lookup
        put(new DimensionField(Dimension.TEACHER, TeacherDimension.NAME), HibernateConsts.STAFF_NAME);
        put(new DimensionField(Dimension.TEACHER, TeacherDimension.ID), HibernateConsts.STAFF_USER_FK);
        put(new DimensionField(Dimension.TEACHER, TeacherDimension.SCHOOL), HibernateConsts.SCHOOL_FK);
        put(new DimensionField(Dimension.ADMINISTRATOR, TeacherDimension.NAME), HibernateConsts.STAFF_NAME);
        put(new DimensionField(Dimension.ADMINISTRATOR, TeacherDimension.ID), HibernateConsts.STAFF_USER_FK);
        put(new DimensionField(Dimension.ADMINISTRATOR, TeacherDimension.SCHOOL), HibernateConsts.SCHOOL_FK);
        put(new DimensionField(Dimension.USER, UserDimension.ID), HibernateConsts.USER_ID);
        put(new DimensionField(Dimension.STAFF, StaffDimension.ID), HibernateConsts.STAFF_USER_FK);

        //School dimension field to DB column name lookup
        put(new DimensionField(Dimension.SCHOOL, SchoolDimension.NAME), HibernateConsts.SCHOOL_NAME);
        put(new DimensionField(Dimension.SCHOOL, SchoolDimension.ID), HibernateConsts.SCHOOL_ID);
        put(new DimensionField(Dimension.SCHOOL, SchoolDimension.ADDRESS), SCHOOL_ADDRESS_COL);
        //SECTION dimension field to DB column name lookup
        put(new DimensionField(Dimension.SECTION, SectionDimension.NAME), HibernateConsts.SECTION_NAME);
        put(new DimensionField(Dimension.SECTION, SectionDimension.ID), HibernateConsts.SECTION_ID);
        put(new DimensionField(Dimension.SECTION, SectionDimension.END_DATE), HibernateConsts.SECTION_END_DATE);
        put(new DimensionField(Dimension.SECTION, SectionDimension.START_DATE), HibernateConsts.SECTION_START_DATE);
        put(new DimensionField(Dimension.SECTION, SectionDimension.TEACHER), HibernateConsts.TEACHER_NAME);
        put(new DimensionField(Dimension.SECTION, SectionDimension.GRADE_FORMULA), HibernateConsts.SECTION_GRADE_FORMULA);
        put(new DimensionField(Dimension.SECTION, SectionDimension.ROOM), HibernateConsts.SECTION_ROOM);
        //COURSE dimension field to DB column name lookup
        put(new DimensionField(Dimension.COURSE, CourseDimension.NAME), HibernateConsts.COURSE_NAME);
        put(new DimensionField(Dimension.COURSE, CourseDimension.ID), HibernateConsts.COURSE_ID);
        //TERM dimension field to DB column name lookup
        put(new DimensionField(Dimension.TERM, TermDimension.NAME), HibernateConsts.TERM_NAME);
        put(new DimensionField(Dimension.TERM, TermDimension.ID), HibernateConsts.TERM_ID);
        put(new DimensionField(Dimension.TERM, TermDimension.END_DATE), HibernateConsts.TERM_END_DATE);
        put(new DimensionField(Dimension.TERM, TermDimension.START_DATE), HibernateConsts.TERM_START_DATE);
        //TERM dimension field to DB column name lookup
        put(new DimensionField(Dimension.YEAR, SchoolYearDimension.ID), HibernateConsts.SCHOOL_YEAR_ID);
        put(new DimensionField(Dimension.YEAR, SchoolYearDimension.END_DATE), HibernateConsts.SCHOOL_YEAR_END_DATE);
        put(new DimensionField(Dimension.YEAR, SchoolYearDimension.START_DATE), HibernateConsts.SCHOOL_YEAR_START_DATE);
        //ASSIGNMENT
        put(new DimensionField(Dimension.ASSIGNMENT, AssignmentDimension.DUE_DATE), HibernateConsts.ASSIGNMENT_DUE_DATE);
        put(new DimensionField(Dimension.ASSIGNMENT, AssignmentDimension.NAME), HibernateConsts.ASSIGNMENT_NAME);
        put(new DimensionField(Dimension.ASSIGNMENT, AssignmentDimension.ID), HibernateConsts.ASSIGNMENT_ID);
    }};

    //TODO: straggler values here should be moved to ColumnConsts or removed altogether...
    public static final String DATABASE = "scholar_warehouse";
    public static final String SCHOOL_ADDRESS_COL = "school_address";
    public static final String STUDENT_HOME_CITY_COL = "home_city";
    public static final String STUDENT_HOME_STREET_COL = "home_street";	
	public static final String AUTHORITY_TABLE = "authorities";
    public static final String AUTHORITY_USER_ID_COL = "user_id";
	public static final String AUTHORITY_AUTHORITY_COL = "authority";
	public static final String REPORT_TABLE = "report";
	public static final String REPORT_ID_COL = "report_id";
	public static final String REPORT_COL = "report";
    /**
     * Helper method converts a Java Date to a Timestamp for storage in the Database.
     * 
     * @param input
     * @return
     */
    public static Timestamp resolveTimestamp(Date input) {
        Timestamp returnVal = null;
        if(null!= input) {
            returnVal = new Timestamp(input.getTime());
        }
        return returnVal;
    }

    // starting with an existing map, return a new map where the keys in the returned map are the values from the existing map,
    // and the values in the returned map are the keys from the existing map.
    private static <T,V> Map<T, V> buildReverseMap(Map<V, T> originalMap) {
        HashMap<T,V> toReturn = new HashMap<>();
        for (V key : originalMap.keySet()) {
            toReturn.put(originalMap.get(key), key);
        }
        return toReturn;
    }

}
