package com.scholarscore.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.Size;

/**
 * The class represents a course, for example 'AP Calculus BC'. The course
 * may be taught in many different schools, in many different years and 
 * terms.  A course may have different assignments and teachers and grading 
 * from year to year, but there is still one instance with a district, unique
 * ID for each course.
 * 
 * The instance of a course thats being taught at a particular time in a particular school
 * is represented by the {@link com.scholarscore.models.Section} class.
 * 
 * @author markroper
 *
 */
@Entity(name = HibernateConsts.COURSE_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Course extends ApiModel implements Serializable, IApiModel<Course> {
    public Course() {
        super();
    }
    
    public Course(Course clone) {
        super(clone);
        this.sourceSystemId = clone.sourceSystemId;
        this.number = clone.number;
    }

    @Size(min = 0, max=255)
    private String number;

    @Size(min = 0, max=255)
    private String sourceSystemId;

    private School school;

    @Override
    public void mergePropertiesIfNull(Course mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);

        if (null == number) {
            this.number = mergeFrom.getNumber();
        }
        if (null == sourceSystemId) {
            this.sourceSystemId = mergeFrom.getSourceSystemId();
        }
    }

    @Column(name = HibernateConsts.COURSE_NUMBER)
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Column(name = HibernateConsts.COURSE_SOURCE_SYSTEM_ID)
    public String getSourceSystemId() {
        return sourceSystemId;
    }

    public void setSourceSystemId(String sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = HibernateConsts.COURSE_ID)
    public Long getId() {
        return super.getId();
    }

    @Override
    @Column(name = HibernateConsts.COURSE_NAME)
    public String getName() {
        return super.getName();
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
}
