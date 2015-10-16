package com.scholarscore.models;

import java.io.Serializable;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
    protected Set<Term> terms;
    protected School school;
    
    public SchoolYear() {
        super();
        terms = Sets.newHashSet();
    }
    
    public SchoolYear(SchoolYear year) {
        super(year);
        terms = Sets.newHashSet();
        this.startDate = year.startDate;
        this.endDate = year.endDate;
        this.terms = year.terms;
    }
    
    public SchoolYear(Date startDate, Date endDate) {
        this();
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @OneToOne(optional = true)
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name=HibernateConsts.SCHOOL_FK)
    @Fetch(FetchMode.JOIN)
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
    public Set<Term> getTerms() {
        return terms;
    }

    public void setTerms(Set<Term> terms) {
        this.terms = terms;
    }

    public void addTerm(final Term term){
        terms.add(term);
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

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static class SchoolYearBuilder extends ApiModelBuilder<SchoolYearBuilder, SchoolYear> {

        protected Date startDate;
        protected Date endDate;
        protected Set<Term> terms;
        protected School school;

        public SchoolYearBuilder(){
            terms = Sets.newHashSet();
        }

        public SchoolYearBuilder withStartDate(final Date startDate){
            this.startDate = startDate;
            return this;
        }

        public SchoolYearBuilder withEndDate(final Date endDate){
            this.endDate = endDate;
            return this;
        }

        public SchoolYearBuilder withTerm(final Term term){
            terms.add(term);
            return this;
        }

        public SchoolYearBuilder withTerms(final List<Term> terms){
            this.terms.addAll(terms);
            return this;
        }

        public SchoolYearBuilder withSchool(final School school){
            this.school = school;
            return this;
        }

        public SchoolYear build(){
            SchoolYear schoolYear = super.build();
            schoolYear.setStartDate(startDate);
            schoolYear.setEndDate(endDate);
            schoolYear.setTerms(terms);
            //make sure this is reciprocal
            schoolYear.setSchool(school);
            school.addYear(schoolYear);
            return schoolYear;
        }

        @Override
        protected SchoolYearBuilder me() {
            return this;
        }

        @Override
        public SchoolYear getInstance() {
            return new SchoolYear();
        }
    }
}
