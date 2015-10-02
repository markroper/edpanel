package com.scholarscore.models.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.Address;
import com.scholarscore.models.Course;

@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("serial")
public abstract class Staff extends User {
    private String sourceSystemId;
    private Address homeAddress;
    private String homePhone;
    private Long userId;
    
    public Staff() {
        
    }
    
    public Staff(Staff s) {
        super(s);
        this.sourceSystemId = s.sourceSystemId;
        this.homeAddress = s.homeAddress;
        this.homePhone = s.homePhone;
    }
    
    @Override
    public void mergePropertiesIfNull(User mergeFrom) {
        if(mergeFrom instanceof Staff) {
            Staff staff = (Staff) mergeFrom;
            if(null == this.sourceSystemId) {
                this.sourceSystemId = staff.sourceSystemId;
            }
            if(null == this.homeAddress) {
                this.homeAddress = staff.homeAddress;
            }
            if(null == this.homePhone) {
                this.homePhone = staff.homePhone;
            }
            if(null == this.userId) {
                this.userId = staff.userId;
            }
        }
    }
    
    public String getSourceSystemId() {
        return sourceSystemId;
    }
    public void setSourceSystemId(String sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
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
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Staff staff = (Staff) o;

        if (homePhone != null ? !homePhone.equals(staff.homePhone) : staff.homePhone != null) { return false; }
        if (homeAddress != null ? !homeAddress.equals(staff.homeAddress) : staff.homeAddress != null) { return false; }
        if (userId != null ? !userId.equals(staff.userId) : staff.userId != null) { return false; }
        if (sourceSystemId != null ? !sourceSystemId.equals(staff.sourceSystemId) : staff.sourceSystemId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (homePhone != null ? homePhone.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (sourceSystemId != null ? sourceSystemId.hashCode() : 0);
        result = 31 * result + (homeAddress != null ? homeAddress.hashCode() : 0);
        return result;
    }
    
}
