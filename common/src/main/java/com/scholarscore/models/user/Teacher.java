package com.scholarscore.models.user;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Teacher extends Staff implements Serializable{
    public Teacher() {
        this.setStaffRole(StaffRole.TEACHER);
        this.userType = UserType.TEACHER;
    }
    
    public Teacher(Staff t) {
        super(t);
        setStaffRole(StaffRole.TEACHER);
        this.userType = UserType.TEACHER;
    }  



    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static class TeacherBuilder extends PersonBuilder<TeacherBuilder, Teacher> {

        public Teacher build(){
            return super.build();
        }

        @Override
        protected TeacherBuilder me() {
            return this;
        }

        public Teacher getInstance(){
            return new Teacher();
        }
    }
    
    
}
