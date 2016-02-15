package com.scholarscore.models.dashboard;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by markroper on 2/15/16.
 */
public class ColumnDef implements Serializable {
    //The field to pluck from the query result, of the form 'values[1]', values[2], etc
    protected String field;
    //The name to display as the column header for this column e.g. 'name'
    protected String displayName;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, displayName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ColumnDef other = (ColumnDef) obj;
        return Objects.equals(this.field, other.field)
                && Objects.equals(this.displayName, other.displayName);
    }
}
