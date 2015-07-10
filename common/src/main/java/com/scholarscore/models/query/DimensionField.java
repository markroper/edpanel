package com.scholarscore.models.query;

import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("serial")
public class DimensionField implements Serializable {
    Dimension dimension;
    String field;
    
    public DimensionField() {
        
    }
    
    public DimensionField(Dimension dim, String field) {
        this.dimension = dim;
        this.field = field;
    }
    
    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final DimensionField other = (DimensionField) obj;
        return Objects.equals(this.dimension, other.dimension) 
                && Objects.equals(this.field, other.field);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(dimension, field);
    }
}