CREATE TABLE `scholar_warehouse`.`school` (
  `school_id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key column for the school table.',
  `school_name` VARCHAR(256) NULL COMMENT 'A human readable user-defined name',
  PRIMARY KEY (`school_id`))
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`student` (
  `student_id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `student_name` VARCHAR(256) NULL COMMENT 'User defined human-readable name',
  `source_system_id` VARCHAR(256) NULL COMMENT 'The identifier from the source system, if any',
  `mailing_street` VARCHAR(512) NULL COMMENT 'The street portion of the mailing address',
  `mailing_city` VARCHAR(256) NULL COMMENT 'The city portion of the student mailing address',
  `mailing_state` VARCHAR(256) NULL COMMENT 'The state abbreviation of the student mailing address',
  `mailing_postal_code` VARCHAR(256) NULL COMMENT 'The postal code of the student mailing address',
  `home_street` VARCHAR(512) NULL COMMENT 'The street portion of the home address',
  `home_city` VARCHAR(256) NULL COMMENT 'The city portion of the student home address',
  `home_state` VARCHAR(256) NULL COMMENT 'The state abbreviation of the student home address',
  `home_postal_code` VARCHAR(256) NULL COMMENT 'The postal code of the student home address',
  `gender` VARCHAR(32) NULL COMMENT 'The gender of the student',
  `birth_date` DATETIME NULL COMMENT 'The birth date of the student',
  `district_entry_date` DATETIME NULL COMMENT 'The date the student entered the school district',
  `projected_graduation_year` INT UNSIGNED NULL COMMENT 'The projected year of graduation for the student. For example: 2020',
  `social_security_number` VARCHAR(256) NULL COMMENT 'The student\'s social security number',
  `federal_race` VARCHAR(512) NULL COMMENT 'The student\'s race according to the federal gov\'t',
  `federal_ethnicity` VARCHAR(512) NULL COMMENT 'The student\'s ethnicity according to the federal gov\'t',
  `school_fk` INT UNSIGNED NULL COMMENT 'The foreign key to the current school the student is enrolled in within the district',
  PRIMARY KEY (`student_id`),
  CONSTRAINT `school_fk$student`
    FOREIGN KEY (`school_fk`)
    REFERENCES `scholar_warehouse`.`school`(`school_id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`teacher` (
  `teacher_id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `teacher_name` VARCHAR(256) NULL COMMENT 'User defined human-readable name',
  PRIMARY KEY (`teacher_id`))
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`school_year` (
  `school_year_id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `school_year_name` VARCHAR(256) NULL COMMENT 'User defined human-readable name',
  `school_year_start_date` DATETIME NULL COMMENT 'The school year starting date',
  `school_year_end_date` DATETIME NULL COMMENT 'The school year end date',
  `school_fk` INT UNSIGNED NOT NULL COMMENT 'The foreign key to the school table',
  PRIMARY KEY (`school_year_id`),
  CONSTRAINT `fk_school$school_year`
    FOREIGN KEY (`school_fk`)
    REFERENCES `scholar_warehouse`.`school`(`school_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`term` (
  `term_id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `term_name` VARCHAR(256) NULL COMMENT 'User defined human-readable name',
  `term_start_date` DATETIME NULL COMMENT 'The school term start date',
  `term_end_date` DATETIME NULL COMMENT 'The school term end date',
  `school_year_fk` INT UNSIGNED NOT NULL COMMENT 'The foreign key to the school table',
  PRIMARY KEY (`term_id`),
  CONSTRAINT `fk_school_year$school_term`
    FOREIGN KEY (`school_year_fk`)
    REFERENCES `scholar_warehouse`.`school_year`(`school_year_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`course` (
  `course_id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `course_name` VARCHAR(256) NULL COMMENT 'User defined human-readable name',
  `school_fk` INT UNSIGNED NOT NULL COMMENT 'The foreign key to the school table',
  PRIMARY KEY (`course_id`),
  CONSTRAINT `fk_school$course`
    FOREIGN KEY (`school_fk`)
    REFERENCES `scholar_warehouse`.`school`(`school_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`section` (
  `section_id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `section_name` VARCHAR(256) NULL COMMENT 'User defined human-readable name',
  `section_start_date` DATETIME NULL COMMENT 'The section start date',
  `section_end_date` DATETIME NULL COMMENT 'The section end date',
  `room` VARCHAR(256) NULL COMMENT 'Human-readable room name',
  `grade_formula` VARCHAR(1024) NULL COMMENT 'The section grading formula as a string',
  `course_fk` INT UNSIGNED NOT NULL COMMENT 'The foreign key to the school table',
  `term_fk` INT UNSIGNED NOT NULL COMMENT 'The foreign key to the term table',
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

CREATE TABLE `scholar_warehouse`.`assignment` (
  `assignment_id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `assignment_name` VARCHAR(256) NULL COMMENT 'User defined human-readable name',
  `type_fk` varchar(256) NOT NULL COMMENT 'The assignment type string',
  `assigned_date` DATETIME NULL COMMENT 'The section start date',
  `due_date` DATETIME NULL COMMENT 'The section end date',
  `available_points` INT UNSIGNED NULL COMMENT 'The number of possible points to be awarded for an assignment',
  `section_fk` INT UNSIGNED NOT NULL COMMENT 'The foreign key to the term table',
  PRIMARY KEY (`assignment_id`),
  CONSTRAINT `fk_section$assignment`
    FOREIGN KEY (`section_fk`)
    REFERENCES `scholar_warehouse`.`section`(`section_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE TABLE `scholar_warehouse`.`student_assignment` (
  `student_assignment_id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `student_assignment_name` VARCHAR(256) NULL COMMENT 'User defined human-readable name',
  `completed` BIT(1) COMMENT 'Boolean indicating whether or not the assignment was completed',
  `completion_date` DATETIME NULL COMMENT 'The date the student turned in the assignment',
  `awarded_points` INT UNSIGNED NULL COMMENT 'The number of possible points to be awarded for an assignment',
  `assignment_fk` INT UNSIGNED NOT NULL COMMENT 'The foreign key to the section assignment table',
  `student_fk` INT UNSIGNED NOT NULL COMMENT 'The foreign key to the student table',
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
  `student_section_grade_id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The auto incrementing primary key identity column',
  `complete` BIT(1) COMMENT 'Indicates whether the course grade is final',
  `grade` DOUBLE COMMENT 'Represents a single student grade in a section',
  `section_fk` INT UNSIGNED NOT NULL COMMENT 'The foreign key to the section table',
  `student_fk` INT UNSIGNED NOT NULL COMMENT 'The foreign key to the student table',
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
  `school_fk` INT UNSIGNED NOT NULL COMMENT 'The foreign key to the school table',
  `report` TEXT NOT NULL COMMENT 'The report meta-data in JSON string format',
  PRIMARY KEY (`report_id`),
  CONSTRAINT `fk_school$report`
    FOREIGN KEY (`school_fk`)
    REFERENCES `scholar_warehouse`.`school`(`school_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
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
