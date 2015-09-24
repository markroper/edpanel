package com.scholarscore.api.persistence.mysql;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;
import com.scholarscore.models.query.Measure;
import com.scholarscore.models.query.MeasureField;
import com.scholarscore.models.query.dimension.CourseDimension;
import com.scholarscore.models.query.dimension.SchoolDimension;
import com.scholarscore.models.query.dimension.SchoolYearDimension;
import com.scholarscore.models.query.dimension.SectionDimension;
import com.scholarscore.models.query.dimension.StudentDimension;
import com.scholarscore.models.query.dimension.TeacherDimension;
import com.scholarscore.models.query.dimension.TermDimension;
import com.scholarscore.models.query.measure.BehaviorMeasure;

@SuppressWarnings("serial")
public class DbMappings {
    
   public static final Map<Measure, String> MEASURE_TO_TABLE_NAME = 
           new HashMap<Measure, String>() {{
               put(Measure.COURSE_GRADE, HibernateConsts.COURSE_TABLE);
               put(Measure.ASSIGNMENT_GRADE, HibernateConsts.STUDENT_ASSIGNMENT_TABLE);
               put(Measure.HW_COMPLETION, HibernateConsts.STUDENT_ASSIGNMENT_TABLE);
               put(Measure.DEMERIT, HibernateConsts.BEHAVIOR_TABLE);
               put(Measure.MERIT, HibernateConsts.BEHAVIOR_TABLE);
               put(Measure.SUSPENSION, HibernateConsts.BEHAVIOR_TABLE);
               put(Measure.DETENTION, HibernateConsts.BEHAVIOR_TABLE);
           }};
   public static final Map<Measure, String> MEASURE_TO_COL_NAME = 
           new HashMap<Measure, String>() {{
               put(Measure.COURSE_GRADE, HibernateConsts.STUDENT_SECTION_GRADE_GRADE);
               put(Measure.ASSIGNMENT_GRADE, HibernateConsts.STUDENT_ASSIGNMENT_AWARDED_POINTS);
               put(Measure.HW_COMPLETION, HibernateConsts.STUDENT_ASSIGNMENT_COMPLETED);
           }};
   public static final Map<MeasureField, String> MEASURE_FIELD_TO_COL_NAME = 
           new HashMap<MeasureField, String>() {{
               put(new MeasureField(Measure.DEMERIT, BehaviorMeasure.ID), HibernateConsts.BEHAVIOR_ID);
               put(new MeasureField(Measure.DEMERIT, BehaviorMeasure.STUDENT_FK), HibernateConsts.STUDENT_FK);
               put(new MeasureField(Measure.DEMERIT, BehaviorMeasure.TEACHER_FK), HibernateConsts.TEACHER_FK);
               put(new MeasureField(Measure.DEMERIT, BehaviorMeasure.DATE), HibernateConsts.BEHAVIOR_DATE);
               put(new MeasureField(Measure.DEMERIT, BehaviorMeasure.POINTS), HibernateConsts.BEHAVIOR_POINT_VALUE);
               put(new MeasureField(Measure.DEMERIT, BehaviorMeasure.CATEGORY), HibernateConsts.BEHAVIOR_CATEGORY);
               put(new MeasureField(Measure.DEMERIT, BehaviorMeasure.ROSTER), HibernateConsts.BEHAVIOR_ROSTER);
               put(new MeasureField(Measure.MERIT, BehaviorMeasure.ID), HibernateConsts.BEHAVIOR_ID);
               put(new MeasureField(Measure.MERIT, BehaviorMeasure.STUDENT_FK), HibernateConsts.STUDENT_FK);
               put(new MeasureField(Measure.MERIT, BehaviorMeasure.TEACHER_FK), HibernateConsts.TEACHER_FK);
               put(new MeasureField(Measure.MERIT, BehaviorMeasure.DATE), HibernateConsts.BEHAVIOR_DATE);
               put(new MeasureField(Measure.MERIT, BehaviorMeasure.POINTS), HibernateConsts.BEHAVIOR_POINT_VALUE);
               put(new MeasureField(Measure.MERIT, BehaviorMeasure.CATEGORY), HibernateConsts.BEHAVIOR_CATEGORY);
               put(new MeasureField(Measure.MERIT, BehaviorMeasure.ROSTER), HibernateConsts.BEHAVIOR_ROSTER);
               put(new MeasureField(Measure.DETENTION, BehaviorMeasure.ID), HibernateConsts.BEHAVIOR_ID);
               put(new MeasureField(Measure.DETENTION, BehaviorMeasure.STUDENT_FK), HibernateConsts.STUDENT_FK);
               put(new MeasureField(Measure.DETENTION, BehaviorMeasure.TEACHER_FK), HibernateConsts.TEACHER_FK);
               put(new MeasureField(Measure.DETENTION, BehaviorMeasure.DATE), HibernateConsts.BEHAVIOR_DATE);
               put(new MeasureField(Measure.DETENTION, BehaviorMeasure.POINTS), HibernateConsts.BEHAVIOR_POINT_VALUE);
               put(new MeasureField(Measure.DETENTION, BehaviorMeasure.CATEGORY), HibernateConsts.BEHAVIOR_CATEGORY);
               put(new MeasureField(Measure.DETENTION, BehaviorMeasure.ROSTER), HibernateConsts.BEHAVIOR_ROSTER);
               put(new MeasureField(Measure.SUSPENSION, BehaviorMeasure.ID), HibernateConsts.BEHAVIOR_ID);
               put(new MeasureField(Measure.SUSPENSION, BehaviorMeasure.STUDENT_FK), HibernateConsts.STUDENT_FK);
               put(new MeasureField(Measure.SUSPENSION, BehaviorMeasure.TEACHER_FK), HibernateConsts.TEACHER_FK);
               put(new MeasureField(Measure.SUSPENSION, BehaviorMeasure.DATE), HibernateConsts.BEHAVIOR_DATE);
               put(new MeasureField(Measure.SUSPENSION, BehaviorMeasure.POINTS), HibernateConsts.BEHAVIOR_POINT_VALUE);
               put(new MeasureField(Measure.SUSPENSION, BehaviorMeasure.CATEGORY), HibernateConsts.BEHAVIOR_CATEGORY);
               put(new MeasureField(Measure.SUSPENSION, BehaviorMeasure.ROSTER), HibernateConsts.BEHAVIOR_ROSTER);
           }};

