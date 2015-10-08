package com.scholarscore.models.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.Address;

@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("serial")
public abstract class Person extends User {
    protected String sourceSystemId;
    protected Address homeAddress;
    protected String homePhone;
    protected Long currentSchoolId;
    
    public Person() {
        
    }
    
    public Person(Person s) {
        super(s);
        this.sourceSystemId = s.sourceSystemId;
        this.homeAddress = s.homeAddress;
        this.homePhone = s.homePhone;
        this.currentSchoolId = s.currentSchoolId;
    }
    
    @Override
    public void mergePropertiesIfNull(User mergeFrom) {
        if(mergeFrom instanceof Person) {
            Person staff = (Person) mergeFrom;
            if(null == this.sourceSystemId) {
                this.sourceSystemId = staff.sourceSystemId;
            }
            if(null == this.homeAddress) {
                this.homeAddress = staff.homeAddress;
            }
            if(null == this.homePhone) {
                this.homePhone = staff.homePhone;
            }
            if(null == this.currentSchoolId) {
                this.currentSchoolId = staff.currentSchoolId;
            }
        }
    }
    
    /**
     * Abstract to force subclasses to implement along with hibernate annotations
     */
    public abstract String getSourceSystemId();
    
    public void setSourceSystemId(String sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
    }
    
    /**
     * Abstract to force subclasses to implement along with hibernate annotations
     */
    public abstract Address getHomeAddress();
    
    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }
    
    /**
     * Abstract to force subclasses to implement along with hibernate annotations
     */
    public abstract String getHomePhone();
    
    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }
    
    /**
     * Abstract to force subclasses to implement along with hibernate annotations
     */
    public abstract Long getUserId();

    public void setUserId(Long userId) {
        setId(userId);
    }
    
    /**
     * Abstract to force the subclass to implement the method along with the hibernate annotations
     * @return
     */
    public abstract Long getCurrentSchoolId();

    public void setCurrentSchoolId(Long currentSchoolId) {
        this.currentSchoolId = currentSchoolId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Person staff = (Person) o;

        if (homePhone != null ? !homePhone.equals(staff.homePhone) : staff.homePhone != null) { return false; }
        if (homeAddress != null ? !homeAddress.equals(staff.homeAddress) : staff.homeAddress != null) { return false; }
        if (sourceSystemId != null ? !sourceSystemId.equals(staff.sourceSystemId) : staff.sourceSystemId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (homePhone != null ? homePhone.hashCode() : 0);
        result = 31 * result + (sourceSystemId != null ? sourceSystemId.hashCode() : 0);
        result = 31 * result + (homeAddress != null ? homeAddress.hashCode() : 0);
        return result;
    }
    
}
