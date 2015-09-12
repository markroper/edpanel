package com.scholarscore.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Represents a school year, which may cross calendar year boundaries. 
 * Each instance of a school year for a school has a unique identifier within the 
 * containing school district.
 * 
 * @author markroper
 *
 */
@Entity
@Table(name = HibernateConsts.SCHOOL_YEAR_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchoolYear extends ApiModel implements Serializable, IApiModel<SchoolYear>{
    protected Date startDate;
    protected Date endDate;
    protected List<Term> terms;
    protected School school;
    
    public SchoolYear() {
        
    }
    
    public SchoolYear(SchoolYear year) {
        super(year);
        this.startDate = year.startDate;
        this.endDate = year.endDate;
        this.terms = year.terms;
    }
    
    public SchoolYear(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @OneToOne(optional = true)
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name=HibernateConsts.SCHOOL_FK)
    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = HibernateConsts.SCHOOL_YEAR_ID)
    public Long getId() {
        return super.getId();
    }

    @Override
    @Column(name = HibernateConsts.SCHOOL_YEAR_NAME)
    public String getName() {
        return super.getName();
    }

    @Column(name = HibernateConsts.SCHOOL_YEAR_START_DATE)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name = HibernateConsts.SCHOOL_YEAR_END_DATE)
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Transient
    public List<Term> getTerms() {
        return terms;
    }

    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }

    public Term findTermById(Long id) {
        Term termWithTermId = null;
        if(null != terms) {
            for(Term t : terms) {
                if(t.getId().equals(id)) {
                    termWithTermId = t;
                    break;
                }
            }
        }
        return termWithTermId;
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
        return Objects.equals(this.startDate, other.startDate) 
                && Objects.equals(this.endDate, other.endDate) 
                && Objects.equals(this.terms, other.terms);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(startDate, endDate, terms);
    }
}
