package com.scholarscore.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * This class represents a grade level. For example, the sixth grade.
 * Each instance of a grade level has unique ID within the school district.
 * 
 * For example, the sixth grade for the 2011-2012 school year at North Central 
 * High School has a unique ID that is not repeated in the containing school district.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GradeLevel 
        extends ApiModel implements Serializable, IApiModel<GradeLevel>{
    public GradeLevel() {
        
    }
    
    public GradeLevel(GradeLevel year) {
        super(year);
    }
    
    @Override
    public void mergePropertiesIfNull(GradeLevel mergeFrom) {
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
