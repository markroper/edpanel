package com.scholarscore.models.query;

import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("serial")
public class MeasureField extends QueryField implements Serializable {
    Measure measure;
    String field;
    
    public MeasureField() {
        super();
    }
    
    public MeasureField(Measure m, String s) {
        this.measure = m;
        this.field = s;
    }

    public Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
           return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final MeasureField other = (MeasureField) obj;
        return Objects.equals(this.measure, other.measure) 
                && Objects.equals(this.field, other.field);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() + 31 * Objects.hash(measure, field);
    }
}
