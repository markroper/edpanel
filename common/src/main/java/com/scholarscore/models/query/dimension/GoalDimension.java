package com.scholarscore.models.query.dimension;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.goal.Goal;
import com.scholarscore.models.query.Dimension;

import java.util.Set;

/**
 * Created by markroper on 3/4/16.
 */
public class GoalDimension extends BaseDimension {
    public static final String TYPE = "Type";
    public static final String APPROVED = "Approved";
    public static final String START_DATE = "Start date";
    public static final String STUDENT = "Student";
    public static final String TEACHER = "Teacher";
    public static final String END_DATE = "End date";
    public static final String SECTION = "Section";
    public static final String PROGRESS = "Progress";
    public static final String TEACHER_FOLLOWUP = "Teacher followup";
    public static final Set<String> DIMENSION_FIELDS =
            ImmutableSet.of(ID, NAME, TYPE, APPROVED, START_DATE, END_DATE, PROGRESS, TEACHER_FOLLOWUP, STUDENT, TEACHER, SECTION);
    public static final Set<Dimension> PARENT_DIMENSIONS =
            ImmutableSet.of(Dimension.STUDENT);

    @Override
    public Dimension getType() {
        return Dimension.GOAL;
    }

    @Override
    public Class<?> getAssociatedClass() {
        return Goal.class;
    }

    @Override
    public Set<Dimension> getParentDimensions() {
        return PARENT_DIMENSIONS;
    }

    @Override
    public Set<String> getFields() {
        return DIMENSION_FIELDS;
    }

    @Override
    public String getName() {
        return "Goal";
    }
}
