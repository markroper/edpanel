package com.scholarscore.models;

public class HibernateConsts {

    //Watvh constants
    public static final String WATCH_ID = "watch_id";
    public static final String WATCH_TABLE ="watch";

    //Address constants
    public static final String ADDRESS_TABLE = "address";
    public static final String ADDRESS_STREET = "address_street";
    public static final String ADDRESS_CITY = "address_city";
    public static final String ADDRESS_STATE = "address_state";
    public static final String ADDRESS_POSTAL_CODE = "address_postal_code";
    public static final String ADDRESS_ID = "address_id";
    
    //Assignment constants
    public static final String ASSIGNMENT_TABLE = "assignment";
    public static final String ASSIGNMENT_CLASS = "assignment_class";
    public static final String SECTION_FK = "section_fk";
    public static final String ASSIGNMENT_ID = "assignment_id";
    public static final String ASSIGNMENT_NAME = "assignment_name";
    public static final String ASSIGNMENT_TYPE_FK = "type_fk";
    public static final String ASSIGNMENT_DUE_DATE = "due_date";
    public static final String ASSIGNMENT_AVAILABLE_POINTS = "available_points";
    public static final String ASSIGNMENT_WEIGHT = "weight";
    public static final String ASSIGNMENT_USER_DEFINED_TYPE = "user_defined_type";
    public static final String ASSIGNMENT_INCLUDE_IN_FINAL_GRADES = "include_in_final_grades";
    public static final String ASSIGNMENT_SOURCE_SYSTEM_ID = "assignment_source_system_id";
    //graded
    public static final String ASSIGNMENT_ASSIGNED_DATE = "assigned_date";
    //student assignment
    public static final String STUDENT_ASSIGNMENT_TABLE = "student_assignment";
    public static final String STUDENT_ASSIGNMENT_COMPLETED = "completed";
    public static final String STUDENT_ASSIGNMENT_AWARDED_POINTS = "awarded_points";
    public static final String STUDENT_ASSIGNMENT_COMPLETION_DATE = "completion_date";
    public static final String STUDENT_ASSIGNMENT_COMMENT = "comment";
    public static final String STUDENT_ASSIGNMENT_ID = "student_assignment_id";
    public static final String STUDENT_ASSIGNMENT_NAME = "student_assignment_name";
    public static final String STUDENT_ASSIGNMENT_EXEMPT = "student_assignment_exempt";
    public static final String STUDENT_SOURCE_SYSTEM_USER_ID = "student_source_system_user_id";
    public static final String ASSIGNMENT_FK = "assignment_fk";
    public static final String STUDENT_ASSIGNMENT_FK = "student_assignment_fk";
    
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
    public static final String BEHAVIOR_FK = "behavior_fk";

    public static final String BEHAVIOR_SCORE_TABLE = "behavior_score";
    public static final String BEHAVIOR_SCORE_ID = "behavior_score_id";
    public static final String BEHAVIOR_SCORE_DATE = "behavior_score_date";
    public static final String BEHAVIOR_SCORE_WEEKLY_SCORE = "behavior_score_weekly_score";
    public static final String BEHAVIOR_SCORE_ANNUAL_SCORE = "behavior_score_annual_score";
    
    // the following column names are used in multiple places in different tables
    public static final String STUDENT_FK = "student_fk";
    public static final String TEACHER_FK = "teacher_fk";
    public static final String STAFF_FK = "staff_fk";
    public static final String ADMINISTRATOR_FK = "administrator_fk";
    public static final String USER_FK = "user_fk";
    
