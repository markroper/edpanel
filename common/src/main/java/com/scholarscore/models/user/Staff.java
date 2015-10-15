package com.scholarscore.models.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.Address;

@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("serial")
public abstract class Staff extends User {
    protected String sourceSystemId;
    protected Address homeAddress;
    protected String homePhone;
    
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Staff staff = (Staff) o;

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

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static abstract class StaffBuilder<U extends StaffBuilder<U, T>,T extends Staff> extends UserBuilder<U,T>{
        private String sourceSystemId;
        private Address homeAddress;
        private String homePhone;

        public U withSourceSystemid(final String sourceSystemId){
            this.sourceSystemId = sourceSystemId;
            return me();
        }

        public U withHomeAddress(final Address homeAddress){
            this.homeAddress = homeAddress;
            return me();
        }

        public U withHomePhone(final String homePhone){
            this.homePhone = homePhone;
            return me();
        }

        public T build(){
            T staff = super.build();
            staff.setSourceSystemId(sourceSystemId);
            staff.setHomeAddress(homeAddress);
            staff.setHomePhone(homePhone);
            return staff;
        }
    }
}
