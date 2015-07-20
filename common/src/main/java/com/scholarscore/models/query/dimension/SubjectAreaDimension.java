package com.scholarscore.models.query.dimension;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.SubjectArea;
import com.scholarscore.models.query.Dimension;

@SuppressWarnings("serial")
public class SubjectAreaDimension implements IDimension {
    public static final Set<String> DIMENSION_FIELDS = 
            ImmutableSet.of("ID", "Name");
    public static final Set<Dimension> PARENT_DIMENSIONS = 
            ImmutableSet.of(Dimension.SCHOOL);
    
    @Override
    public Dimension getType() {
        return Dimension.SUBJECT_AREA;
    }

    @Override
    @JsonIgnore
    public Class<?> getAssociatedClass() {
        return SubjectArea.class;
    }

    @Override
    public Set<Dimension> getParentDimensions() {
        return SubjectAreaDimension.PARENT_DIMENSIONS;
    }

    @Override
    public Set<String> getFields() {
        return SubjectAreaDimension.DIMENSION_FIELDS;
    }

    @Override
    public String getName() {
        return "Subject";
    }
}
