package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class Administrator extends Identity implements Serializable, IStaff<Administrator> {
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
        this.setUser(admin.getUser());
        this.setSourceSystemId(admin.getSourceSystemId());
        this.setName(admin.getName());
        this.setHomeAddress(admin.getHomeAddress());
        this.setHomePhone(admin.getHomePhone());
    }

    private String sourceSystemId;
    private Address homeAddress;
    private String homePhone;

    @Override
    public void mergePropertiesIfNull(Administrator mergeFrom) {
        // MJG: do we merge address properties if null too?
        if (null == this.getHomeAddress()) {
            this.setHomeAddress(mergeFrom.getHomeAddress());
        }
        if (null == this.getHomePhone()) {
            this.setHomePhone(mergeFrom.getHomePhone());
        }
        if (null == this.getSourceSystemId()) {
            this.setSourceSystemId(mergeFrom.getSourceSystemId());
        }
        super.mergePropertiesIfNull(mergeFrom);
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = HibernateConsts.ADMIN_ID)
    public Long getId() {
        return super.getId();
    }

    @Override
    @Column(name = HibernateConsts.ADMIN_NAME)
    public String getName() {
        return super.getName();
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode()
                + Objects.hash(homeAddress, homePhone, sourceSystemId);
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

    @Override
    @OneToOne(optional = true)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name=HibernateConsts.ADMIN_USER_FK)
    public User getUser() {
        return super.getUser();
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

    public void setUser(User user) { super.setUser(user); }

    @Override
    public String toString() {
        return"Administrator{" + "(super):{" + super.toString() + "} " +
                "sourceSystemId='" + sourceSystemId + '\'' +
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
        if (homePhone != null ? !homePhone.equals(that.homePhone) : that.homePhone != null) return false;
        if (sourceSystemId != null ? !sourceSystemId.equals(that.sourceSystemId) : that.sourceSystemId != null)
            return false;

        return true;
    }
}
