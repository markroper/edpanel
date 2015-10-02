package com.scholarscore.models;

public class HibernateConsts {

    //Address constants
    public static final String ADDRESS_TABLE = "address";
    public static final String ADDRESS_STREET = "address_street";
    public static final String ADDRESS_CITY = "address_city";
    public static final String ADDRESS_STATE = "address_state";
    public static final String ADDRESS_POSTAL_CODE = "address_postal_code";
    public static final String ADDRESS_ID = "address_id";
    
    //Administrator consts
    public static final String ADMIN_TABLE = "administrator";
    public static final String ADMIN_NAME = "administrator_name";
    public static final String ADMIN_ADDRESS_FK = "administrator_homeAddress_fk";
    public static final String ADMIN_HOME_PHONE = "administrator_home_phone";
    public static final String ADMIN_SOURCE_SYSTEM_ID = "administrator_source_system_id";
    public static final String ADMIN_USER_FK = "administrator_user_fk";
    
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
    //student assignment
    public static final String STUDENT_ASSIGNMENT_TABLE = "student_assignment";
    public static final String STUDENT_ASSIGNMENT_COMPLETED = "completed";
    public static final String STUDENT_ASSIGNMENT_AWARDED_POINTS = "awarded_points";
    public static final String STUDENT_ASSIGNMENT_COMPLETION_DATE = "completion_date";
    public static final String STUDENT_ASSIGNMENT_ID = "student_assignment_id";
    public static final String STUDENT_ASSIGNMENT_NAME = "student_assignment_name";
    public static final String ASSIGNMENT_FK = "assignment_fk";
    
    //Behavior constants
    public static final String BEHAVIOR_TABLE = "behavior";
    public static final String BEHAVIOR_ID = "behavior_id";
    public static final String BEHAVIOR_NAME = "name";
    public static final String BEHAVIOR_REMOTE_SYSTEM = "remote_system";
    public static final String BEHAVIOR_REMOTE_BEHAVIOR_ID = "remote_behavior_id";
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
    
    //School year constants
    public static final String SCHOOL_YEAR_TABLE = "school_year";
    public static final String SCHOOL_YEAR_ID = "school_year_id";
    public static final String SCHOOL_YEAR_NAME = "school_year_name";
    public static final String SCHOOL_YEAR_START_DATE = "school_year_start_date";
    public static final String SCHOOL_YEAR_END_DATE = "school_year_end_date";
    
    //Section constants
    public static final String SECTION_TABLE = "section";
    public static final String SECTION_ID = "section_id";
    public static final String SECTION_NAME = "section_name";
    public static final String SECTION_START_DATE = "section_start_date";
    public static final String SECTION_END_DATE = "section_end_date";
    public static final String SECTION_ROOM = "room";
    public static final String SECTION_GRADE_FORMULA = "grade_formula";
    public static final String COURSE_FK = "course_fk";
    public static final String TERM_FK = "term_fk";
    
    //Student constants
    public static final String STUDENT_TABLE = "student";
    public static final String STUDENT_SOURCE_SYSTEM_ID = "source_system_id";
    public static final String STUDENT_NAME = "student_name";
    public static final String STUDENT_MAILING_FK = "mailing_fk";
    public static final String STUDENT_HOME_FK = "home_fk";
    public static final String STUDENT_GENDER = "gender";
    public static final String STUDENT_BIRTH_DATE = "birth_date";
    public static final String STUDENT_DISTRICT_ENTRY_DATE = "district_entry_date";
    public static final String STUDENT_PROJECTED_GRADUATION_YEAR = "projected_graduation_year";
    public static final String STUDENT_SOCIAL_SECURITY_NUM = "social_security_number";
    public static final String STUDENT_FEDERAL_RACE = "federal_race";
    public static final String STUDENT_FEDERAL_ETHNICITY = "federal_ethnicity";
    public static final String STUDENT_USER_FK = "student_user_fk";
    
    //Student section grade constants
    public static final String STUDENT_SECTION_GRADE_TABLE = "student_section_grade";
    public static final String STUDENT_SECTION_GRADE_ID = "student_section_grade_id";
    public static final String STUDENT_SECTION_GRADE_COMPLETE = "complete";
    public static final String STUDENT_SECTION_GRADE_GRADE = "grade";
    
    //Teacher constants
    public static final String TEACHER_TABLE = "teacher";
    public static final String TEACHER_NAME = "teacher_name";
    public static final String TEACHER_ADDRESS_FK = "teacher_homeAddress_fk";
    public static final String TEACHER_HOME_PHONE = "teacher_home_phone";
    public static final String TEACHER_SOURCE_SYSTEM_ID = "teacher_source_system_id";
    public static final String TEACHER_USER_FK = "teacher_user_fk";
    
    //Term constants
    public static final String TERM_TABLE = "term";
    public static final String TERM_ID = "term_id";
    public static final String TERM_NAME = "term_name";
    public static final String TERM_START_DATE = "term_start_date";
    public static final String TERM_END_DATE = "term_end_date";
    public static final String SCHOOL_YEAR_FK = "school_year_fk";
    
    //Users constants
    public static final String USERS_TABLE = "users";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "username";
    public static final String USER_PASSWORD = "password";
    public static final String USER_ENABLED = "enabled";
    public static final String USER_TYPE = "user_type";
    public static final String USER_EMAIL_ADDRESS = "email_address";
    public static final String USER_EMAIL_CONFIRM_CODE = "email_confirm_code";
    public static final String USER_EMAIL_CONFIRM_CODE_GENERATED_TIME = "email_confirm_code_creation_time";
    public static final String USER_EMAIL_CONFIRMED = "email_confirmed";
    public static final String USER_PHONE_NUMBER = "phone_number";
    public static final String USER_PHONE_CONFIRM_CODE = "phone_confirm_code";
    public static final String USER_PHONE_CONFIRM_CODE_GENERATED_TIME = "phone_confirm_code_creation_time";
    public static final String USER_PHONE_CONFIRMED = "phone_confirmed";

    //Section to Teacher table
    public static final String TEACHER_SECTION_TABLE = "teacher_section";
    public static final String TEACHER_SECTION_ROLE = "role";

    //IGoal constants
    public static final String GOAL_TABLE = "goal";
    public static final String GOAL_ID = "goal_id";
    public static final String GOAL_APPROVED = "approved";
    public static final String PARENT_FK = "parent_fk";
    public static final String DESIRED_GOAL_VALUE = "desired_value";
    public static final String GOAL_TYPE = "goal_type";
    public static final String GOAL_START_DATE = "start_date";
    public static final String GOAL_END_DATE = "end_date";
    public static final String BEHAVIOR_GOAL_CATEGORY = "behavior_category";
    public static final String GOAL_NAME = "name";
    public static final String GOAL_SECTION_FK = "section_fk";
    
    //School days
    public static final String SCHOOL_DAY_TABLE = "school_day";
    public static final String SCHOOL_DAY_ID = "school_day_id";
    public static final String SCHOOL_DAY_DATE = "school_day_date";
    
    //Attendance
    public static final String ATTENDANCE_TABLE = "attendance";
    public static final String ATTENDANCE_ID = "attendance_id";
    public static final String SCHOOL_DAY_FK = "school_day_fk";
    public static final String ATTENDANCE_STATUS = "attendance_status";
    public static final String ATTENDANCE_DESCRIPTION = "attendance_description";
}
