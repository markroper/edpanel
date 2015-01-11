package com.scholarscore.models;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.scholarscore.models.serializers.AssignmentTypeDeserializer;

/**
 * Enumerates the supported types of assignments in the system, including a USER_DEFINED 
 * type that can be used as needed by end users.
 * 
 * @author markroper
 *
 */
@JsonDeserialize(using = AssignmentTypeDeserializer.class)
public enum AssignmentType implements Serializable {
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
    
    public static AssignmentType toAssignmentType(String input) {
        return AssignmentType.valueOf(input);
    }
}
