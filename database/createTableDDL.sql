DROP DATABASE IF EXISTS scholar_warehouse;
CREATE DATABASE scholar_warehouse;

CREATE TABLE `scholar_warehouse`.`address` (
  `address_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The address ID',
  `address_street` VARCHAR(255) NULL COMMENT 'The street address',
  `address_city` VARCHAR(255) NULL COMMENT 'The street address',
  `address_state` VARCHAR(255) NULL COMMENT 'The street address',
  `address_postal_code` VARCHAR(255) NULL COMMENT 'The street address',
  PRIMARY KEY (`address_id`))
  ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`school` (
  `school_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key column for the school table.',
  `school_name` VARCHAR(255) NULL COMMENT 'A human readable user-defined name',
  `school_number` BIGINT UNSIGNED NULL COMMENT 'The school number within the district',
  `school_address_fk` BIGINT UNSIGNED COMMENT 'The FK pointer back to the address of the school',
  `main_phone` VARCHAR(255) NULL COMMENT 'The school\'s main phone number',
  `principal_name` VARCHAR(255) NULL COMMENT 'The principal\'s name',
  `principal_email` VARCHAR(255) NULL COMMENT 'The principal\'s email address',
  `source_system_id` VARCHAR(255) NULL UNIQUE COMMENT 'The source system from which the entity was imported - the id from that system',
  PRIMARY KEY (`school_id`))
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`user` (
    `user_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto-incrementing primary key column',
    `username` varchar(50) NOT NULL COMMENT 'the username used to login',
    `password` CHAR(60) CHARACTER SET UTF8 COLLATE UTF8_BIN NULL COMMENT 'the password',
    `enabled` BOOLEAN NOT NULL COMMENT 'if the user has ever logged in and created a password',
    `onetime_pass` varchar(50) CHARACTER SET UTF8 NULL COMMENT 'one-time access token used for initial user setup and forgot password', 
    `onetime_pass_created` DATETIME NULL COMMENT 'when the one time pass was last generated', 
    PRIMARY KEY (`user_id`),
    UNIQUE(`username`)
)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`contact_method` (
  `contact_method_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto-incrementing primary key column',
  `contact_type` varchar(32) NOT NULL COMMENT 'the contact medium (e.g. phone, email)',
  `user_fk` BIGINT UNSIGNED NOT NULL COMMENT 'the fk to the user table',
  `contact_value` varchar(256) NOT NULL COMMENT 'the actual contact info - the email address, phone number, etc',
  `confirm_code` varchar(64) NULL COMMENT 'the confirmation code sent to the user via the specified medium',
  `confirm_code_created` DATETIME NULL COMMENT 'the time this confirmation code was generated and sent',
  `confirmed` BOOLEAN NOT NULL COMMENT 'if this email has been confirmed as belonging to the user',
    PRIMARY KEY (`contact_method_id`),
  CONSTRAINT `user_fk$contact_method`
  FOREIGN KEY (`user_fk`) REFERENCES `scholar_warehouse`.`user` (`user_id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
  CONSTRAINT `uniq_contact_type$user`
  UNIQUE (`contact_type`,`user_fk`)

)
ENGINE = InnoDB;
  
CREATE TABLE `scholar_warehouse`.`student` (
  `student_name` VARCHAR(255) NULL COMMENT 'User defined human-readable name',
  `source_system_id` VARCHAR(255) NULL UNIQUE COMMENT 'The identifier from the source system, if any',
  `student_source_system_user_id` VARCHAR(255) NULL COMMENT 'The identifier of the user from the source system, if any',
  `mailing_fk` BIGINT UNSIGNED NULL COMMENT 'The address FK for mailing address',
  `home_fk` BIGINT UNSIGNED NULL COMMENT 'The address FK for home address',
  `gender` INT NULL COMMENT 'The gender of the student',
  `student_user_fk` BIGINT UNSIGNED NULL UNIQUE COMMENT 'The user FK of the student',
  `birth_date` DATE NULL COMMENT 'The birth date of the student',
  `district_entry_date` DATE NULL COMMENT 'The date the student entered the school district',
  `projected_graduation_year` BIGINT UNSIGNED NULL COMMENT 'The projected year of graduation for the student. For example: 2020',
  `social_security_number` VARCHAR(255) NULL COMMENT 'The student\'s social security number',
  `federal_race` VARCHAR(512) NULL COMMENT 'The student\'s race according to the federal gov\'t',
  `federal_ethnicity` VARCHAR(512) NULL COMMENT 'The student\'s ethnicity according to the federal gov\'t',
  `school_fk` BIGINT UNSIGNED NULL COMMENT 'The foreign key to the current school the student is enrolled in within the district',
  CONSTRAINT `school_fk$student`
  FOREIGN KEY (`school_fk`) REFERENCES `scholar_warehouse`.`school` (`school_id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `home_fk$student`
  FOREIGN KEY (`home_fk`) REFERENCES `scholar_warehouse`.`address` (`address_id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `mailing_fk$student`
  FOREIGN KEY (`mailing_fk`) REFERENCES `scholar_warehouse`.`address` (`address_id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `student_user_fk$student`
  FOREIGN KEY (`student_user_fk`) REFERENCES `scholar_warehouse`.`user` (`user_id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE
    )
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`teacher` (
  `teacher_name` VARCHAR(256) NULL COMMENT 'User defined human-readable name',
  `teacher_source_system_id` VARCHAR(256) NULL UNIQUE,
  `teacher_source_system_user_id` VARCHAR(256) NULL COMMENT 'The identifier of the user from the source system, if any',
  `teacher_user_fk` BIGINT UNSIGNED NULL UNIQUE COMMENT 'The user_fk of the teacher',
  `teacher_home_phone` VARCHAR(256) NULL COMMENT 'Home phone number for teacher',
  `teacher_homeAddress_fk` BIGINT UNSIGNED COMMENT 'The home address FK',
  `school_fk` BIGINT UNSIGNED NULL COMMENT 'The foreign key to the current primary school the teacher teaches at',
  CONSTRAINT `teacher_homeAddress_fk$teacher`
  FOREIGN KEY (`teacher_homeAddress_fk`) REFERENCES `scholar_warehouse`.`address`(`address_id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `school_fk$teacher`
  FOREIGN KEY (`school_fk`) REFERENCES `scholar_warehouse`.`school` (`school_id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  FOREIGN KEY (`teacher_user_fk`) REFERENCES `scholar_warehouse`.`user` (`user_id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE
  )
  ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`administrator` (
  `administrator_name` VARCHAR(256) NULL COMMENT 'User defined human-readable name',
  `administrator_home_phone` VARCHAR(256) NULL,
  `administrator_homeAddress_fk` BIGINT UNSIGNED COMMENT 'The home address FK',
  `administrator_source_system_id` VARCHAR(256) NULL UNIQUE,
  `administrator_source_system_user_id` VARCHAR(256) NULL COMMENT 'The identifier of the user from the source system, if any',
  `administrator_user_fk` BIGINT UNSIGNED NULL UNIQUE COMMENT 'The user_fk of the teacher',
  `school_fk` BIGINT UNSIGNED NULL COMMENT 'The foreign key to the current school the administrator actively works for',
  CONSTRAINT `administrator_homeAddress_fk$administrator`
  FOREIGN KEY (`administrator_homeAddress_fk`)
    REFERENCES `scholar_warehouse`.`address`(`address_id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `school_fk$administrator`
  FOREIGN KEY (`school_fk`) REFERENCES `scholar_warehouse`.`school` (`school_id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  FOREIGN KEY (`administrator_user_fk`) REFERENCES `scholar_warehouse`.`user` (`user_id`)
  ON DELETE SET NULL
  ON UPDATE CASCADE
  )
  ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`school_year` (
  `school_year_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `school_year_name` VARCHAR(256) NULL COMMENT 'User defined human-readable name',
  `school_year_start_date` DATE NULL COMMENT 'The school year starting date',
  `school_year_end_date` DATE NULL COMMENT 'The school year end date',
  `school_fk` BIGINT UNSIGNED NOT NULL COMMENT 'The foreign key to the school table',
  PRIMARY KEY (`school_year_id`),
  CONSTRAINT `school_fk$school_year`
    FOREIGN KEY (`school_fk`)
    REFERENCES `scholar_warehouse`.`school`(`school_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`term` (
  `term_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `term_name` VARCHAR(256) NULL COMMENT 'User defined human-readable name',
  `term_start_date` DATE NULL COMMENT 'The school term start date',
  `term_end_date` DATE NULL COMMENT 'The school term end date',
  `school_year_fk` BIGINT UNSIGNED NOT NULL COMMENT 'The foreign key to the school table',
  `term_source_system_id` VARCHAR(256) NULL UNIQUE COMMENT 'The origin system id',
  `term_portion` BIGINT UNSIGNED NULL COMMENT 'The denominator of the fraction of the year this term represents (1=all year, 2=half, 3=a third, etc)',
  PRIMARY KEY (`term_id`),
  CONSTRAINT `fk_school_year$school_term`
    FOREIGN KEY (`school_year_fk`)
    REFERENCES `scholar_warehouse`.`school_year`(`school_year_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`course` (
  `course_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `course_name` VARCHAR(256) NULL COMMENT 'User defined human-readable name',
  `course_number` VARCHAR(256) NULL COMMENT 'User defined human-readable number for the course',
  `course_source_system_id` VARCHAR(256) NULL COMMENT 'The origin system id',
  `school_fk` BIGINT UNSIGNED NOT NULL COMMENT 'The foreign key to the school table',
  PRIMARY KEY (`course_id`),
  CONSTRAINT `fk_school$course`
    FOREIGN KEY (`school_fk`)
    REFERENCES `scholar_warehouse`.`school`(`school_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`section` (
  `section_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `section_name` VARCHAR(256) NULL COMMENT 'User defined human-readable name',
  `section_start_date` DATE NULL COMMENT 'The section start date',
  `section_end_date` DATE NULL COMMENT 'The section end date',
  `room` VARCHAR(256) NULL COMMENT 'Human-readable room name',
  `grade_formula` VARCHAR(16384) NULL COMMENT 'The section grading formula as a string',
  `course_fk` BIGINT UNSIGNED NOT NULL COMMENT 'The foreign key to the school table',
  `term_fk` BIGINT UNSIGNED NOT NULL COMMENT 'The foreign key to the term table',
  `number_of_terms` INTEGER UNSIGNED NULL COMMENT 'The number of terms that the section spans',
  `section_source_system_id` VARCHAR(256) NULL UNIQUE COMMENT 'The source system from which the entity was imported - the id from that system',
  `section_expression` BLOB COMMENT 'The section expression showing what period the section is taught',
  PRIMARY KEY (`section_id`),
  CONSTRAINT `fk_course$section`
    FOREIGN KEY (`course_fk`)
    REFERENCES `scholar_warehouse`.`course`(`course_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_term$section`
    FOREIGN KEY (`term_fk`)
    REFERENCES `scholar_warehouse`.`term`(`term_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`teacher_section` (
  `teacher_section_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `teacher_fk` BIGINT UNSIGNED COMMENT 'The FK to the teacher table',
  `section_fk` BIGINT UNSIGNED COMMENT 'The FK to the section table',
  `role` VARCHAR(256) NULL COMMENT 'Indicates the role the teacher has in the section',
  PRIMARY KEY (`teacher_section_id`),
  CONSTRAINT `teacher_section_teacher_fk`
    FOREIGN KEY(`teacher_fk`)
    REFERENCES `scholar_warehouse`.`teacher`(`teacher_user_fk`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `teacher_section_section_fk`
    FOREIGN KEY(`section_fk`)
    REFERENCES `scholar_warehouse`.`section`(`section_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
  ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`assignment` (
  `assignment_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `assignment_name` VARCHAR(256) NULL COMMENT 'User defined human-readable name',
  `type_fk` VARCHAR(255) NOT NULL COMMENT 'The assignment type string',
  `assignment_class` VARCHAR(256) NULL COMMENT 'The section start date',
  `assigned_date` DATE NULL COMMENT 'The section start date',
  `due_date` DATE NULL COMMENT 'The section end date',
  `available_points` BIGINT UNSIGNED NULL COMMENT 'The number of possible points to be awarded for an assignment',
  `section_fk` BIGINT UNSIGNED NOT NULL COMMENT 'The foreign key to the term table',
  `weight` DOUBLE NULL COMMENT 'The weighting for calculating the section grade for the assignment, if any',
  `user_defined_type` VARCHAR(256) NULL COMMENT 'Dynamically defined assignment type that can be created by the user, if any',
  `include_in_final_grades` BIT(1) COMMENT 'True if the assignment should be included in grade calculations, otherwise false',
  `assignment_source_system_id` VARCHAR(256) NULL UNIQUE COMMENT 'The source system from which the entity was imported - the id from that system',
  PRIMARY KEY (`assignment_id`),
  CONSTRAINT `fk_section$assignment`
    FOREIGN KEY (`section_fk`)
    REFERENCES `scholar_warehouse`.`section`(`section_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

ALTER TABLE `scholar_warehouse`.`assignment` ADD INDEX (`user_defined_type`);

CREATE TABLE `scholar_warehouse`.`student_assignment` (
  `student_assignment_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `student_assignment_name` VARCHAR(256) NULL COMMENT 'User defined human-readable name',
  `comment` BLOB NULL COMMENT 'Teacher comment on student assignment',
  `completion_date` DATE NULL COMMENT 'The date the student turned in the assignment',
  `awarded_points` DOUBLE NULL COMMENT 'The number of possible points to be awarded for an assignment',
  `assignment_fk` BIGINT UNSIGNED NOT NULL COMMENT 'The foreign key to the section assignment table',
  `student_fk` BIGINT UNSIGNED NOT NULL COMMENT 'The foreign key to the student table',
  `student_assignment_exempt` BIT(1) COMMENT 'Whether the student assignment is exempt from grade calculations',
  PRIMARY KEY (`student_assignment_id`),
  CONSTRAINT `fk_assignment$student_assignment`
    FOREIGN KEY (`assignment_fk`)
    REFERENCES `scholar_warehouse`.`assignment`(`assignment_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_student$student_assignment`
    FOREIGN KEY (`student_fk`)
    REFERENCES `scholar_warehouse`.`student`(`student_user_fk`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  UNIQUE KEY `assignment_fk$student_fk` (`assignment_fk`,`student_fk`))
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`student_section_grade` (
  `student_section_grade_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `complete` BIT(1) COMMENT 'Indicates whether the course grade is final',
  `grade` DOUBLE COMMENT 'Represents a single student grade in a section',
  `term_grades` VARCHAR(16384) NULL COMMENT 'Final term grades for the section, if they have been calulated',
  `section_fk` BIGINT UNSIGNED NOT NULL COMMENT 'The foreign key to the section table',
  `student_fk` BIGINT UNSIGNED NOT NULL COMMENT 'The foreign key to the student table',
  PRIMARY KEY (`student_section_grade_id`),
  CONSTRAINT `uniq_section$student` 
    UNIQUE (`section_fk`,`student_fk`),
  CONSTRAINT `fk_section$student_section_grade`
    FOREIGN KEY (`section_fk`)
    REFERENCES `scholar_warehouse`.`section`(`section_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_student$student_section_grade`
    FOREIGN KEY (`student_fk`)
    REFERENCES `scholar_warehouse`.`student`(`student_user_fk`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`gpa` (
  `gpa_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Auto incrementing primary key identity column',
  `student_fk` BIGINT UNSIGNED NOT NULL COMMENT 'Foreign key to the student table',
  `gpa_start_date` DATE NULL COMMENT 'The start date for the period for which the GPA is calculated',
  `gpa_end_date` DATE NULL COMMENT 'The end date for the period for which the GPA was calculated',
  `gpa_calc_date` DATE NOT NULL COMMENT 'The date the GPA was calculated',
  `gpa_type` VARCHAR(64) NOT NULL COMMENT 'Indicates the GPA calculation method',
  `gpa_score` DOUBLE NOT NULL COMMENT 'The GPA value',
  PRIMARY KEY (`gpa_id`),
  CONSTRAINT `uniq_calc_date$student`
    UNIQUE (`gpa_calc_date`,`student_fk`,`gpa_type`),
  CONSTRAINT `fk_student$gpa`
    FOREIGN KEY (`student_fk`)
    REFERENCES `scholar_warehouse`.`student`(`student_user_fk`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
 ENGINE = InnoDB;
 ALTER TABLE `scholar_warehouse`.`gpa` ADD INDEX (`gpa_type`);

 CREATE TABLE `scholar_warehouse`.`current_gpa` (
  `current_gpa_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Auto incrementing primary key identity column',
  `student_fk` BIGINT UNSIGNED NOT NULL COMMENT 'Foreign key to the student table',
  `gpa_fk` BIGINT UNSIGNED NOT NULL COMMENT 'Foreign key to the GPA table',
  PRIMARY KEY (`current_gpa_id`),
  CONSTRAINT `fk_student$current_gpa`
    FOREIGN KEY (`student_fk`)
    REFERENCES `scholar_warehouse`.`student`(`student_user_fk`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_gpa$current_gpa`
    FOREIGN KEY (`gpa_fk`)
    REFERENCES `scholar_warehouse`.`gpa`(`gpa_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
 ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`report` (
  `report_id`  INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto-incrementing primary key identity column',
  `school_fk` BIGINT UNSIGNED COMMENT 'The foreign key to the school table',
  `report` TEXT NOT NULL COMMENT 'The report meta-data in JSON string format',
  PRIMARY KEY (`report_id`),
  CONSTRAINT `fk_school$report`
    FOREIGN KEY (`school_fk`)
    REFERENCES `scholar_warehouse`.`school`(`school_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`behavior` (
  `behavior_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto-incrementing primary key identity column',
  `student_fk` BIGINT UNSIGNED NOT NULL COMMENT 'The foreign key to the student table',
  `user_fk` BIGINT UNSIGNED NULL COMMENT 'The foreign key to the user table',
  `name` VARCHAR(256) NULL COMMENT 'Human readable name of behavior event',
  `date` DATE NOT NULL COMMENT 'Date the behavior event occurred',
  `remote_system` VARCHAR(64) NULL COMMENT 'The name of the remote system that the remote_id columns refer to',
  `remote_behavior_id` VARCHAR(64) NULL COMMENT 'ID of the behavior in a remote system(currently only deanslist)',
  `remote_student_id` VARCHAR(256) NULL COMMENT 'ID of the student in a remote system (currently only deanslist)',
  `category` VARCHAR(256) NULL COMMENT 'Human readable category of the behavior event',
  `point_value` VARCHAR(256) NULL COMMENT 'Point value of the behavior',
  `roster` VARCHAR(256) NULL COMMENT 'Class where the event occurred',
  PRIMARY KEY (`behavior_id`),
  CONSTRAINT `fk_student$behavior`
    FOREIGN KEY (`student_fk`)
    REFERENCES `scholar_warehouse`.`student`(`student_user_fk`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_teacher$behavior`
    FOREIGN KEY (`user_fk`)
    REFERENCES `scholar_warehouse`.`user`(`user_id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  UNIQUE KEY `remote_system_composite` (`remote_system`, `remote_behavior_id`)
)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`authorities` (
    `authority_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto-incrementing primary key',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT 'The user id of user associated with this record',
    `authority` VARCHAR(50) NOT NULL COMMENT 'The User Role',
    PRIMARY KEY (`authority_id`),
    CONSTRAINT `user_id$authorities`
    FOREIGN KEY (`user_id`) REFERENCES `scholar_warehouse`.`user` (`user_id`)
      ON DELETE CASCADE
      ON UPDATE CASCADE
)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`school_day` (
    `school_day_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'System generated ID',
    `school_fk` BIGINT UNSIGNED NOT NULL COMMENT 'The school foreign key',
    `school_day_date` DATE NULL COMMENT 'The date of the school day',
    `school_day_source_system_id` VARCHAR(256) NULL UNIQUE,
    `school_day_source_system_other_id` BIGINT UNSIGNED NULL,
    PRIMARY KEY (`school_day_id`),
    FOREIGN KEY (`school_fk`) REFERENCES `scholar_warehouse`.`school` (`school_id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`attendance` (
    `attendance_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'System generated ID',
    `school_day_fk` BIGINT UNSIGNED NOT NULL COMMENT 'Foreign key to the school days table',
    `student_fk` BIGINT UNSIGNED NOT NULL COMMENT 'Foreign key to the student table',
    `section_fk` BIGINT UNSIGNED COMMENT 'Foreign key to the section table',
    `attendance_type` VARCHAR(64) NOT NULL COMMENT 'DAILY, SECTION, other',
    `attendance_status` VARCHAR(64) NOT NULL COMMENT 'Maps to POJO enum values PRESENT, EXCUSED_ABSENT, ABSENT, TARDY',
    `attendance_description` VARCHAR(256) NULL COMMENT 'Description of the attendance status, if any',
    `attendance_source_system_period_id` BIGINT UNSIGNED NULL,
    `attendance_source_system_id` VARCHAR(256) NULL,
    `attendance_code` VARCHAR(255) null COMMENT 'Depending on source system, can be used to indicate school vs. class attendance or other',
    PRIMARY KEY (`attendance_id`),
    FOREIGN KEY (`school_day_fk`) REFERENCES `scholar_warehouse`.`school_day` (`school_day_id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (`student_fk`) REFERENCES `scholar_warehouse`.`student` (`student_user_fk`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (`section_fk`) REFERENCES `scholar_warehouse`.`section`(`section_id`)
      ON DELETE CASCADE
      ON UPDATE CASCADE
)
ENGINE = InnoDB;

ALTER TABLE `scholar_warehouse`.`attendance` ADD INDEX (`attendance_type`);
ALTER TABLE `scholar_warehouse`.`attendance` ADD INDEX (`attendance_status`);

CREATE TABLE `scholar_warehouse`.`goal` (
  `goal_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary key identity column for a goal',
  `approved` INT NOT NULL COMMENT 'Int that should be 0 or 1 indicating if a goal was approved by the assigned teacher',
  `parent_fk` BIGINT(20) COMMENT 'Foreign key that could associate many different places depending on the goal. For assignment goals it points to student assignmnet id',
  `desired_value` DOUBLE NOT NULL COMMENT 'The value the student is attempting to reach with this goal',
  `student_fk` BIGINT UNSIGNED NOT NULL COMMENT 'Foreign key linking to the student this is assigned to',
  `teacher_fk` BIGINT UNSIGNED NULL COMMENT 'Foreign key linking to the teacher who needs to approve this goal',
  `goal_type` varchar(45) NOT NULL COMMENT ' Corresponds to enum GoalType, defines what subclass of goal we are dealing with',
  `start_date` DATE DEFAULT NULL COMMENT ' Certain goals occur over a time range, this indicates that starting point',
  `end_date` DATE DEFAULT NULL COMMENT ' Certain goals occur over a time range, this indicates the end date',
  `behavior_category` varchar(45) DEFAULT NULL COMMENT 'In behavior goals we need a more specific category. Corresponds to enum BehaviorType so show what type of behavior goal',
  `goal_aggregate`  BLOB DEFAULT NULL COMMENT 'Blob to store the JSON needed for formula goals',
  `name` varchar(45) NOT NULL COMMENT 'The name of the goal',
PRIMARY KEY (`goal_id`),
  CONSTRAINT `fk_student_goal`
    FOREIGN KEY (`student_fk`)
    REFERENCES `scholar_warehouse`.`student`(`student_user_fk`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_teacher_goal`
    FOREIGN KEY (`teacher_fk`)
    REFERENCES `scholar_warehouse`.`teacher`(`teacher_user_fk`)
    ON DELETE SET NULL
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`ui_attributes` (
    `ui_attributes_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary key identity column for a UI Attributes bag',
    `school_fk` BIGINT UNSIGNED NOT NULL UNIQUE COMMENT 'Unique foreign key to the school table',
    `attributes` BLOB NULL COMMENT 'Client-side attributes as unmanaged JSON',
    PRIMARY KEY (`ui_attributes_id`),
    FOREIGN KEY (`school_fk`) REFERENCES `scholar_warehouse`.`school` (`school_id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`survey` (
  `survey_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary key identity column for a survey',
  `school_fk` BIGINT UNSIGNED NULL COMMENT 'Foreign key linking to the school this survey was created in, null for district wide',
  `section_fk` BIGINT UNSIGNED NULL COMMENT 'Foreign key linking to the section this survey was created in, null for a school wide survey',
  `user_fk` BIGINT UNSIGNED NOT NULL COMMENT 'Foreign key linking to the user table for the creating user',
  `survey_name` VARCHAR(256) NULL COMMENT 'The human readable name of the survey',
  `survey_created_date` DATE DEFAULT NULL COMMENT 'The date the survey was created',
  `survey_administer_date` DATE DEFAULT NULL COMMENT 'The date of survey administration',
  `survey_schema` BLOB DEFAULT NULL COMMENT 'Blob to store the JSON needed for formula goals',
  PRIMARY KEY (`survey_id`),
  FOREIGN KEY (`user_fk`)
    REFERENCES `scholar_warehouse`.`user`(`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (`school_fk`)
    REFERENCES `scholar_warehouse`.`school`(`school_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (`section_fk`)
    REFERENCES `scholar_warehouse`.`section`(`section_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;
ALTER TABLE `scholar_warehouse`.`survey` ADD INDEX (`survey_created_date`);
ALTER TABLE `scholar_warehouse`.`survey` ADD INDEX (`survey_administer_date`);

CREATE TABLE `scholar_warehouse`.`survey_response` (
  `survey_response_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary key identity column for a survey',
  `student_fk` BIGINT UNSIGNED NOT NULL COMMENT 'Foreign key linking to the user table for the responding user',
  `survey_fk` BIGINT UNSIGNED NOT NULL COMMENT 'The FK to the parent survey associated with the response',
  `survey_response_date` DATE DEFAULT NULL COMMENT 'The date the survey was completed',
  `survey_response` BLOB DEFAULT NULL COMMENT 'Blob to store the JSON formatted survey response',
  PRIMARY KEY (`survey_response_id`),
  FOREIGN KEY (`student_fk`)
    REFERENCES `scholar_warehouse`.`student`(`student_user_fk`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (`survey_fk`)
    REFERENCES `scholar_warehouse`.`survey`(`survey_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;
ALTER TABLE `scholar_warehouse`.`survey_response` ADD INDEX (`survey_response_date`);

CREATE TABLE `scholar_warehouse`.`notification_group` (
  `notification_group_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary key identity column for a notification group',
  `notification_group_type` VARCHAR(64) NOT NULL COMMENT 'Serialized NotificationGroupType, Hibernate discriminator column',
  `student_fk` BIGINT UNSIGNED NULL COMMENT 'Foreign key linking to the student table for owning student, if any',
  `teacher_fk` BIGINT UNSIGNED NULL COMMENT 'Foreign key linking to the teacher table for owning teacher, if any',
  `administrator_fk` BIGINT UNSIGNED NULL COMMENT 'Foreign key linking to the administrator table for owning administrator, if any',
  `section_fk` BIGINT UNSIGNED NULL COMMENT 'Foreign key linking to the section table for subject section, if any',
  `notification_group_student_filter` BLOB COMMENT 'JSON formatter FilteredStudents values.',
  PRIMARY KEY (`notification_group_id`),
  FOREIGN KEY (`student_fk`)
    REFERENCES `scholar_warehouse`.`student`(`student_user_fk`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (`teacher_fk`)
    REFERENCES `scholar_warehouse`.`teacher`(`teacher_user_fk`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (`administrator_fk`)
    REFERENCES `scholar_warehouse`.`administrator`(`administrator_user_fk`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (`section_fk`)
    REFERENCES `scholar_warehouse`.`section`(`section_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`notification` (
  `notification_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary key identity column for a survey',
  `notification_name` VARCHAR(256) NULL COMMENT 'Human readable display name for the notification',
  `school_fk` BIGINT UNSIGNED NULL COMMENT 'Foriegn key to the school table, if any',
  `section_fk` BIGINT UNSIGNED NULL COMMENT 'Foriegn key to the section table, if any',
  `user_fk` BIGINT UNSIGNED NOT NULL COMMENT 'Foriegn key to the users table. Indicates creator and owner of the notification',
  `owning_group_fk` BIGINT UNSIGNED NOT NULL COMMENT 'Foreign key linking to the student table for owning student, if any',
  `subject_group_fk` BIGINT UNSIGNED NOT NULL COMMENT 'Foreign key linking to the student table for subject student, if any',
  `notification_trigger` DOUBLE NOT NULL COMMENT 'The value at which the notification is triggered',
  `notification_aggregate_function` VARCHAR(256) NULL COMMENT 'SUM, AVG, STD_DEV, & so on',
  `notification_window` BLOB NULL COMMENT 'Day, week, month combined with flag for percent change vs. value trigger',
  `notification_measure` VARCHAR(64) NULL COMMENT 'GPA, SECTION_GRADE, SUSPENSION, etc.',
  `notification_created_date` DATE DEFAULT NULL COMMENT 'The date the notification was created',
  `notification_expiry_date` DATE DEFAULT NULL COMMENT 'The date the notification expires',
  PRIMARY KEY (`notification_id`),
  FOREIGN KEY (`school_fk`)
    REFERENCES `scholar_warehouse`.`school`(`school_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (`section_fk`)
    REFERENCES `scholar_warehouse`.`section`(`section_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (`user_fk`)
    REFERENCES `scholar_warehouse`.`user`(`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (`owning_group_fk`)
    REFERENCES `scholar_warehouse`.`notification_group`(`notification_group_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (`subject_group_fk`)
    REFERENCES `scholar_warehouse`.`notification_group`(`notification_group_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`triggered_notification` (
  `triggered_notification_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary key identity column for a triggered notification',
  `user_fk` BIGINT UNSIGNED NOT NULL COMMENT 'The user to be alerted about the notification being triggered',
  `notification_fk` BIGINT UNSIGNED NOT NULL COMMENT 'Foriegn key to the notification table, required',
  `triggered_notification_date` DATE NOT NULL COMMENT 'The date the notification was triggered',
  `triggered_notification_active` BIT(1) COMMENT 'True if the triggered notification is active, otherwise false',
  `triggered_notification_positive` BIT(1) COMMENT 'True if the triggered notification indicates a positive trend, otherwise false',
  `triggered_notification_value_when_triggered` DOUBLE NOT NULL COMMENT 'The value that triggered the notification',
  PRIMARY KEY (`triggered_notification_id`),
  FOREIGN KEY (`notification_fk`)
    REFERENCES `scholar_warehouse`.`notification`(`notification_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (`user_fk`)
    REFERENCES `scholar_warehouse`.`user` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;