    // these aren't defined columns in the DB, but are used by the SQL statements
    // as labels for addressing the calculated columns used by prepScore
    public static final String PREPSCORE_START_DATE = "start_date";
    public static final String PREPSCORE_END_DATE = "end_date";
    public static final String PREPSCORE_DERIVED_WEEKS_TABLE = "derived_weeks";
    public static final String PREPSCORE_DERIVED_BUCKETED_BEHAVIOR_TABLE = "bucketed_behavior_events";
    public static final String PREPSCORE_DERIVED_INNER_POINT_VALUE = "inner_point_value";
    
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
    public static final String SCHOOL_NUMBER = "school_number";
    public static final String SCHOOL_DISABLE_BEHAVIOR = "school_disable_behavior";
    public static final String SCHOOL_DISABLE_GPA = "school_disable_gpa";
    public static final String SCHOOL_SOURCE_SYSTEM_ID = "source_system_id";
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
    public static final String SECTION_SOURCE_SYSTEM_ID = "section_source_system_id";
    public static final String SECTION_NUMBER_OF_TERMS = "number_of_terms";
    public static final String SECTION_EXPRESION = "section_expression";
    
    //Student constants
    public static final String STUDENT_TABLE = "student";
    public static final String STUDENT_SOURCE_SYSTEM_ID = "source_system_id";
    public static final String STUDENT_WITHDRAWAL_DATE = "withdrawal_date";
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
    public static final String STUDENT_SPED = "student_sped";
    public static final String STUDENT_SPED_DETAIL = "student_sped_detail";
    public static final String STUDENT_ELL = "student_ell";
    public static final String STUDENT_ELL_DETAIL = "student_ell_detail";
    public static final String STUDENT_ENROLL_STATUS = "student_enroll_status";
    public static final String STUDENT_GRADE_LEVEL = "student_grade_level";
    public static final String STUDENT_STATE_ID = "student_state_id";
    public static final String STUDENT_PREVIOUS_SCHOOL_ID = "student_previous_school_id";
    
    //Student section grade constants
    public static final String STUDENT_SECTION_GRADE_TABLE = "student_section_grade";
    public static final String STUDENT_SECTION_GRADE_ID = "student_section_grade_id";
    public static final String STUDENT_SECTION_GRADE_COMPLETE = "complete";
    public static final String STUDENT_SECTION_GRADE_GRADE = "grade";
    public static final String STUDENT_SECTION_GRADE_TERM_GRADES = "term_grades";
    public static final String STUDENT_SECTION_GRADE_FK = "student_section_grade_fk";

    //Section grade
    public static final String SECTION_GRADE_TABLE = "section_grade";
    public static final String SECTION_GRADE_FK = "section_grade_fk";
    public static final String SECTION_GRADE_ID = "section_grade_id";
    public static final String SECTION_GRADE_COMMENT = "comment";
    public static final String SECTION_GRADE_GRADE = "grade";
    public static final String SECTION_GRADE_DATE = "date";
    public static final String SECTION_GRADE_MANUALLY_OVERRIDDEN = "manually_overridden";
    public static final String SECTION_GRADE_LETTER_GRADE = "letter_grade";

    //Teacher constants
    public static final String TEACHER_NAME = "teacher_name";

    //Teacher constants
    public static final String STAFF_TABLE = "staff";
    public static final String STAFF_NAME = "staff_name";
    public static final String STAFF_ADDRESS_FK = "staff_homeAddress_fk";
    public static final String STAFF_HOME_PHONE = "staff_home_phone";
    public static final String STAFF_SOURCE_SYSTEM_ID = "staff_source_system_id";
    public static final String STAFF_SOURCE_SYSTEM_USER_ID = "staff_source_system_user_id";
    public static final String STAFF_USER_FK = "staff_user_fk";
    public static final String STAFF_IS_ADMIN = "admin";
    public static final String STAFF_IS_TEACHER = "teacher";
    
    //Term constants
    public static final String TERM_TABLE = "term";
    public static final String TERM_ID = "term_id";
    public static final String TERM_NAME = "term_name";
    public static final String TERM_START_DATE = "term_start_date";
    public static final String TERM_PORTION = "term_portion";
    public static final String TERM_END_DATE = "term_end_date";
    public static final String SCHOOL_YEAR_FK = "school_year_fk";
    public static final String TERM_SOURCE_SYSTEM_ID = "term_source_system_id";
    
