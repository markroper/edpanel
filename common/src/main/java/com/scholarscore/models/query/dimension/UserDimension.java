package com.scholarscore.models.query.dimension;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.user.User;

import java.util.Set;

/**
 * This dimension should be used if you are trying to get
 * information about the user_id field on and object that can belong to
 * either a teacher, admin or student, at time of writing this is only
 * done on behavior events in the query generator
 * Created by cwallace on 12/17/15.
 */
public class UserDimension implements IDimension {
    public static final String ID = "ID";
    public static final Set<String> DIMENSION_FIELDS =
            ImmutableSet.of(ID);
    public static final Set<Dimension> PARENT_DIMENSIONS =
            ImmutableSet.of();

    @Override
    public Dimension getType() {
        return Dimension.USER;
    }

    @Override
    public Class<?> getAssociatedClass() {
        return User.class;
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
        return "User";
    }
}
