package com.scholarscore.models.attendance;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.IApiModel;
import com.scholarscore.models.School;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Represents a day that school is in session for a single school
 * 
 * @author markroper
 *
 */
@Entity(name = HibernateConsts.SCHOOL_DAY_TABLE)
@Table(name = HibernateConsts.SCHOOL_DAY_TABLE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("serial")
public class SchoolDay implements Serializable, IApiModel<SchoolDay> {
    private Long id;
    private String sourceSystemId;
    private School school;
    private Date date;
    
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = HibernateConsts.SCHOOL_DAY_ID)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = HibernateConsts.SCHOOL_DAY_DATE)
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name=HibernateConsts.SCHOOL_FK)
    @Fetch(FetchMode.JOIN)
    public School getSchool() {
        return school;
    }
    public void setSchool(School school) {
        this.school = school;
    }

    @Column(name = HibernateConsts.SCHOOL_DAY_SOURCE_SYSTEM_ID)
    public String getSourceSystemId() {
        return sourceSystemId;
    }
    public void setSourceSystemId(String sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
    }

    public SchoolDay() { }
    
    public SchoolDay(SchoolDay schoolDay) { 
        this.id = schoolDay.id;
        this.school = schoolDay.school;
        this.date = schoolDay.date;
        this.sourceSystemId = schoolDay.sourceSystemId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, school, date, sourceSystemId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SchoolDay other = (SchoolDay) obj;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.school, other.school)
                && Objects.equals(this.sourceSystemId, other.sourceSystemId)
                && Objects.equals(this.date, other.date);
    }

    @Override
    public String toString() {
        return "SchoolDay{" +
                "id=" + id +
                ", school=" + school +
                ", sourceSystemId=" + sourceSystemId +
                ", date=" + date +
                '}';
    }

    @Override
    public void mergePropertiesIfNull(SchoolDay mergeFrom) {
        if (mergeFrom == null) { return; } 
        if (null == id) {
            this.id = mergeFrom.id;
        }
        if (null == school) {
            this.school = mergeFrom.school;
        }
        if (null == date) {
            this.date = mergeFrom.date;
        }
        if (null == sourceSystemId) {
            this.sourceSystemId = mergeFrom.sourceSystemId;
        }
    }


    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static class SchoolDayBuilder {
        private Long id;
        private School school;
        private Date date;

        protected SchoolDayBuilder me(){
            return this;
        }

        public SchoolDayBuilder withId(final Long id){
            this.id = id;
            return this;
        }

        public SchoolDayBuilder withSchool(final School school){
            this.school = school;
            return this;
        }

        public SchoolDayBuilder withDate(final Date date){
            this.date = date;
            return this;
        }

        public SchoolDay build(){
            SchoolDay schoolDay = new SchoolDay();
            schoolDay.setId(id);
            schoolDay.setSchool(school);
            schoolDay.setDate(date);
            return schoolDay;
        }
    }
}
