package com.scholarscore.models;

public class ColumnConsts {

    //Address constants
    public static final String ADDRESS_TABLE = "address";
    public static final String ADDRESS_STREET = "address_street";
    public static final String ADDRESS_CITY = "address_city";
    public static final String ADDRESS_STATE = "address_state";
    public static final String ADDRESS_POSTAL_CODE = "address_postal_code";
    public static final String ADDRESS_ID = "address_id";
    
    //Administrator consts
    public static final String ADMIN_TABLE = "administrator";
    public static final String ADMIN_ID = "administrator_id";
    public static final String ADMIN_NAME = "administrator_name";
    public static final String ADMIN_ADDRESS_FK = "administrator_homeAddress_fk";
    public static final String ADMIN_HOME_PHONE = "administrator_home_phone";
    public static final String ADMIN_SOURCE_SYSTEM_ID = "administrator_source_system_id";
    public static final String ADMIN_USERNAME = "administrator_username";
    
    //Assignment constants
    public static final String ASSIGNMENT_TABLE = "assignment";
    public static final String SECTION_FK = "section_fk";
    public static final String ASSIGNMENT_ID = "assignment_id";
    public static final String ASSIGNMENT_NAME = "assignment_name";
    public static final String ASSIGNMENT_TYPE_FK = "type_fk";
    public static final String ASSIGNMENT_DUE_DATE = "due_date";
    public static final String ASSIGNMENT_AVAILABLE_POINTS = "available_points";
    //graded
    public static final String ASSIGNMENT_ASSIGNED_DATE = "assigned_date";
    
    //Behavior constants
    public static final String BEHAVIOR_TABLE = "behavior";
    public static final String BEHAVIOR_ID = "behavior_id";
    public static final String BEHAVIOR_NAME = "name";
    public static final String BEHAVIOR_REMOTE_STUDENT_ID = "remote_student_id";
    public static final String BEHAVIOR_DATE = "date";
    public static final String BEHAVIOR_CATEGORY = "category";
    public static final String BEHAVIOR_POINT_VALUE = "point_value";
    public static final String BEHAVIOR_ROSTER = "roster";
    public static final String STUDENT_FK = "student_fk";
    public static final String TEACHER_FK = "teacher_fk";
    
    //Course constants
    public static final String COURSE_TABLE = "course";
    public static final String COURSE_NUMBER = "course_number";
    public static final String COURSE_SOURCE_SYSTEM_ID = "course_source_system_id";
    public static final String COURSE_ID = "course_id";
    public static final String COURSE_NAME = "course_name";  
    public static final String SCHOOL_FK = "school_fk";
    
    //School constants
    public static final String SCHOOL_TABLE = "school";
    public static final String SCHOOL_ID = "school_id";
    public static final String SCHOOL_NAME = "school_name";
    public static final String SCHOOL_SOURCE_SYSTEM_ID = "sourceSystemId";
    public static final String SCHOOL_PRINCIPAL_EMAIL = "principal_email";
    public static final String SCHOOL_PRINCIPAL_NAME = "principal_name";
    public static final String SCHOOL_MAIN_PHONE = "main_phone";
    public static final String SCHOOL_ADDRESS_FK = "school_address_fk";
    //    public static final String ADDRESS_STREET = "";
//    public static final String ADDRESS_STREET = "";
    
}
