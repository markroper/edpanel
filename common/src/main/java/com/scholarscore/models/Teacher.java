package com.scholarscore.models;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;

@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Teacher extends ApiModel implements Serializable, IApiModel<Teacher>{
    public static final DimensionField ID = new DimensionField(Dimension.TEACHER, "ID");
    public static final DimensionField NAME = new DimensionField(Dimension.TEACHER, "Name");
    public static final DimensionField EMAIL_ADDRESS = new DimensionField(Dimension.TEACHER, "Address");
    public static final Set<DimensionField> DIMENSION_FIELDS = new HashSet<DimensionField>() {{
        add(ID);
        add(NAME);
        add(EMAIL_ADDRESS);
    }};
    
    public Teacher() {
        
    }
    
    public Teacher(Teacher t) {
        super(t);
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
    public void mergePropertiesIfNull(Teacher mergeFrom) {
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
}
