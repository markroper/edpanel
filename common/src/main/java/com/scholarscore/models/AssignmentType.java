package com.scholarscore.models;

import java.io.Serializable;

/**
 * Enumerates the supported types of assignments in the system, including a USER_DEFINED 
 * type that can be used as needed by end users.
 * 
 * @author markroper
 *
 */
public enum AssignmentType implements Serializable {
    ATTENDANCE, 
    HOMEWORK,
    QUIZ,
    TEST,
    MIDTERM,
    FINAL,
    LAB,
    CLASSWORK,
    USER_DEFINED;
    
    public static AssignmentType toAssignmentType(String input) {
        return AssignmentType.valueOf(input);
    }
}
