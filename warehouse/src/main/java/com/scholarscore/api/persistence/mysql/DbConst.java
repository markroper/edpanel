package com.scholarscore.api.persistence.mysql;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;
import com.scholarscore.models.query.Measure;
import com.scholarscore.models.query.dimension.CourseDimension;
import com.scholarscore.models.query.dimension.SchoolDimension;
import com.scholarscore.models.query.dimension.SchoolYearDimension;
import com.scholarscore.models.query.dimension.SectionDimension;
import com.scholarscore.models.query.dimension.StudentDimension;
import com.scholarscore.models.query.dimension.TeacherDimension;
import com.scholarscore.models.query.dimension.TermDimension;

@SuppressWarnings("serial")
public class DbConst {
    
   public static final Map<Measure, String> MEASURE_TO_TABLE_NAME = 
           new HashMap<Measure, String>() {{
               put(Measure.COURSE_GRADE, STUDENT_SECTION_GRADE_TABLE);
               put(Measure.ASSIGNMENT_GRADE, STUDENT_ASSIGNMENT_TABLE);
               put(Measure.HW_COMPLETION, STUDENT_ASSIGNMENT_TABLE);
           }};
   public static final Map<Measure, String> MEASURE_TO_COL_NAME = 
           new HashMap<Measure, String>() {{
               put(Measure.COURSE_GRADE, STUD_SECTION_GRADE_GRADE);
               put(Measure.ASSIGNMENT_GRADE, STUD_AWARDED_POINTS);
               put(Measure.HW_COMPLETION, STUD_COMPLETED_COL);
           }};

    public static final Map<Dimension, String> DIMENSION_TO_TABLE_NAME = 
            new HashMap<Dimension, String>() {{
               put(Dimension.SCHOOL, SCHOOL_TABLE);
               put(Dimension.COURSE, COURSE_TABLE);
               put(Dimension.SECTION, SECTION_TABLE);
               put(Dimension.TERM, TERM_TABLE);
               put(Dimension.STUDENT, STUDENT_TABLE);
               put(Dimension.TEACHER, TEACHER_TABLE);
               put(Dimension.YEAR, SCHOOL_YEAR_TABLE);
            }};

