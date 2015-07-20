package com.scholarscore.models.query.dimension;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.Section;
import com.scholarscore.models.query.Dimension;

@SuppressWarnings("serial")
public class SectionDimension implements IDimension {
    public static final String ID = "ID";
    public static final String NAME = "Name";
    public static final String START_DATE = "Start Date";
    public static final String END_DATE = "End Date";
    public static final String TEACHER = "Teacher";
    public static final String GRADE_FORMULA = "Grade Formula";
    public static final String ROOM = "Room";
    public static final Set<String> DIMENSION_FIELDS = 
            ImmutableSet.of(ID, NAME, START_DATE, END_DATE, TEACHER, 
                    GRADE_FORMULA, ROOM);
    public static final Set<Dimension> PARENT_DIMENSIONS = 
            ImmutableSet.of(Dimension.COURSE, Dimension.TERM);
    
    @Override
    public Dimension getType() {
        return Dimension.YEAR;
    }

    @Override
    @JsonIgnore
    public Class<?> getAssociatedClass() {
        return Section.class;
    }

    @Override
    public Set<Dimension> getParentDimensions() {
        return SectionDimension.PARENT_DIMENSIONS;
    }

    @Override
    public Set<String> getFields() {
        return SectionDimension.DIMENSION_FIELDS;
    }

    @Override
    public String getName() {
        return "Section";
    }
}
