package com.scholarscore.api.persistence.mysql;

import java.sql.Timestamp;
import java.util.Date;

public class DbConst {
    public static final String DATABASE = "scholar_warehouse";
    //Tables
    public static final String SCHOOL_TABLE = "school";
    public static final String SCHOOL_YEAR_TABLE = "school_year";
    public static final String TERM_TABLE = "school_term";
    public static final String STUDENT_TABLE = "student";
    public static final String SECTION_TABLE = "section";
    //Columns
    public static final String SCHOOL_ID_COL = "school_id";
    public static final String NAME_COL = "name";
    
    public static final String START_DATE_COL = "start_date";
    public static final String END_DATE_COL = "end_date";
    public static final String SCHOOL_FK_COL = "school_fk";
    public static final String SCHOOL_YEAR_ID_COL = "school_year_id";
    
    public static final String TERM_ID_COL = "school_term_id";
    public static final String SCHOOL_YEAR_FK_COL = "school_year_fk";
    
    public static final String STUDENT_ID_COL = "student_id";
    
    public static final String SECTION_ID_COL = "section_id";
    public static final String ROOM_COL = "room";
    public static final String GRADE_FORMULA_COL = "grade_formula";
    public static final String TERM_FK_COL = "term_fk";
    public static final String COURSE_FK_COL = "course_fk";
    
    public static Timestamp resolveTimestamp(Date input) {
        Timestamp returnVal = null;
        if(null!= input) {
            returnVal = new Timestamp(input.getTime());
        }
        return returnVal;
    }
}
