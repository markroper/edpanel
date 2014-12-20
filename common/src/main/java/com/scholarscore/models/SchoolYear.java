package com.scholarscore.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
        return Objects.equals(this.startDate, other.startDate) && Objects.equals(this.endDate, other.endDate) && termsEqual(this.terms, other.terms);
    }
    
    private boolean termsEqual(LinkedHashSet<Term> terms1, LinkedHashSet<Term> terms2) {
        if(null == terms1 && null == terms2) {
            return true;
        }
        if((null == terms1 && null != terms2) || (null != terms1 && null ==terms2)) {
            return false;
        }
        if(terms1.size() != terms2.size()) {
            return false;
        }
        Iterator<Term> it1 = terms1.iterator();
        Iterator<Term> it2 = terms2.iterator();
        while(it1.hasNext()) {
            if(!it1.next().equals(it2.next())) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(startDate, endDate, terms);
    }
}
