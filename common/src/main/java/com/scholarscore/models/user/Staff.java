package com.scholarscore.models.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.Address;
import com.scholarscore.models.HibernateConsts;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

    private StaffRole staffRole;
    protected UserType userType;
    public Staff() {
    }

    public Staff(Staff s) {
        super(s);
        setStaffRole(s.getStaffRole());
        userType = s.getType();


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

    @Column(name = HibernateConsts.STAFF_ROLE)
    @Enumerated(EnumType.STRING)
    public StaffRole getStaffRole() {
        return staffRole;
    }

    public void setStaffRole(StaffRole staffRole) {
        this.staffRole = staffRole;
    }

    @Override
    @Transient
    public UserType getType() {
        return this.userType;
    }

    @Override
    public void setType(UserType userType) {
        this.userType = userType;
    }


    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final Staff other = (Staff) obj;
        return Objects.equals(this.staffRole, other.staffRole);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode()
                + Objects.hash(staffRole, currentSchoolId);
    }

    @Override
    public String toString() {
        return "Staff{" + "(super: " + super.toString() + ")" +
                ", staffRole='" + staffRole + '\'' +
                '}';
    }

}
