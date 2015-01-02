package com.scholarscore.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

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
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Course extends ApiModel implements Serializable, IApiModel<Course> {
    //TODO: Boolean honors
    //TODO: GPA weight
    
    public Course() {
        super();
    }
    
    public Course(Course clone) {
        super(clone);
    }

    @Override
    public void mergePropertiesIfNull(Course mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if(!super.equals(obj)) {
            return false;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode();
    }
}
