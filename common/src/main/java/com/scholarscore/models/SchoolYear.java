package com.scholarscore.models;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represents a school year, which may cross calendar year boundaries. 
 * Each instance of a school year for a school has a unique identifier within the 
 * containing school district.
 * 
 * @author markroper
 *
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchoolYear extends ApiModel implements Serializable, IApiModel<SchoolYear>{
    protected Date startDate;
    protected Date endDate;
    protected LinkedHashSet<Term> terms;
    
    public SchoolYear() {
        
    }
    
    public SchoolYear(SchoolYear year) {
        super(year);
        this.startDate = year.startDate;
        this.endDate = year.endDate;
        this.terms = year.terms;
    }
    
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public LinkedHashSet<Term> getTerms() {
        return terms;
    }

    public void setTerms(LinkedHashSet<Term> terms) {
        this.terms = terms;
    }

    @Override
    public void mergePropertiesIfNull(SchoolYear mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);  
        if(null ==  this.startDate) {
            this.startDate = mergeFrom.startDate;
        }
        if(null == this.endDate) {
            this.endDate = mergeFrom.endDate;
        }
        if(null == this.terms) {
            this.terms = mergeFrom.terms;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        final SchoolYear other = (SchoolYear) obj;
        return Objects.equals(this.startDate, other.startDate) && Objects.equals(this.endDate, other.endDate) && Objects.equals(this.terms, other.terms);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(startDate, endDate, terms);
    }
}
