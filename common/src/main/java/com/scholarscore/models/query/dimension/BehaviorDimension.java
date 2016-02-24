package com.scholarscore.models.query.dimension;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.query.Dimension;

import java.util.Set;

/**
 * User: jordan
 * Date: 2/16/16
 * Time: 5:15 PM
 */
public class BehaviorDimension implements IDimension {

    public static final Set<Dimension> PARENT_DIMENSIONS =
            ImmutableSet.of(Dimension.STUDENT, Dimension.USER);

    @Override
    public Dimension getType() {
        return Dimension.ATTENDANCE;
    }

    @Override
    public Class<?> getAssociatedClass() {
        return Attendance.class;
    }

    @Override
    public Set<Dimension> getParentDimensions() {
        return PARENT_DIMENSIONS;
    }

    @Override
    public Set<String> getFields() {
        return null;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
