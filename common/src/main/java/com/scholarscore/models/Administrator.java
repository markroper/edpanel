package com.scholarscore.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mattg on 7/19/15.
 */
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

    public Administrator(Administrator t) {
        super(t);
        this.setLogin(t.getLogin());
        this.setSourceSystemId(t.getSourceSystemId());
        this.setName(t.getName());
        this.setHomeAddress(t.getHomeAddress());
        this.setHomePhone(t.getHomePhone());
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
        if (null == this.homeAddress) {
            this.homeAddress = mergeFrom.homeAddress;
        }
        if (null == this.homePhone) {
            this.homePhone = mergeFrom.homePhone;
        }
        if (null == this.sourceSystemId) {
            this.sourceSystemId = mergeFrom.sourceSystemId;
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

    @Override
    public int hashCode() {
        return 31 * super.hashCode();
    }

    public void setSourceSystemId(String sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
    }

    public void setLogin(User login) {
        this.login = login;
    }

    public User getLogin() {
        return login;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getSourceSystemId() {
        return sourceSystemId;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

}
