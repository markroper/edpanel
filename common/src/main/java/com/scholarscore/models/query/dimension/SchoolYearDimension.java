package com.scholarscore.models.query.dimension;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.query.Dimension;

import java.util.Set;

@SuppressWarnings("serial")
public class SchoolYearDimension extends BaseDimension implements IDimension {
    public static final Set<String> DIMENSION_FIELDS = 
            ImmutableSet.of(ID, START_DATE, END_DATE);
    public static final Set<Dimension> PARENT_DIMENSIONS = 
            ImmutableSet.of(Dimension.SCHOOL);
    
    @Override
    public Dimension getType() {
        return Dimension.YEAR;
    }

    @Override
    @JsonIgnore
    public Class<?> getAssociatedClass() {
        return SchoolYear.class;
    }

    @Override
    public Set<Dimension> getParentDimensions() {
        return SchoolYearDimension.PARENT_DIMENSIONS;
    }

    @Override
    public Set<String> getFields() {
        return SchoolYearDimension.DIMENSION_FIELDS;
    }

    @Override
    public String getName() {
        return "Year";
    }
}
