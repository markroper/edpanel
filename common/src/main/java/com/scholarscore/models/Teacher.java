package com.scholarscore.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Teacher extends ApiModel implements Serializable, IApiModel<Teacher>{
    public Teacher() {
        
    }
    
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
