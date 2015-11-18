package com.scholarscore.models.query.dimension;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.query.Dimension;

import java.util.Set;

/**
 * Created by markroper on 11/17/15.
 */
public class AssignmentDimension implements IDimension {
    public static final String ID = "ID";
    public static final String NAME = "Name";
    public static final String DUE_DATE = "Due Date";
    public static final String AVAILABLE_POINTS = "Available Points";
    public static final Set<String> DIMENSION_FIELDS =
            ImmutableSet.of(ID, NAME, DUE_DATE);
    public static final Set<Dimension> PARENT_DIMENSIONS =
            ImmutableSet.of(Dimension.SECTION);

    @Override
    public Dimension getType() {
        return Dimension.ASSIGNMENT;
    }

    @Override
    public Class<?> getAssociatedClass() {
        return Assignment.class;
    }

    @Override
    public Set<Dimension> getParentDimensions() {
        return AssignmentDimension.PARENT_DIMENSIONS;
    }

    @Override
    public Set<String> getFields() {
        return AssignmentDimension.DIMENSION_FIELDS;
    }

    @Override
    public String getName() {
        return "Assignment";
    }
}
