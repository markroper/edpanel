package com.scholarscore.models.query.dimension;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.Term;
import com.scholarscore.models.query.Dimension;

import java.util.Set;

@SuppressWarnings("serial")
public class TermDimension implements IDimension {
    public static final String ID = "ID";
    public static final String NAME = "Name";
    public static final String START_DATE = "Start Date";
    public static final String END_DATE = "End Date";
    public static final Set<String> DIMENSION_FIELDS = 
            ImmutableSet.of(ID, NAME, START_DATE, END_DATE);
    public static final Set<Dimension> PARENT_DIMENSIONS = 
            ImmutableSet.of(Dimension.YEAR);
    
    @Override
    public Dimension getType() {
        return Dimension.TERM;
    }

    @Override
    @JsonIgnore
    public Class<?> getAssociatedClass() {
        return Term.class;
    }

    @Override
    public Set<Dimension> getParentDimensions() {
        return TermDimension.PARENT_DIMENSIONS;
    }

    @Override
    public Set<String> getFields() {
        return TermDimension.DIMENSION_FIELDS;
    }

    @Override
    public String getName() {
        return "Term";
    }
}
