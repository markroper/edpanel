package com.scholarscore.models.attendance;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.School;

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
public class SchoolDay implements Serializable {
    private Long id;
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

    @Override
    public int hashCode() {
        return Objects.hash(id, school, date);
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
                && Objects.equals(this.date, other.date);
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
