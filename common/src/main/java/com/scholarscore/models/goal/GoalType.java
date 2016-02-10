package com.scholarscore.models.goal;

import java.io.Serializable;

/**
 * Enum for different types of goals.
 * ATTENDANCE, TERM AND SECTION goals are yet to be implemented
 * Created by cwallace on 9/17/2015.
 */
public enum GoalType implements Serializable {
    ATTENDANCE,
    BEHAVIOR,
    SECTION_GRADE,
    ASSIGNMENT,
    COMPLEX,
    CONSTANT
}
