package com.scholarscore.models.query.dimension;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.gpa.CurrentGpa;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.measure.CurrentGpaMeasure;

import java.util.Set;

/**
 * User: jordan
 * Date: 2/16/16
 * Time: 5:15 PM
 */
public class CurrentGpaDimension extends BaseDimension implements IDimension {
    public static final Set<Dimension> PARENT_DIMENSIONS =
            ImmutableSet.of(Dimension.STUDENT, Dimension.GPA);

    @Override
    public Dimension getType() {
        return Dimension.CURRENT_GPA;
    }

    @Override
    public Class<?> getAssociatedClass() {
        return CurrentGpa.class;
    }

    @Override
    public Set<Dimension> getParentDimensions() {
        return PARENT_DIMENSIONS;
    }

    @Override
    public Set<String> getFields() {
        return CurrentGpaMeasure.FIELDS;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
