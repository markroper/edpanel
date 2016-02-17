package com.scholarscore.models.query.dimension;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.attendance.SchoolDay;
import com.scholarscore.models.grade.StudentSectionGrade;
import com.scholarscore.models.query.Dimension;

import java.util.Set;

/**
 * User: jordan
 * Date: 2/16/16
 * Time: 5:15 PM
 */
public class SchoolDayPseudoDimension implements IDimension {

    public static final Set<Dimension> PARENT_DIMENSIONS =
            ImmutableSet.of(Dimension.SCHOOL);

    @Override
    public Dimension getType() {
        return Dimension.SCHOOL_DAY;
    }

    @Override
    public Class<?> getAssociatedClass() {
        return SchoolDay.class;
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
        return "SchoolDayPseudoDimension";
    }
}
