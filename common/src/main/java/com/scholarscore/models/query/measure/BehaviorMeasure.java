package com.scholarscore.models.query.measure;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public abstract class BehaviorMeasure {
    
    public static final String ID = "ID";
    public static final String STUDENT_FK = "Student";
    public static final String TEACHER_FK = "Teacher";
    public static final String DATE = "Behavior Date";
    public static final String POINTS = "Points";
    public static final String CATEGORY = "Category";
    public static final String ROSTER = "Roster";
    public static final Set<String> BEHAVIOR_FIELDS = 
            ImmutableSet.of(ID, STUDENT_FK, TEACHER_FK, DATE, POINTS, CATEGORY, ROSTER);
    
    public Set<String> getFields() {
        return BEHAVIOR_FIELDS;
    }

}
