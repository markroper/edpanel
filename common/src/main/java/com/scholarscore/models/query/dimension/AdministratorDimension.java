package com.scholarscore.models.query.dimension;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.user.Teacher;

import java.util.Set;

@SuppressWarnings("serial")
public class AdministratorDimension implements IDimension {
    public static final String ID = "ID";
    public static final String NAME = "Name";
    public static final String EMAIL = "Email";
    public static final Set<String> DIMENSION_FIELDS = 
            ImmutableSet.of(ID, NAME, EMAIL);
    public static final Set<Dimension> PARENT_DIMENSIONS = 
            ImmutableSet.of(Dimension.SCHOOL);
    
    @Override
    public Dimension getType() {
        return Dimension.TEACHER;
    }

    @Override
    @JsonIgnore
    public Class<?> getAssociatedClass() {
        return Teacher.class;
    }

    @Override
    public Set<Dimension> getParentDimensions() {
        return AdministratorDimension.PARENT_DIMENSIONS;
    }

    @Override
    public Set<String> getFields() {
        return AdministratorDimension.DIMENSION_FIELDS;
    }

    @Override
    public String getName() {
        return "Administrator";
    }
}
