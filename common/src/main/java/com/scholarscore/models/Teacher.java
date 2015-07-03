package com.scholarscore.models;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;

@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Teacher extends ApiModel implements Serializable, IApiModel<Teacher>{
    public static final DimensionField ID = new DimensionField(Dimension.TEACHER, "ID");
    public static final DimensionField NAME = new DimensionField(Dimension.TEACHER, "Name");
    public static final DimensionField EMAIL_ADDRESS = new DimensionField(Dimension.TEACHER, "Address");
    public static final Set<DimensionField> DIMENSION_FIELDS = new HashSet<DimensionField>() {{
        add(ID);
        add(NAME);
        add(EMAIL_ADDRESS);
    }};
    
    public Teacher() {
        
    }
    
    public Teacher(Teacher t) {
        super(t);
    }
    
    // FK to the Users table entry
    @JsonIgnore
    private String username;
    
    @JsonInclude
    private transient User login;
    
    @Override
    public void mergePropertiesIfNull(Teacher mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode();
    }
}
