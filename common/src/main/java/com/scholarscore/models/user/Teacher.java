package com.scholarscore.models.user;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.Address;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.IStaff;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;

@Entity(name = HibernateConsts.TEACHER_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
@PrimaryKeyJoinColumn(name=HibernateConsts.TEACHER_USER_FK, referencedColumnName = HibernateConsts.USER_ID)
public class Teacher extends Person implements Serializable, IStaff<Teacher> {
    public Teacher() {
    }
    
    public Teacher(Teacher t) {
        super(t);
    }  
    
    @Column(name = HibernateConsts.TEACHER_NAME)
    public String getName() {
        return super.getName();
    }

    @OneToOne(optional = true)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name=HibernateConsts.TEACHER_ADDRESS_FK)
    public Address getHomeAddress() {
        return homeAddress;
    }

    @Column(name = HibernateConsts.TEACHER_HOME_PHONE)
    public String getHomePhone() {
        return homePhone;
    }

    @Column(name = HibernateConsts.TEACHER_SOURCE_SYSTEM_ID)
    public String getSourceSystemId() {
        return sourceSystemId;
    }
    
    @Column(name = HibernateConsts.TEACHER_USER_FK, insertable = false, updatable = false)
    public Long getUserId() {
        return getId();
    }
    
    @Column(name = HibernateConsts.SCHOOL_FK, nullable = true)
    public Long getCurrentSchoolId() {
        return currentSchoolId;
    }
    
    @Override
    @Transient
    public UserType getType() {
        return UserType.TEACHER;
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
