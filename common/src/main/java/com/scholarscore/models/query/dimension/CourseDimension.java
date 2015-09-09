package com.scholarscore.models.query.dimension;

import java.beans.Transient;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.Course;
import com.scholarscore.models.query.Dimension;

@SuppressWarnings("serial")
public class CourseDimension implements IDimension {
    public static final String ID = "ID";
    public static final String NAME = "Name";
    public static final Set<String> DIMENSION_FIELDS = 
            ImmutableSet.of(ID, NAME);
    public static final Set<Dimension> PARENT_DIMENSIONS = 
            ImmutableSet.of(Dimension.SCHOOL, Dimension.SUBJECT_AREA);
    
    @Override
    public Dimension getType() {
        return Dimension.COURSE;
    }

    @Override
    @JsonIgnore
    public Class<?> getAssociatedClass() {
        return Course.class;
    }

    @Override
    public Set<Dimension> getParentDimensions() {
        return CourseDimension.PARENT_DIMENSIONS;
    }

    @Override
    public Set<String> getFields() {
        return CourseDimension.DIMENSION_FIELDS;
    }

    @Override
    public String getName() {
        return "Course";
    }

}
