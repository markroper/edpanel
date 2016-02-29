package com.scholarscore.models.query.measure;

import com.scholarscore.models.query.dimension.IDimension;

import java.util.Objects;

/**
 * User: jordan
 * Date: 2/29/16
 * Time: 12:10 PM
 */
public abstract class BaseMeasure implements IMeasure {

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass() || !(obj instanceof IMeasure)) {
            return false;
        }
        final IMeasure other = (IMeasure) obj;
        return Objects.equals(this.getCompatibleDimensions(), other.getCompatibleDimensions())
                && Objects.equals(this.getCompatibleMeasures(), other.getCompatibleMeasures())
                && Objects.equals(this.getMeasure(), other.getMeasure())
                && Objects.equals(this.getFields(), other.getFields())
                && Objects.equals(this.getName(), other.getName());
    }

    @Override
    public int hashCode() {
        return 31 *
                Objects.hash(this.getCompatibleDimensions(), this.getCompatibleMeasures(), this.getMeasure(), this.getFields(), this.getName());
    }

}
