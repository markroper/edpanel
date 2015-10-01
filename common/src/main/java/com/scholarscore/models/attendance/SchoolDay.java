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
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(id, date, school);
    }
}
