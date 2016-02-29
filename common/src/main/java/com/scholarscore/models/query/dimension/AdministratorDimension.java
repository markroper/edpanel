package com.scholarscore.models.query.dimension;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.user.Staff;

import java.util.Set;

@SuppressWarnings("serial")
public class AdministratorDimension extends BaseDimension {
    public static final String EMAIL = "Email";
    public static final String SCHOOL = "School";
    public static final Set<String> DIMENSION_FIELDS = 
            ImmutableSet.of(ID, NAME, EMAIL, SCHOOL);
    public static final Set<Dimension> PARENT_DIMENSIONS = 
            ImmutableSet.of(Dimension.SCHOOL);
    
    @Override
    public Dimension getType() {
        return Dimension.ADMINISTRATOR;
    }

    @Override
    @JsonIgnore
    public Class<?> getAssociatedClass() {
        return Staff.class;
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
