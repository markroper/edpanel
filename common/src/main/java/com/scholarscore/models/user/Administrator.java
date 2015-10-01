package com.scholarscore.models.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.Address;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.IStaff;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;

import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;
import javax.persistence.Entity;

/**
 * Created by mattg on 7/19/15.
 */
@Entity(name = HibernateConsts.ADMIN_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
@PrimaryKeyJoinColumn(name=HibernateConsts.ADMIN_USER_FK, referencedColumnName = HibernateConsts.USER_ID)
public class Administrator extends User implements Serializable, IStaff<Administrator> {
    public static final DimensionField ID = new DimensionField(Dimension.ADMINISTRATOR, "ID");
    public static final DimensionField NAME = new DimensionField(Dimension.ADMINISTRATOR, "Name");
    public static final DimensionField EMAIL_ADDRESS = new DimensionField(Dimension.ADMINISTRATOR, "Address");
    public static final Set<DimensionField> DIMENSION_FIELDS = new HashSet<DimensionField>() {{
        add(ID);
        add(NAME);
        add(EMAIL_ADDRESS);
    }};
    
    private String sourceSystemId;
    private Address homeAddress;
    private String homePhone;
    private Long userId;
    
    public Administrator() {
    }

    public Administrator(Administrator admin) {
        super(admin);
        this.setUserId(admin.getUserId());
        this.setSourceSystemId(admin.getSourceSystemId());
        this.setName(admin.getName());
        this.setHomeAddress(admin.getHomeAddress());
        this.setHomePhone(admin.getHomePhone());
    }

    @Override
    public void mergePropertiesIfNull(User mergeFrom) {
        // MJG: do we merge address properties if null too?
        if(mergeFrom instanceof Administrator) {
            Administrator from = (Administrator) mergeFrom;
            if(null == this.getUserId()) {
                this.setUserId(from.getUserId());
            }
            if (null == this.getHomeAddress()) {
                this.setHomeAddress(from.getHomeAddress());
            }
            if (null == this.getHomePhone()) {
                this.setHomePhone(from.getHomePhone());
            }
            if (null == this.getSourceSystemId()) {
                this.setSourceSystemId(from.getSourceSystemId());
            }
        }
        super.mergePropertiesIfNull(mergeFrom);
    }

    @Override
    @Column(name = HibernateConsts.ADMIN_NAME)
    public String getName() {
        return super.getName();
    }

    @OneToOne(optional = true)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name= HibernateConsts.ADMIN_ADDRESS_FK)
    public Address getHomeAddress() {
        return homeAddress;
    }

    @Column(name = HibernateConsts.ADMIN_HOME_PHONE)
    public String getHomePhone() {
        return homePhone;
    }

    @Column(name = HibernateConsts.ADMIN_SOURCE_SYSTEM_ID)
    public String getSourceSystemId() {
        return sourceSystemId;
    }

    @Column(name = HibernateConsts.ADMIN_USER_FK, insertable = false, updatable = false)
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }
    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public void setSourceSystemId(String sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
    }
    
    @Override
    @Transient
    public UserType getType() {
        return UserType.ADMINISTRATOR;
    }

    @Override
    public String toString() {
        return"Administrator{" + "(super):{" + super.toString() + "} " +
                "sourceSystemId='" + sourceSystemId + '\'' +
                ", userId=" + userId +
                ", homeAddress=" + homeAddress +
                ", homePhone='" + homePhone + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Administrator that = (Administrator) o;

        if (homeAddress != null ? !homeAddress.equals(that.homeAddress) : that.homeAddress != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (homePhone != null ? !homePhone.equals(that.homePhone) : that.homePhone != null) return false;
        if (sourceSystemId != null ? !sourceSystemId.equals(that.sourceSystemId) : that.sourceSystemId != null)
            return false;

        return true;
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode()
                + Objects.hash(homeAddress, homePhone, sourceSystemId, userId);
    }
}
