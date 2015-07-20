package com.scholarscore.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Teacher extends ApiModel implements Serializable, IApiModel<Teacher>{
    
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
