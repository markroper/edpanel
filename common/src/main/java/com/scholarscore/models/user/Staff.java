package com.scholarscore.models.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.Address;
import com.scholarscore.models.HibernateConsts;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;
import java.util.Objects;

/**
 * Created by cwallace on 1/18/16.
 */

@Entity(name = HibernateConsts.STAFF_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
@PrimaryKeyJoinColumn(name=HibernateConsts.STAFF_USER_FK, referencedColumnName = HibernateConsts.USER_ID)
public class Staff extends Person {

    private boolean isAdmin;
    private boolean isTeacher;
    public Staff() {
        isAdmin = false;
        isTeacher = false;
    }

    public Staff(Staff s) {
        super(s);
        this.isAdmin = s.isAdmin;
        this.isTeacher = s.isTeacher;

    }

    @Column(name = HibernateConsts.STAFF_IS_ADMIN)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Column(name = HibernateConsts.STAFF_IS_TEACHER)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    public boolean isTeacher() {
        return isTeacher;
    }

    public void setTeacher(boolean teacher) {
        isTeacher = teacher;
    }

    @Column(name = HibernateConsts.STAFF_NAME)
    public String getName() {
        return super.getName();
    }

    @OneToOne(optional = true)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name=HibernateConsts.STAFF_ADDRESS_FK)
    public Address getHomeAddress() {
        return homeAddress;
    }

    @Column(name = HibernateConsts.STAFF_HOME_PHONE)
    public String getHomePhone() {
        return homePhone;
    }

    @Column(name = HibernateConsts.STAFF_SOURCE_SYSTEM_ID)
    public String getSourceSystemId() {
        return sourceSystemId;
    }

    @Column(name = HibernateConsts.STAFF_USER_FK, insertable = false, updatable = false)
    public Long getUserId() {
        return getId();
    }

    @Column(name = HibernateConsts.SCHOOL_FK, nullable = true)
    public Long getCurrentSchoolId() {
        return currentSchoolId;
    }

    @Override
    @Column(name = HibernateConsts.STAFF_SOURCE_SYSTEM_USER_ID)
    public String getSourceSystemUserId() {
        return sourceSystemUserId;
    }


    @Override
    @Transient
    public UserType getType() {
        if (isAdmin) {
            return UserType.ADMINISTRATOR;
        } else if (isTeacher){
            return UserType.TEACHER;
        } else {
            return null;
        }
    }



    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        final Staff other = (Staff) obj;
        return Objects.equals(this.isAdmin, other.isAdmin) &&
                Objects.equals(this.isTeacher, other.isTeacher);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode()
                + Objects.hash(currentSchoolId, isAdmin, isTeacher);
    }

    @Override
    public String toString() {
        return "Staff{" + "(super: " + super.toString() + ")" +
                ", isAdmin='" + isAdmin + '\'' +
                ", isTeacher='" + isTeacher + '\'' +
                '}';
    }

    public static class StaffBuilder extends PersonBuilder<StaffBuilder, Staff> {

        //Source system identifier. E.g. powerschool ID
        private String sourceSystemId;
        private Address homeAddress;
        //EthnicityRace
        private Long currentSchoolId;
        private boolean isAdmin;
        private boolean isTeacher;

        public StaffBuilder withSourceSystemId(final String sourceSystemId){
            this.sourceSystemId = sourceSystemId;
            return this;
        }


        public StaffBuilder withHomeAddress(final Address homeAddress){
            this.homeAddress = homeAddress;
            return this;
        }

        public StaffBuilder withCurrentSchoolId(final Long currentSchoolId){
            this.currentSchoolId = currentSchoolId;
            return this;
        }

        public StaffBuilder withAdmin(final boolean isAdmin) {
            this.isAdmin = isAdmin;
            return this;
        }

        public StaffBuilder withTeacher(final boolean isTeacher) {
            this.isTeacher = isTeacher;
            return this;
        }

        public Staff build(){
            Staff staff = super.build();
            staff.setSourceSystemId(sourceSystemId);
            staff.setHomeAddress(homeAddress);
            staff.setCurrentSchoolId(currentSchoolId);
            staff.setAdmin(isAdmin);
            staff.setTeacher(isTeacher);
            return staff;
        }

        @Override
        protected StaffBuilder me() {
            return this;
        }

        public Staff getInstance(){
            return new Staff();
        }
    }

}
