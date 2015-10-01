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
public class Teacher extends User implements Serializable, IStaff<Teacher> {
    private String sourceSystemId;
    private Address homeAddress;
    private String homePhone;
    private Long userId;
    
    public Teacher() {
    }
    
    public Teacher(Teacher t) {
        super(t);
        this.setUserId(t.getUserId());
        this.setSourceSystemId(t.getSourceSystemId());
        this.setHomeAddress(t.getHomeAddress());
        this.setHomePhone(t.getHomePhone());
    }

    @Override
    public void mergePropertiesIfNull(User mergeFrom) {
        if(mergeFrom instanceof Teacher) {
            Teacher merge = (Teacher) mergeFrom;
            if (null == this.getUserId()) {
                this.setUserId(merge.getUserId());
            }
            if (null == this.getHomeAddress()) {
                this.setHomeAddress(merge.getHomeAddress());
            }
            if (null == this.getHomePhone()) {
                this.setHomePhone(merge.getHomePhone());
            }
            if (null == this.getSourceSystemId()) {
                this.setSourceSystemId(merge.getSourceSystemId());
            }
        }
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

    @Override
    public void setSourceSystemId(String sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }
    
    @Override
    @Transient
    public UserType getType() {
        return UserType.TEACHER;
    }
    
    @Column(name = HibernateConsts.TEACHER_USER_FK, insertable = false, updatable = false)
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Teacher)) { return false; }
        if (!super.equals(o)) { return false; }
        Teacher teacher = (Teacher) o;
        if (getSourceSystemId() != null ? !getSourceSystemId().equals(teacher.getSourceSystemId()) : teacher.getSourceSystemId() != null) {
            return false;
        }
        if (getHomeAddress() != null ? !getHomeAddress().equals(teacher.getHomeAddress()) : teacher.getHomeAddress() != null) {
            return false;
        }
        if (getUserId() != null ? !getUserId().equals(teacher.getUserId()) : teacher.getUserId() != null) {
            return false;
        }
        return !(getHomePhone() != null ? !getHomePhone().equals(teacher.getHomePhone()) : teacher.getHomePhone() != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getSourceSystemId() != null ? getSourceSystemId().hashCode() : 0);
        result = 31 * result + (getHomeAddress() != null ? getHomeAddress().hashCode() : 0);
        result = 31 * result + (getHomePhone() != null ? getHomePhone().hashCode() : 0);
        result = 31 * result + (getUserId() != null ? getUserId().hashCode() : 0);
        return result;
    }
}