    //Users constants
    public static final String USERS_TABLE = "user";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "username";
    public static final String USER_PASSWORD = "password";
    public static final String USER_ENABLED = "enabled";
    public static final String USER_ONETIME_PASS = "onetime_pass";
    public static final String USER_ONETIME_PASS_CREATED = "onetime_pass_created";
    public static final String USER_TYPE = "user_type";

    public static final String CONTACT_METHOD_TABLE = "contact_method";
    public static final String CONTACT_METHOD_ID = "contact_method_id";
    public static final String CONTACT_METHOD_TYPE = "contact_type";
    public static final String CONTACT_METHOD_USER_FK = "user_fk";
    public static final String CONTACT_METHOD_CONTACT_VALUE = "contact_value";
    public static final String CONTACT_METHOD_CONFIRM_CODE = "confirm_code";
    public static final String CONTACT_METHOD_CONFIRM_CODE_CREATED = "confirm_code_created";
    public static final String CONTACT_METHOD_CONFIRMED = "confirmed";
    
    //Section to Teacher table
    public static final String TEACHER_SECTION_TABLE = "teacher_section";
    public static final String TEACHER_SECTION_ROLE = "role";

    //IGoal constants
    public static final String GOAL_TABLE = "goal";
    public static final String GOAL_ID = "goal_id";
    public static final String GOAL_FK = "goal_fk";
    public static final String GOAL_APPROVED = "approved";
    public static final String GOAL_AUTOCOMPLETE = "autocomplete";
    public static final String GOAL_FOLLOWUP = "followup";
    public static final String DESIRED_GOAL_VALUE = "desired_value";
    public static final String GOAL_TYPE = "goal_type";
    public static final String GOAL_START_DATE = "start_date";
    public static final String GOAL_END_DATE = "end_date";
    public static final String BEHAVIOR_GOAL_CATEGORY = "behavior_category";
    public static final String GOAL_NAME = "name";
    public static final String GOAL_ASSIGNMENT_NAME = "assignment_name";
    public static final String GOAL_PLAN = "plan";
    public static final String GOAL_MESSAGE = "message";
    public static final String GOAL_OBSTACLE = "obstacle";
    public static final String GOAL_OUTCOME = "outcome";
    public static final String GOAL_PROGRESS = "progress";
    public static final String GOAL_FINAL = "final_value";
    
    //School days
    public static final String SCHOOL_DAY_TABLE = "school_day";
    public static final String SCHOOL_DAY_ID = "school_day_id";
    public static final String SCHOOL_DAY_DATE = "school_day_date";
    public static final String SCHOOL_DAY_SOURCE_SYSTEM_ID = "school_day_source_system_id";
    public static final String SCHOOL_DAY_SOURCE_SYSTEM_OTHER_ID = "school_day_source_system_other_id";
    
    //Attendance
    public static final String ATTENDANCE_TABLE = "attendance";
    public static final String ATTENDANCE_ID = "attendance_id";
    public static final String SCHOOL_DAY_FK = "school_day_fk";
    public static final String ATTENDANCE_STATUS = "attendance_status";
    public static final String ATTENDANCE_DESCRIPTION = "attendance_description";
    public static final String ATTENDANCE_SOURCE_SYSTEM_ID = "attendance_source_system_id";
    public static final String ATTENDANCE_CODE = "attendance_code";
    public static final String ATTENDANCE_PERIOD_ID = "attendance_source_system_period_id";
    public static final String ATTENDANCE_TYPE = "attendance_type";

