package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.persistence.Entity;

/**
 * Created by mattg on 7/19/15.
 */
@Entity(name = HibernateConsts.ADMIN_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Administrator extends ApiModel implements Serializable, IStaff<Administrator> {
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
        this.setUsername(admin.getUsername());
        this.setLogin(admin.getLogin());
        this.setSourceSystemId(admin.getSourceSystemId());
        this.setName(admin.getName());
        this.setHomeAddress(admin.getHomeAddress());
        this.setHomePhone(admin.getHomePhone());
    }

    // FK to the Users table entry
    @JsonIgnore
    private String username;

    @JsonInclude
    private transient User login;

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
        if (null == this.getUsername()) {
            this.setUsername(mergeFrom.getUsername());
        }
        super.mergePropertiesIfNull(mergeFrom);
    }

    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        return true;
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
        return 31 * super.hashCode();
    }

    @Transient
    public User getLogin() {
        return login;
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

    @Column(name = HibernateConsts.ADMIN_USERNAME)
    @Override
    public String getUsername() {
        return username;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }
    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public void setSourceSystemId(String sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
    }

    public void setLogin(User login) {
        this.login = login;
    }

}
