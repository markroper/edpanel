package com.scholarscore.models;

/**
 * Enumerates the supported types of assignments in the system, including a USER_DEFINED 
 * type that can be used as needed by end users.
 * 
 * @author markroper
 *
 */
public enum AssignmentType {
    GRADED, //TODO: make abstract when the subclasses of this class are 
    ATTENDANCE, 
    HOMEWORK,
    QUIZ,
    TEST,
    MIDTERM,
    FINAL,
    LAB,
    CLASSWORK,
    USER_DEFINED;
}
