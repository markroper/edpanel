package com.scholarscore.models;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * The class represents a single school within a school district.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class School extends ApiModel implements Serializable, IApiModel<School>{
    public School() {
        super();
    }
    
    public School(School clone) {
        super(clone);
    }
    
    @Override
    public void mergePropertiesIfNull(School mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if(!super.equals(obj)) {
            return false;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode();
    }

}