    //GPA
    public static final String GPA_ID = "gpa_id";
    public static final String GPA_TABLE = "gpa";
    public static final String GPA_TYPE = "gpa_type";
    public static final String GPA_START_DATE = "gpa_start_date";
    public static final String GPA_END_DATE = "gpa_end_date";
    public static final String GPA_SCORE = "gpa_score";
    public static final String GPA_CALCULATION_DATE = "gpa_calc_date";
    //CURRENT GPA
    public static final String CURRENT_GPA_TABLE = "current_gpa";
    public static final String CURRENT_GPA_ID = "current_gpa_id";
    public static final String GPA_FK = "gpa_fk";

    //UI attributes
    public static final String UI_ATTRIBUTES_TABLE = "ui_attributes";
    public static final String UI_ATTRIBUTES = "attributes";
    public static final String UI_ATTRIBUTES_ID = "ui_attributes_id";

    //Survey attributes
    public static final String SURVEY_TABLE = "survey";
    public static final String SURVEY_ID = "survey_id";
    public static final String SURVEY_NAME = "survey_name";
    public static final String SURVEY_CREATED_DATE = "survey_created_date";
    public static final String SURVEY_ADMINISTER_DATE = "survey_administer_date";
    public static final String SURVEY_SCHEMA = "survey_schema";
    public static final String SURVEY_USER_FK = "user_fk";
    public static final String SURVEY_FK = "survey_fk";

    //Survey responses
    public static final String SURVEY_RESPONSES_TABLE = "survey_response";
    public static final String SURVEY_RESPONSE_ID = "survey_response_id";
    public static final String SURVEY_RESPONSE_DATE = "survey_response_date";
    public static final String SURVEY_RESPONSE = "survey_response";

    //Notifications
    public static final String NOTIFICATION_TABLE = "notification";
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String NOTIFICATION_NAME = "notification_name";
    public static final String NOTIFICATION_SUBSCRIBERS_FK = "owning_group_fk";
    public static final String NOTIFICATION_SUBJECTS_FK = "subject_group_fk";
    public static final String NOTIFICATION_TRIGGER = "notification_trigger";
    public static final String NOTIFICATION_AGG_FUNCTION = "notification_aggregate_function";
    public static final String NOTIFICATION_WINDOW = "notification_window";
    public static final String NOTIFICATION_MEASURE = "notification_measure";
    public static final String NOTIFICATION_CREATED_DATE = "notification_created_date";
    public static final String NOTIFICATION_EXPIRY_DATE = "notification_expiry_date";
    public static final String NOTIFICATION_TRIGGER_GREATER_THAN = "notification_trigger_greater_than";
    public static final String NOTIFICATION_ONE_TIME = "one_time";
    public static final String NOTIFICATION_TRIGGERED = "triggered";

    public static final String NOTIFICATION_GROUP = "notification_group";
    public static final String NOTIFICATION_GROUP_ID = "notification_group_id";
    public static final String NOTIFICATION_GROUP_TYPE = "notification_group_type";
    public static final String NOTIFICATION_GROUP_FILTER = "notification_group_student_filter";

    public static final String TRIGGERED_NOTIFICATION_TABLE = "triggered_notification";
    public static final String TRIGGERED_NOTIFICATION_ID = "triggered_notification_id";
    public static final String TRIGGERED_NOTIFICATION_DATE = "triggered_notification_date";
    public static final String TRIGGERED_NOTIFICATION_ACTIVE = "triggered_notification_active";
    public static final String TRIGGERED_NOTIFICATION_VALUE_WHEN_TRIGGERED = "triggered_notification_value_when_triggered";
    public static final String NOTIFICATION_FK = "notification_fk";
    public static final String SUBJECT_USER_FK = "subject_user_fk";

    //Messages
    public static final String MESSAGE_TABLE = "message";
    public static final String MESSAGE_ID = "message_id";
    public static final String MESSAGE_FK = "message_fk";
    public static final String MESSAGE_SENT = "message_sent";
    public static final String MESSAGE_BODY = "message_body";

