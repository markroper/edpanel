package com.scholarscore.models.query.measure.behavior;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.measure.BaseMeasure;

import java.util.Set;

public abstract class BehaviorMeasure extends BaseMeasure {

    final Set<Dimension> compatibleDimensions = ImmutableSet.of(Dimension.TERM, Dimension.YEAR,
            Dimension.STUDENT, Dimension.TEACHER, Dimension.SCHOOL, Dimension.STAFF);
    
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
