package com.scholarscore.models.query.dimension;

import java.util.Objects;

/**
 * User: jordan
 * Date: 2/29/16
 * Time: 11:06 AM
 */
public abstract class BaseDimension implements IDimension {
    public static final String ID = "ID";
    public static final String NAME = "Name";
    public static final String START_DATE = "Start Date";
    public static final String END_DATE = "End Date";

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass() || !(obj instanceof IDimension)) {
            return false;
        }
        final IDimension other = (IDimension) obj;
        return Objects.equals(this.getType(), other.getType())
                && Objects.equals(this.getAssociatedClass(), other.getAssociatedClass())
                && Objects.equals(this.getParentDimensions(), other.getParentDimensions())
                && Objects.equals(this.getFields(), other.getFields())
                && Objects.equals(this.getName(), other.getName());
    }

    @Override
    public int hashCode() {
        return 31 *
                 Objects.hash(this.getType(), this.getAssociatedClass(), this.getParentDimensions(), this.getFields(), this.getName());
    }

}
