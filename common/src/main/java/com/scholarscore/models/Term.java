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
        this.schoolYear.addTerm(this);
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
        return Objects.equals(this.startDate, other.startDate) 
                && Objects.equals(this.endDate, other.endDate)
                && Objects.equals(this.schoolYear, other.schoolYear);
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(startDate, endDate, schoolYear);
    }

    @Override
    public String toString() {
        return "Term{" + "(super:{" + super.toString() + "})" + 
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", schoolYear=" + schoolYear +
                '}';
    }

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static class TermBuilder extends ApiModelBuilder<TermBuilder, Term>{
        protected Date startDate;
        protected Date endDate;
        protected SchoolYear schoolYear;

        public TermBuilder withStartDate(final Date startDate){
            this.startDate = startDate;
            return this;
        }

        public TermBuilder withEndDate(final Date endDate){
            this.endDate = endDate;
            return this;
        }

        public TermBuilder withSchoolYear(final SchoolYear schoolYear){
            this.schoolYear = schoolYear;
            return this;
        }

        public Term build(){
            Term term = super.build();
            term.setStartDate(startDate);
            term.setEndDate(endDate);
            //make this reciprocal
            term.setSchoolYear(schoolYear);
            schoolYear.addTerm(term);
            return term;
        }

        @Override
        protected TermBuilder me() {
            return this;
        }

        @Override
        public Term getInstance() {
            return null;
        }
    }
}
