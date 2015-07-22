package com.scholarscore.models.query.dimension;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.GradeLevel;
import com.scholarscore.models.query.Dimension;

@SuppressWarnings("serial")
public class SchoolDimension implements IDimension {
    public static final String ID = "ID";
    public static final String NAME = "Name";
    public static final String ADDRESS = "Address";
    public static final Set<String> DIMENSION_FIELDS = 
            ImmutableSet.of(ID, NAME, ADDRESS);
    public static final Set<Dimension> PARENT_DIMENSIONS = null;
    
    @Override
    public Dimension getType() {
        return Dimension.SCHOOL;
    }

    @Override
    @JsonIgnore
    public Class<?> getAssociatedClass() {
        return GradeLevel.class;
    }

    @Override
    public Set<Dimension> getParentDimensions() {
        return SchoolDimension.PARENT_DIMENSIONS;
    }

    @Override
    public Set<String> getFields() {
        return SchoolDimension.DIMENSION_FIELDS;
    }

    @Override
    public String getName() {
        return "School";
    }
}