    public static final Map<DimensionField, String> DIMENSION_TO_COL_NAME = 
            new HashMap<DimensionField, String>(){{
        //Student dimension field to db column name lookup
        put(new DimensionField(Dimension.STUDENT, StudentDimension.AGE), STUDENT_BIRTH_DATE_COL);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.GENDER), STUDENT_GENDER_COL);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.NAME), STUDENT_NAME_COL);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.ID), STUDENT_ID_COL);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.ETHNICITY), STUDENT_ETHNICITY_COL);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.RACE), STUDENT_RACE_COL);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.HOME_ADDRESS), STUDENT_HOME_STREET_COL);
        put(new DimensionField(Dimension.STUDENT, StudentDimension.CITY_OF_RESIDENCE), STUDENT_HOME_CITY_COL);
        //Teacher dimension field to DB column name lookup
        put(new DimensionField(Dimension.TEACHER, TeacherDimension.NAME), TEACHER_NAME_COL);
        put(new DimensionField(Dimension.TEACHER, TeacherDimension.ID), TEACHER_ID_COL);
        put(new DimensionField(Dimension.TEACHER, TeacherDimension.EMAIL), TEACHER_EMAIL_COL);
        //School dimension field to DB column name lookup
        put(new DimensionField(Dimension.SCHOOL, SchoolDimension.NAME), SCHOOL_NAME_COL);
        put(new DimensionField(Dimension.SCHOOL, SchoolDimension.ID), SCHOOL_ID_COL);
        put(new DimensionField(Dimension.SCHOOL, SchoolDimension.ADDRESS), SCHOOL_ADDRESS_COL);
        //SECTION dimension field to DB column name lookup
        put(new DimensionField(Dimension.SECTION, SectionDimension.NAME), SECTION_NAME_COL);
        put(new DimensionField(Dimension.SECTION, SectionDimension.ID), SECTION_ID_COL);
        put(new DimensionField(Dimension.SECTION, SectionDimension.END_DATE), SECTION_END_DATE_COL);
        put(new DimensionField(Dimension.SECTION, SectionDimension.START_DATE), SECTION_START_DATE_COL);
        put(new DimensionField(Dimension.SECTION, SectionDimension.TEACHER), TEACHER_NAME_COL);
        put(new DimensionField(Dimension.SECTION, SectionDimension.GRADE_FORMULA), GRADE_FORMULA_COL);
        put(new DimensionField(Dimension.SECTION, SectionDimension.ROOM), ROOM_COL);
        //COURSE dimension field to DB column name lookup
        put(new DimensionField(Dimension.COURSE, CourseDimension.NAME), COURSE_NAME_COL);
        put(new DimensionField(Dimension.COURSE, CourseDimension.ID), COURSE_ID_COL);
        //TERM dimension field to DB column name lookup
        put(new DimensionField(Dimension.TERM, TermDimension.NAME), TERM_NAME_COL);
        put(new DimensionField(Dimension.TERM, TermDimension.ID), TERM_ID_COL);
        put(new DimensionField(Dimension.TERM, TermDimension.END_DATE), TERM_END_DATE_COL);
        put(new DimensionField(Dimension.TERM, TermDimension.START_DATE), TERM_START_DATE_COL);
        //TERM dimension field to DB column name lookup
        put(new DimensionField(Dimension.YEAR, SchoolYearDimension.ID), SCHOOL_YEAR_ID_COL);
        put(new DimensionField(Dimension.YEAR, SchoolYearDimension.END_DATE), SCHOOL_YEAR_END_DATE_COL);
        put(new DimensionField(Dimension.YEAR, SchoolYearDimension.START_DATE), SCHOOL_YEAR_START_DATE_COL);
    }};

    public static final String DATABASE = "scholar_warehouse";
    //Tables
    public static final String SCHOOL_TABLE = "school";
    public static final String SCHOOL_YEAR_TABLE = "school_year";
    public static final String TERM_TABLE = "term";
    public static final String STUDENT_TABLE = "student";
    public static final String SECTION_TABLE = "section";
    public static final String COURSE_TABLE = "course";
    public static final String ASSIGNMENT_TABLE = "assignment";
    public static final String STUDENT_ASSIGNMENT_TABLE = "student_assignment";
    public static final String STUDENT_SECTION_GRADE_TABLE = "student_section_grade";
    public static final String TEACHER_TABLE = "teacher";
    //Columns
    public static final String SCHOOL_ID_COL = "school_id";
    public static final String SCHOOL_NAME_COL = "school_name";
    public static final String SCHOOL_ADDRESS_COL = "school_address";
    
    public static final String SCHOOL_FK_COL = "school_fk";
    public static final String SCHOOL_YEAR_ID_COL = "school_year_id";
    public static final String SCHOOL_YEAR_NAME_COL = "school_year_name";
    public static final String SCHOOL_YEAR_START_DATE_COL = "school_year_start_date";
    public static final String SCHOOL_YEAR_END_DATE_COL = "school_year_end_date";
    
    public static final String TERM_ID_COL = "term_id";
    public static final String SCHOOL_YEAR_FK_COL = "school_year_fk";
    public static final String TERM_NAME_COL = "term_name";
    public static final String TERM_START_DATE_COL = "term_start_date";
    public static final String TERM_END_DATE_COL = "term_end_date";
    
    public static final String STUDENT_ID_COL = "student_id";
    public static final String STUDENT_NAME_COL = "student_name";
//    public static final String STUDENT_AGE = "student_age";
    public static final String STUDENT_GENDER_COL = "gender";
