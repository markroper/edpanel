package com.scholarscore.models.user;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("serial")
public abstract class Person extends User {
    protected String sourceSystemId;
    protected String sourceSystemUserId;
    protected String homePhone;
    protected Long currentSchoolId;
    
    public Person() {
        
    }
    
    public Person(Person s) {
        super(s);
        this.sourceSystemId = s.sourceSystemId;
        this.homePhone = s.homePhone;
        this.currentSchoolId = s.currentSchoolId;
        this.sourceSystemUserId = s.sourceSystemUserId;
    }
    
    @Override
    public void mergePropertiesIfNull(User mergeFrom) {
        super.mergePropertiesIfNull(mergeFrom);
        if(mergeFrom instanceof Person) {
            Person staff = (Person) mergeFrom;
            if(null == this.sourceSystemId) {
                this.sourceSystemId = staff.sourceSystemId;
            }
            if(null == this.sourceSystemUserId) {
                this.sourceSystemUserId = staff.sourceSystemUserId;
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

    /**
     * Abstract to force the subclass to implement the method along with the hibernate annotations
     * @return
     */
    public abstract String getSourceSystemUserId();

    public void setSourceSystemUserId(String sourceSystemUserId) {
        this.sourceSystemUserId = sourceSystemUserId;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(sourceSystemId, sourceSystemUserId, homePhone, currentSchoolId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final Person other = (Person) obj;
        return Objects.equals(this.sourceSystemId, other.sourceSystemId)
                && Objects.equals(this.sourceSystemUserId, other.sourceSystemUserId)
                && Objects.equals(this.homePhone, other.homePhone)
                && Objects.equals(this.currentSchoolId, other.currentSchoolId);
    }

    /**
     * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
     * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
     * chain setting attributes together.
     */
    public static abstract class PersonBuilder<U extends PersonBuilder<U, T>,T extends Person> extends UserBuilder<U,T>{
        private String sourceSystemId;
        private String sourceSystemUserId;
        private String homePhone;

        public U withSourceSystemid(final String sourceSystemId){
            this.sourceSystemId = sourceSystemId;
            return me();
        }

        public U withSourceSystemUserId(final String sourceSystemUserId){
            this.sourceSystemUserId = sourceSystemUserId;
            return me();
        }

        public U withHomePhone(final String homePhone){
            this.homePhone = homePhone;
            return me();
        }

        public T build(){
            T staff = super.build();
            staff.setSourceSystemId(sourceSystemId);
            staff.setHomePhone(homePhone);
            staff.setSourceSystemUserId(sourceSystemUserId);
            return staff;
        }
    }
}
