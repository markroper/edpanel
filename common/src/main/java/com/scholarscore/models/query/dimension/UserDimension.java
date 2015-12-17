package com.scholarscore.models.query.dimension;

import com.google.common.collect.ImmutableSet;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.user.User;

import java.util.Set;

/**
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