//    public static final String STUDENT_FREE_LUNCH_COL = "student_free_lunch";
//    public static final String STUDENT_GRADE_REPEATER_COL = "student_grade_repeater";
    public static final String STUDENT_ETHNICITY_COL = "federal_ethnicity";
    public static final String STUDENT_RACE_COL = "federal_race";
//    public static final String STUDENT_ELL_COL = "student_ell";
//    public static final String STUDENT_SPECIAL_ED_COL = "student_special_ed";
    public static final String STUDENT_HOME_CITY_COL = "home_city";
    public static final String STUDENT_MAILING_STREET_COL = "mailing_street";
    public static final String STUDENT_MAILING_CITY_COL = "mailing_city";
    public static final String STUDENT_MAILING_STATE_COL = "mailing_state";
    public static final String STUDENT_MAILING_POSTAL_COL = "mailing_postal_code";
    public static final String STUDENT_HOME_STREET_COL = "home_street";
    public static final String STUDENT_HOME_STATE_COL = "home_state";
    public static final String STUDENT_HOME_POSTAL_COL = "home_postal_code";
    public static final String STUDENT_BIRTH_DATE_COL = "birth_date";
    public static final String STUDENT_DISTRICT_ENTRY_DATE_COL = "district_entry_date";
    public static final String STUDENT_PROJECTED_GRADUATION_DATE_COL = "projected_graduation_year";
    public static final String STUDENT_SOURCE_SYSTEM_ID = "source_system_id";
    public static final String STUDENT_SOCIAL_SECURTIY_NUMBER_COL = "social_security_number";
    
    public static final String SECTION_ID_COL = "section_id";
    public static final String ROOM_COL = "room";
    public static final String GRADE_FORMULA_COL = "grade_formula";
    public static final String TERM_FK_COL = "term_fk";
    public static final String COURSE_FK_COL = "course_fk";
    public static final String SECTION_NAME_COL = "section_name";
    public static final String SECTION_START_DATE_COL = "section_start_date";
    public static final String SECTION_END_DATE_COL = "section_end_date";
    
    public static final String COURSE_ID_COL = "course_id";
    public static final String COURSE_NAME_COL = "course_name";
    
    public static final String ASSIGNMENT_ID_COL = "assignment_id";
    public static final String ASSIGNMENT_NAME_COL = "assignment_name";
    public static final String TYPE_FK_COL = "type_fk";
    public static final String ASSIGNED_DATE_COL = "assigned_date";
    public static final String DUE_DATE_COL = "due_date";
    public static final String AVAILABLE_POINTS_COL = "available_points";
    public static final String SECTION_FK_COL = "section_fk";
    
    public static final String STUD_ASSIGNMENT_ID_COL = "student_assignment_id";
    public static final String STUD_ASSIGNMENT_NAME_COL = "student_assignment_name";
    public static final String STUD_COMPLETED_COL = "completed";
    public static final String STUD_COMPLETION_DATE_COL = "completion_date";
    public static final String STUD_AWARDED_POINTS = "awarded_points";
    public static final String ASSIGNMENT_FK_COL = "assignment_fk";
    public static final String STUD_FK_COL = "student_fk";
    
    public static final String STUD_SECTION_GRADE_ID_COL = "student_section_grade_id";
    public static final String STUD_SECTION_GRADE_COMPLETE = "complete";
    public static final String STUD_SECTION_GRADE_GRADE = "grade";
    
    public static final String TEACHER_ID_COL = "teacher_id";
    public static final String TEACHER_NAME_COL = "teacher_name";
	public static final String TEACHER_EMAIL_COL = "teacher_email";
    
    public static final String USER_TABLE = "users";
    public static final String USER_USERNAME_COL = "username";
	public static final String USER_PASSWORD_COL = "password";
	public static final String USER_ENABLED_COL = "enabled";
	
	public static final String AUTHORITY_TABLE = "authorities";
	public static final String AUTHORITY_USERNAME_COL = "username";
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
