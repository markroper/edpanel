package com.scholarscore.models.query.dimension;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.query.Dimension;

@SuppressWarnings("serial")
public class SchoolYearDimension implements IDimension {
    public static final String ID = "ID";
    public static final String START_DATE = "Start Date";
    public static final String END_DATE = "End Date";
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
