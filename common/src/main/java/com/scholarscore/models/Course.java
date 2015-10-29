package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.Size;
import java.io.Serializable;

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

    @Size(min = 0, max=255)
    private String number;

    @Size(min = 0, max=255)
    private String sourceSystemId;

    private School school;

    public Course() {
        super();
    }
    
    public Course(Course clone) {
        super(clone);
        this.sourceSystemId = clone.sourceSystemId;
        this.number = clone.number;
        this.school = clone.school;
    }

    @Override
    public void mergePropertiesIfNull(Course mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);

        if (null == number) {
            this.number = mergeFrom.getNumber();
        }
        if (null == sourceSystemId) {
            this.sourceSystemId = mergeFrom.getSourceSystemId();
        }
        if (null == school) {
            this.school = mergeFrom.getSchool();
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
    @JoinColumn(name=HibernateConsts.SCHOOL_FK)
    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Course course = (Course) o;

        if (number != null ? !number.equals(course.number) : course.number != null) { return false; }
        if (school != null ? !school.equals(course.school) : course.school != null) { return false; }
        if (sourceSystemId != null ? !sourceSystemId.equals(course.sourceSystemId) : course.sourceSystemId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (sourceSystemId != null ? sourceSystemId.hashCode() : 0);
        result = 31 * result + (school != null ? school.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Course{" + " (super: " + super.toString() + ")" +
                "number='" + number + '\'' +
                ", sourceSystemId='" + sourceSystemId + '\'' +
                ", school=" + school +
                '}';
    }

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static class CourseBuilder extends ApiModelBuilder<CourseBuilder, Course> {

        private String number;
        private String sourceSystemId;
        private School school;

        public CourseBuilder withNumber(final String number){
            this.number = number;
            return this;
        }

        public CourseBuilder withSourceSystemId(final String sourceSystemId){
            this.sourceSystemId = sourceSystemId;
            return this;
        }

        public CourseBuilder withSchool(final School school){
            this.school = school;
            return this;
        }

        public Course build(){
            Course course = super.build();
            course.setNumber(number);
            course.setSourceSystemId(sourceSystemId);
            course.setSchool(school);
            return course;
        }

        @Override
        protected CourseBuilder me() {
            return this;
        }

        @Override
        public Course getInstance() {
            return new Course();
        }
    }
}