    public static final Map<Dimension, String> DIMENSION_TO_TABLE_NAME = 
            new HashMap<Dimension, String>() {{
               put(Dimension.SCHOOL, HibernateConsts.SCHOOL_TABLE);
               put(Dimension.COURSE, HibernateConsts.COURSE_TABLE);
               put(Dimension.SECTION, HibernateConsts.SECTION_TABLE);
               put(Dimension.TERM, HibernateConsts.TERM_TABLE);
               put(Dimension.STUDENT, HibernateConsts.STUDENT_TABLE);
               put(Dimension.TEACHER, HibernateConsts.TEACHER_TABLE);
               put(Dimension.YEAR, HibernateConsts.SCHOOL_YEAR_TABLE);
            }};

    public static final Map<DimensionField, String> DIMENSION_TO_COL_NAME = 
            new HashMap<DimensionField, String>(){{
        //Student dimension field to db column name lookup
        put(new DimensionField(Dimension.STUDENT, StudentDimension.AGE), HibernateConsts.STUDENT_BIRTH_DATE);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.GENDER), HibernateConsts.STUDENT_GENDER);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.NAME), HibernateConsts.STUDENT_NAME);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.ID), HibernateConsts.STUDENT_ID);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.ETHNICITY), HibernateConsts.STUDENT_FEDERAL_ETHNICITY);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.RACE), HibernateConsts.STUDENT_FEDERAL_RACE);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.HOME_ADDRESS), STUDENT_HOME_STREET_COL);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.CITY_OF_RESIDENCE), STUDENT_HOME_CITY_COL);
        //Teacher dimension field to DB column name lookup
        put(new DimensionField(Dimension.TEACHER, TeacherDimension.NAME), HibernateConsts.TEACHER_NAME);
        put(new DimensionField(Dimension.TEACHER, TeacherDimension.ID), HibernateConsts.TEACHER_ID);

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
}
