package com.scholarscore.models;

import java.io.Serializable;
import java.util.Map;
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
    Map<Long, SchoolYear> years;
    
    public School() {
        super();
    }
    
    public School(School clone) {
        super(clone);
        this.years = clone.years;
    }
    
    public Map<Long, SchoolYear> getYears() {
        return years;
    }

    public void setYears(Map<Long, SchoolYear> years) {
        this.years = years;
    }

    @Override
    public void mergePropertiesIfNull(School mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if(null == years) {
            this.years = mergeFrom.years;
        }
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
        final School other = (School) obj;
        return Objects.equals(this.years, other.years);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(years);
    }

}
