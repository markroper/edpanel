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
        AssignmentType returnType = null;
        switch(input) {
        case "GRADED":
            returnType = GRADED;
            break;
        case "ATTENDENCE":
            returnType = ATTENDANCE;
            break;
        case "HOMEWORK":
            returnType = HOMEWORK;
            break;
        case "QUIZ":
            returnType = QUIZ;
            break;
        case "TEST":
            returnType = TEST;
            break;
        case "MIDTERM":
            returnType = MIDTERM;
            break;
        case "FINAL":
            returnType = FINAL;
            break;
        case "LAB":
            returnType = LAB;
            break;
        case "CLASSWORK":
            returnType = CLASSWORK;
            break;
        case "USER_DEFINED":
            returnType = USER_DEFINED;
            break;
        default:
            break;
        }
        return returnType;
    }
}