    public static final String MESSAGE_THREAD_TABLE = "message_thread";
    public static final String MESSAGE_THREAD_FK = "message_thread_fk";
    public static final String MESSAGE_THREAD_ID = "message_thread_id";

    public static final String MESSAGE_THREAD_PARTICIPANT_TABLE = "message_thread_participant";

    public static final String MESSAGE_READ_STATE_TABLE = "message_read_state";
    public static final String MESSAGE_READ_STATE_ON = "message_read_state_on";

    public static final String MESSAGE_TOPIC_TABLE = "message_topic";
    public static final String MESSAGE_TOPIC_ID = "message_topic_id";
    public static final String MESSAGE_TOPIC_TYPE = "message_topic_type";
    public static final String MESSAGE_TOPIC_FK = "message_topic_fk";

    //Dashboards
    public static final String REPORT_TABLE = "report";
    public static final String REPORT_ID = "report_id";

    public static final String REPORT_FK = "report_fk";
    public static final String DASHBOARD_TABLE = "dashboard";
    public static final String DASHBOARD_ID = "dashboard_id";
    public static final String DASHBOARD_NAME = "dashboard_name";
    public static final String DASHBOARD_FK = "dashboard_fk";

    public static final String DASHBOARD_ROW_TABLE = "dashboard_row";
    public static final String DASHBOARD_ROW_ID = "dashboard_row_id";
    public static final String DASHBOARD_ROW_FK = "dashboard_row_fk";
    public static final String DASHBOARD_ROW_POSITION = "dashboard_row_position";

    public static final String DASHBOARD_REPORT_TABLE = "dashboard_report";
    public static final String DASHBOARD_REPORT_NAME = "dashboard_report_name";
    public static final String DASHBOARD_REPORT_TYPE = "dashboard_report_type";
    public static final String DASHBOARD_REPORT_ID = "dashboard_report_id";
    public static final String DASHBOARD_REPORT_SUPPORT_DEMOGRAPHIC_FILTER = "dashboard_report_support_demographic_filter";
    public static final String DASHBOARD_REPORT_SUPPORT_DATE_FILTER = "dashboard_report_support_date_filter";
    public static final String DASHBOARD_REPORT_COLUMN_DEFS = "dashboard_report_column_defs";
    public static final String DASHBOARD_REPORT_POSITION = "dashboard_report_position";
    public static final String DASHBOARD_REPORT_CLICK_REPORT_FK = "click_report_fk";
    public static final String DASHBOARD_REPORT_ASSIGNMENT_IDS = "dashboard_report_assignment_ids";

    public static final String MA_MCAS_TABLE = "ma_mcas_results";
    public static final String MA_MCAS_ID = "ma_mcas_results_id";
    public static final String MA_MCAS_STUD_GRADE_LEVEL = "student_grade_level";
    public static final String MA_MCAS_ADMIN_YEAR = "admin_year";
    public static final String MA_MCAS_EXAM_GRADE_LEVEL = "exam_grade_level";
    public static final String MA_MCAS_TOPIC_SCORE = "english_topic_score";
    public static final String MA_MCAS_COMPOSITION_SCORE = "english_composition_score";
    public static final String MA_MCAS_MATH_FK = "math_fk";
    public static final String MA_MCAS_ENGLISH_FK = "english_fk";
    public static final String MA_MCAS_SCIENCE_FK = "science_fk";

    public static final String MA_MCAS_TOPIC_TABLE = "ma_mcas_topic_scores";
    public static final String MA_MCAS_TOPIC_ID = "ma_mcas_topic_scores_id";
    public static final String ALT_TEST = "alt_test";
    public static final String COMPLEXITY = "complexity";
    public static final String EXAM_STATUS = "exam_status";
    public static final String RAW_SCORE = "raw_score";
    public static final String SCALED_SCORE = "scaled_score";
    public static final String PERF = "perf";
    public static final String PERF_2 = "perf_2";
    public static final String QUARTILE = "quartile";
}
