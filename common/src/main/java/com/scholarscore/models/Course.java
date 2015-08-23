package com.scholarscore.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
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
@Entity(name = "course")
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

    @Column(name = "course_number")
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Column(name = "course_source_system_id")
    public String getSourceSystemId() {
        return sourceSystemId;
    }

    public void setSourceSystemId(String sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "course_id")
    public Long getId() {
        return super.getId();
    }

    @Override
    @Column(name = "course_name")
    public String getName() {
        return super.getName();
    }

}
