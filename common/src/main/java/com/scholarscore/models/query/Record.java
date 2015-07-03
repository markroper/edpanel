package com.scholarscore.models.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("serial")
public class Record implements Serializable {
    List<Object> values;
    
    public Record() {   
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }
    
    public void addValue(Object fieldValue) {
        if(null == values) {
            values = new ArrayList<Object>();
        }
        values.add(fieldValue);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(values);
    }

    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        final Record other = (Record) obj;
        return Objects.equals(this.values, other.values);
    }
    
    
}
