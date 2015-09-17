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
  `school_name` VARCHAR(256) NULL COMMENT 'A human readable user-defined name',
  `school_address_fk` BIGINT UNSIGNED COMMENT 'The FK pointer back to the address of the school',
  `main_phone` VARCHAR(256) NULL COMMENT 'The school\'s main phone number',
  `principal_name` VARCHAR(256) NULL COMMENT 'The principal\'s name',
  `principal_email` VARCHAR(256) NULL COMMENT 'The principa\'s email address',
  `sourceSystemId` VARCHAR(256) NULL COMMENT 'The source system from which the entity was imported - the id from that system',
  PRIMARY KEY (`school_id`))
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`student` (
  `student_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `student_name` VARCHAR(256) NULL COMMENT 'User defined human-readable name',
  `source_system_id` VARCHAR(256) NULL COMMENT 'The identifier from the source system, if any',
  `mailing_fk` BIGINT UNSIGNED NULL COMMENT 'The address FK for mailing address',
  `home_fk` BIGINT UNSIGNED NULL COMMENT 'The address FK for home address',
  `gender` INT NULL COMMENT 'The gender of the student',
  `username` VARCHAR(256) NULL COMMENT 'The username of the student',
  `birth_date` DATETIME NULL COMMENT 'The birth date of the student',
  `district_entry_date` DATETIME NULL COMMENT 'The date the student entered the school district',
  `projected_graduation_year` BIGINT UNSIGNED NULL COMMENT 'The projected year of graduation for the student. For example: 2020',
  `social_security_number` VARCHAR(256) NULL COMMENT 'The student\'s social security number',
  `federal_race` VARCHAR(512) NULL COMMENT 'The student\'s race according to the federal gov\'t',
  `federal_ethnicity` VARCHAR(512) NULL COMMENT 'The student\'s ethnicity according to the federal gov\'t',
  `school_fk` BIGINT UNSIGNED NULL COMMENT 'The foreign key to the current school the student is enrolled in within the district',
  PRIMARY KEY (`student_id`),
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
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`teacher` (
  `teacher_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `teacher_name` VARCHAR(256) NULL COMMENT 'User defined human-readable name',
  `teacher_source_system_id` VARCHAR(256) NULL,
  `teacher_username` VARCHAR(256) NULL COMMENT 'A link back to the users table',
  `teacher_home_phone` VARCHAR(256) NULL COMMENT 'Home phone number for teacher',
  `teacher_homeAddress_fk` BIGINT UNSIGNED COMMENT 'The home address FK',
  PRIMARY KEY (`teacher_id`),
  CONSTRAINT `teacher_homeAddress_fk$teacher`
  FOREIGN KEY (`teacher_homeAddress_fk`) REFERENCES `scholar_warehouse`.`address`(`address_id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE)
  ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`administrator` (
  `administrator_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `administrator_name` VARCHAR(256) NULL COMMENT 'User defined human-readable name',
  `administrator_home_phone` VARCHAR(256) NULL,
  `administrator_homeAddress_fk` BIGINT UNSIGNED COMMENT 'The home address FK',
  `administrator_source_system_id` VARCHAR(256) NULL,
  `administrator_username` VARCHAR(256) NULL COMMENT 'A link back to the users table',
  PRIMARY KEY (`administrator_id`),
  CONSTRAINT `administrator_homeAddress_fk$administrator`
  FOREIGN KEY (`administrator_homeAddress_fk`)
    REFERENCES `scholar_warehouse`.`address`(`address_id`)
  ON DELETE SET NULL
  ON UPDATE CASCADE)
  ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`school_year` (
  `school_year_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `school_year_name` VARCHAR(256) NULL COMMENT 'User defined human-readable name',
  `school_year_start_date` DATETIME NULL COMMENT 'The school year starting date',
  `school_year_end_date` DATETIME NULL COMMENT 'The school year end date',
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
  `term_start_date` DATETIME NULL COMMENT 'The school term start date',
  `term_end_date` DATETIME NULL COMMENT 'The school term end date',
  `school_year_fk` BIGINT UNSIGNED NOT NULL COMMENT 'The foreign key to the school table',
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
  `section_start_date` DATETIME NULL COMMENT 'The section start date',
  `section_end_date` DATETIME NULL COMMENT 'The section end date',
  `room` VARCHAR(256) NULL COMMENT 'Human-readable room name',
  `grade_formula` VARCHAR(1024) NULL COMMENT 'The section grading formula as a string',
  `course_fk` BIGINT UNSIGNED NOT NULL COMMENT 'The foreign key to the school table',
  `term_fk` BIGINT UNSIGNED NOT NULL COMMENT 'The foreign key to the term table',
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
    REFERENCES `scholar_warehouse`.`teacher`(`teacher_id`)
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
  `type_fk` INT NOT NULL COMMENT 'The assignment type string',
  `assignmentClass` VARCHAR(256) NULL COMMENT 'The section start date',
  `assigned_date` DATETIME NULL COMMENT 'The section start date',
  `due_date` DATETIME NULL COMMENT 'The section end date',
  `available_points` BIGINT UNSIGNED NULL COMMENT 'The number of possible points to be awarded for an assignment',
  `section_fk` BIGINT UNSIGNED NOT NULL COMMENT 'The foreign key to the term table',
  PRIMARY KEY (`assignment_id`),
  CONSTRAINT `fk_section$assignment`
    FOREIGN KEY (`section_fk`)
    REFERENCES `scholar_warehouse`.`section`(`section_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`student_assignment` (
  `student_assignment_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `student_assignment_name` VARCHAR(256) NULL COMMENT 'User defined human-readable name',
  `completed` BIT(1) COMMENT 'Boolean indicating whether or not the assignment was completed',
  `completion_date` DATETIME NULL COMMENT 'The date the student turned in the assignment',
  `awarded_points` BIGINT UNSIGNED NULL COMMENT 'The number of possible points to be awarded for an assignment',
  `assignment_fk` BIGINT UNSIGNED NOT NULL COMMENT 'The foreign key to the section assignment table',
  `student_fk` BIGINT UNSIGNED NOT NULL COMMENT 'The foreign key to the student table',
  PRIMARY KEY (`student_assignment_id`),
  CONSTRAINT `fk_assignment$student_assignment`
    FOREIGN KEY (`assignment_fk`)
    REFERENCES `scholar_warehouse`.`assignment`(`assignment_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_student$student_assignment`
    FOREIGN KEY (`student_fk`)
    REFERENCES `scholar_warehouse`.`student`(`student_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`student_section_grade` (
  `student_section_grade_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `complete` BIT(1) COMMENT 'Indicates whether the course grade is final',
  `grade` DOUBLE COMMENT 'Represents a single student grade in a section',
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
    REFERENCES `scholar_warehouse`.`student`(`student_id`)
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
  `teacher_fk` BIGINT UNSIGNED NULL COMMENT 'The foreign key to the teacher table',
  `name` VARCHAR(256) NULL COMMENT 'Human readable name of behavior event',
  `date` DATETIME NULL COMMENT 'Date the behavior event occurred',
  `remote_system` VARCHAR(64) NULL COMMENT 'The name of the remote system that the remote_id columns refer to',
  `remote_behavior_id` VARCHAR(64) NULL COMMENT 'ID of the behavior in a remote system(currently only deanslist)',
  `remote_student_id` VARCHAR(256) NULL COMMENT 'ID of the student in a remote system (currently only deanslist)',
  `category` VARCHAR(256) NULL COMMENT 'Human readable category of the behavior event',
  `point_value` VARCHAR(256) NULL COMMENT 'Point value of the behavior',
  `roster` VARCHAR(256) NULL COMMENT 'Class where the event occurred',
  PRIMARY KEY (`behavior_id`),
  CONSTRAINT `fk_student$behavior`
    FOREIGN KEY (`student_fk`)
    REFERENCES `scholar_warehouse`.`student`(`student_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_teacher$behavior`
    FOREIGN KEY (`teacher_fk`)
    REFERENCES `scholar_warehouse`.`teacher`(`teacher_id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  UNIQUE KEY `remote_system_composite` (`remote_system`, `remote_behavior_id`)
)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`users` (
    `username` varchar(50) NOT NULL,
    `password` varchar(50) CHARACTER SET UTF8 NOT NULL,
    `enabled` BOOLEAN NOT NULL,
    PRIMARY KEY (username))
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`authorities` (
    `username` varchar(50) not null COMMENT 'The associated user with this record',
    `authority` varchar(50) not null COMMENT 'The Users Role',
    CONSTRAINT `fk_authorities_users` 
    FOREIGN KEY(`username`) 
    REFERENCES `scholar_warehouse`.`users`(`username`)
        ON DELETE CASCADE
        ON UPDATE CASCADE)
ENGINE = InnoDB;
CREATE INDEX `ix_auth_username` on `scholar_warehouse`.`authorities`(`username`,`authority`);

insert into `scholar_warehouse`.`users` (username, password, enabled) values ('mroper', 'admin', 1);
insert into `scholar_warehouse`.`users` (username, password, enabled) values ('mattg', 'admin', 1);

insert into `scholar_warehouse`.`authorities` (username, authority) values ('mroper', 'ADMIN');
insert into `scholar_warehouse`.`authorities` (username, authority) values ('mattg', 'ADMIN');

insert into `scholar_warehouse`.`administrator` (administrator_name, administrator_username) values ('Mark Roper', 'mroper');
insert into `scholar_warehouse`.`administrator` (administrator_name, administrator_username) values ('Matt Greenwood', 'mattg');
