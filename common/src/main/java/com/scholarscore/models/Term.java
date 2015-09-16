package com.scholarscore.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

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
@Entity(name = HibernateConsts.TERM_TABLE)
@Table(name = HibernateConsts.TERM_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Term extends ApiModel implements Serializable, IApiModel<Term>{
    protected Date startDate;
    protected Date endDate;
    protected SchoolYear schoolYear;

    public Term() {
        
    }
    
    public Term(Term year) {
        super(year);
        this.schoolYear = year.schoolYear;
        this.startDate = year.startDate;
        this.endDate = year.endDate;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = HibernateConsts.TERM_ID)
    public Long getId() {
        return super.getId();
    }

    @Override
    @Column(name = HibernateConsts.TERM_NAME)
    public String getName() {
        return super.getName();
    }

    @Column(name = HibernateConsts.TERM_START_DATE)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name = HibernateConsts.TERM_END_DATE)
    public Date getEndDate() {
        return endDate;
    }

    @OneToOne(optional = true)
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name=HibernateConsts.SCHOOL_YEAR_FK)
    @Fetch(FetchMode.JOIN)
    public SchoolYear getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(SchoolYear schoolYear) {
        this.schoolYear = schoolYear;
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
        if (null == this.schoolYear) {
            this.schoolYear = mergeFrom.schoolYear;
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
