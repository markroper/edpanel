package com.scholarscore.models.query.dimension;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.School;
import com.scholarscore.models.query.Dimension;

import java.util.Set;

@SuppressWarnings("serial")
public class SchoolDimension extends BaseDimension implements IDimension {
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
        return School.class;
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
