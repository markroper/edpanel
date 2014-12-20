package com.scholarscore.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

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
public class Term extends ApiModel implements Serializable, IApiModel<Term>{
    protected Date startDate;
    protected Date endDate;
    
    public Term() {
        
    }
    
    public Term(Term year) {
        super(year);
        this.startDate = year.startDate;
        this.endDate = year.endDate;
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

    @Override
    public void mergePropertiesIfNull(Term mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);     
        if(null ==  this.startDate) {
            this.startDate = mergeFrom.startDate;
        }
        if(null == this.endDate) {
            this.endDate = mergeFrom.endDate;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        final Term other = (Term) obj;
        return Objects.equals(this.startDate, other.startDate) && Objects.equals(this.endDate, other.endDate);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(startDate, endDate);
    }

}
