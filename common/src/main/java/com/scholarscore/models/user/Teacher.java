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
public class Teacher extends Staff implements Serializable, IStaff<Teacher> {
    public Teacher() {
    }
    
    public Teacher(Teacher t) {
        super(t);
    }

    @Override
    public void mergePropertiesIfNull(User mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
    }    
    
    @Column(name = HibernateConsts.TEACHER_NAME)
    public String getName() {
        return super.getName();
    }

    @OneToOne(optional = true)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name=HibernateConsts.TEACHER_ADDRESS_FK)
    public Address getHomeAddress() {
        return super.getHomeAddress();
    }

    @Column(name = HibernateConsts.TEACHER_HOME_PHONE)
    public String getHomePhone() {
        return super.getHomePhone();
    }

    @Column(name = HibernateConsts.TEACHER_SOURCE_SYSTEM_ID)
    public String getSourceSystemId() {
        return super.getSourceSystemId();
    }
    
    @Column(name = HibernateConsts.TEACHER_USER_FK, insertable = false, updatable = false)
    public Long getUserId() {
        return super.getUserId();
    }
    
    @Override
    @Transient
    public UserType getType() {
        return UserType.TEACHER;
    }
}
