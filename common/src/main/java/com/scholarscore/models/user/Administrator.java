package com.scholarscore.models.user;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.Address;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.IStaff;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;

/**
 * Created by mattg on 7/19/15.
 */
@Entity(name = HibernateConsts.ADMIN_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
@PrimaryKeyJoinColumn(name=HibernateConsts.ADMIN_USER_FK, referencedColumnName = HibernateConsts.USER_ID)
public class Administrator extends Staff implements Serializable, IStaff<Administrator> {
    public static final DimensionField ID = new DimensionField(Dimension.ADMINISTRATOR, "ID");
    public static final DimensionField NAME = new DimensionField(Dimension.ADMINISTRATOR, "Name");
    public static final DimensionField EMAIL_ADDRESS = new DimensionField(Dimension.ADMINISTRATOR, "Address");
    public static final Set<DimensionField> DIMENSION_FIELDS = new HashSet<DimensionField>() {{
        add(ID);
        add(NAME);
        add(EMAIL_ADDRESS);
    }};
    
    public Administrator() {
    }

    public Administrator(Administrator admin) {
        super(admin);
    }

    @Override
    public void mergePropertiesIfNull(User mergeFrom) {
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
        return super.getHomeAddress();
    }

    @Column(name = HibernateConsts.ADMIN_HOME_PHONE)
    public String getHomePhone() {
        return super.getHomePhone();
    }

    @Column(name = HibernateConsts.ADMIN_SOURCE_SYSTEM_ID)
    public String getSourceSystemId() {
        return super.getSourceSystemId();
    }

    @Column(name = HibernateConsts.ADMIN_USER_FK, insertable = false, updatable = false)
    public Long getUserId() {
        return super.getUserId();
    }
    
    @Override
    @Transient
    public UserType getType() {
        return UserType.ADMINISTRATOR;
    }

    @Override
    public String toString() {
        return"Administrator{" + "(super):{" + super.toString() + "} ";
    }
}
