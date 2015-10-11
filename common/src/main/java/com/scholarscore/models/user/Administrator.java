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
        return getId();
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

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public class AdministratorBuilder extends StaffBuilder<Administrator>{

        public Administrator build(){
            return super.build();
        }

        public Administrator getInstance(){
            return new Administrator();
        }
    }
}
