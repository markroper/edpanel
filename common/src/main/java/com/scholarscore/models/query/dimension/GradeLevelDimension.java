package com.scholarscore.models.query.dimension;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.GradeLevel;
import com.scholarscore.models.query.Dimension;

public class GradeLevelDimension implements IDimension {
    public static final Set<String> DIMENSION_FIELDS = 
            ImmutableSet.of("ID", "Name");
    public static final Set<Dimension> PARENT_DIMENSIONS = 
            ImmutableSet.of(Dimension.SCHOOL);
    
    @Override
    public Dimension getType() {
        return Dimension.GRADE_LEVEL;
    }

    @Override
    @JsonIgnore
    public Class<?> getAssociatedClass() {
        return GradeLevel.class;
    }

    @Override
    public Set<Dimension> getParentDimensions() {
        return GradeLevelDimension.PARENT_DIMENSIONS;
    }

    @Override
    public Set<String> getFields() {
        return GradeLevelDimension.DIMENSION_FIELDS;
    }
}
