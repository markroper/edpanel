package com.scholarscore.models.attendance;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.IApiModel;
import com.scholarscore.models.School;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
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
    private Long sourceSystemOtherId;
    private School school;
    private LocalDate date;
    private Long cycleId;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = HibernateConsts.SCHOOL_DAY_ID)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = HibernateConsts.SCHOOL_DAY_DATE)
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
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

    @Column(name = HibernateConsts.SCHOOL_DAY_SOURCE_SYSTEM_OTHER_ID)
    public Long getSourceSystemOtherId() {
        return sourceSystemOtherId;
    }

    public void setSourceSystemOtherId(Long sourceSystemOtherId) {
        this.sourceSystemOtherId = sourceSystemOtherId;
    }

    //We need this to associate schoolIds with the Cycle day they occured on, but we dont have the notion of
    //This in EdPanel so we don't need to persist it yet
    @Transient
    public Long getCycleId() {
        return cycleId;
    }

    public void setCycleId(Long cycleId) {
        this.cycleId = cycleId;
    }

    public SchoolDay() { }
    
    public SchoolDay(SchoolDay schoolDay) { 
        this.id = schoolDay.id;
        this.school = schoolDay.school;
        this.date = schoolDay.date;
        this.sourceSystemId = schoolDay.sourceSystemId;
        this.sourceSystemOtherId = schoolDay.sourceSystemOtherId;
        this.cycleId = schoolDay.cycleId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, school, date, sourceSystemId, sourceSystemOtherId, cycleId);
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
                && Objects.equals(this.sourceSystemOtherId, other.sourceSystemOtherId)
                && Objects.equals(this.cycleId, other.cycleId)
                && Objects.equals(this.date, other.date);
    }

    @Override
    public String toString() {
        return "SchoolDay{" +
                "id=" + id +
                ", school=" + school +
                ", sourceSystemId=" + sourceSystemId +
                ", sourceSystemOtherId=" + sourceSystemOtherId +
                ", date=" + date +
                ", cycleId=" + cycleId +
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
        if (null == sourceSystemOtherId) {
            this.sourceSystemOtherId = mergeFrom.sourceSystemOtherId;
        }
        if (null == cycleId) {
            this.cycleId = mergeFrom.cycleId;
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
        private LocalDate date;
        private String sourceSystemId;
        private Long sourceSystemOtherId;
        private Long cycleId;

        protected SchoolDayBuilder me(){
            return this;
        }

        public SchoolDayBuilder withId(final Long id){
            this.id = id;
            return this;
        }

        public SchoolDayBuilder withSourceSystemId(final String id){
            this.sourceSystemId = id;
            return this;
        }

        public SchoolDayBuilder withSourceSystemOtherId(final Long id){
            this.sourceSystemOtherId = id;
            return this;
        }


        public SchoolDayBuilder withSchool(final School school){
            this.school = school;
            return this;
        }

        public SchoolDayBuilder withDate(final LocalDate date){
            this.date = date;
            return this;
        }
        public SchoolDayBuilder withycleId(final Long cycleId){
            this.cycleId =cycleId;
            return this;
        }

        public SchoolDay build(){
            SchoolDay schoolDay = new SchoolDay();
            schoolDay.setId(id);
            schoolDay.setSchool(school);
            schoolDay.setDate(date);
            schoolDay.setSourceSystemId(sourceSystemId);
            schoolDay.setSourceSystemOtherId(sourceSystemOtherId);
            schoolDay.setCycleId(cycleId);
            return schoolDay;
        }
    }
}
