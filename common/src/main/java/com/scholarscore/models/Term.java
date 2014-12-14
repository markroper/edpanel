package com.scholarscore.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * A term represents one segment of an {@link com.scholarscore.models.SchoolYear}.
 * Some schools use semesters, others trimesters, and others quarters.  One term would be 
 * one such period.
 * 
 * Each instance of a term within a year within a school has a unqiue ID within a district.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Term 
        extends ApiModel implements Serializable, IApiModel<Term>{
    public Term() {
        
    }
    
    public Term(Term year) {
        super(year);
    }
    
    @Override
    public void mergePropertiesIfNull(Term mergeFrom) {
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
