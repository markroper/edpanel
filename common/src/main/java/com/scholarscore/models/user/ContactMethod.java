package com.scholarscore.models.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.IApiModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * User: jordan
 * Date: 10/2/15
 * Time: 7:50 PM
 * * 
 */
@Entity(name = HibernateConsts.CONTACT_METHOD_TABLE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactMethod implements Serializable, IApiModel<ContactMethod> {
    
    private Long id;
    private ContactType contactType;
    @Size(min=1, max=256)
    private String contactValue;
    @Size(min=1, max=64)
    private String confirmCode;
    private Date confirmCodeCreated;

    private Boolean confirmed = false;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = HibernateConsts.CONTACT_METHOD_ID)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = HibernateConsts.CONTACT_METHOD_TYPE)
    @Enumerated(EnumType.STRING)
    public ContactType getContactType() {
        return contactType;
    }

    public void setContactType(ContactType contactType) {
        this.contactType = contactType;
    }

    @Column(name = HibernateConsts.CONTACT_METHOD_CONTACT_VALUE)
    public String getContactValue() {
        return contactValue;
    }

    public void setContactValue(String contactValue) {
        this.contactValue = contactValue;
    }

    @Column(name = HibernateConsts.CONTACT_METHOD_CONFIRM_CODE)
    public String getConfirmCode() {
        return confirmCode;
    }

    public void setConfirmCode(String confirmCode) {
        this.confirmCode = confirmCode;
    }

    @Column(name = HibernateConsts.CONTACT_METHOD_CONFIRM_CODE_CREATED)
    public Date getConfirmCodeCreated() {
        return confirmCodeCreated;
    }

    public void setConfirmCodeCreated(Date confirmCodeCreated) {
        this.confirmCodeCreated = confirmCodeCreated;
    }

    @Column(name = HibernateConsts.CONTACT_METHOD_CONFIRMED)
    public Boolean confirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    @Override
    public void mergePropertiesIfNull(ContactMethod mergeFrom) {
        if (mergeFrom == null) { return; }
        if (id == null) {
            this.id = mergeFrom.id;
        }
        if (contactType == null) {
            this.contactType = mergeFrom.contactType;
        }
        if (contactValue == null) {
            this.contactValue = mergeFrom.contactValue;
        }
        if (confirmCode == null) {
            this.confirmCode = mergeFrom.confirmCode;
        }
        if (confirmCodeCreated == null) {
            this.confirmCodeCreated = mergeFrom.confirmCodeCreated;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ContactMethod other = (ContactMethod) obj;
        return Objects.equals(this.id, other.id) 
                && Objects.equals(this.contactType, other.contactType)
                && Objects.equals(this.contactValue, other.contactValue)
                && Objects.equals(this.confirmCode, other.confirmCode)
                && Objects.equals(this.confirmCodeCreated, other.confirmCodeCreated)
                && Objects.equals(this.confirmed, other.confirmed);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, contactType,
                contactValue,
                confirmCode,
                confirmCodeCreated,
                confirmed);
    }

    @Override
    public String toString() {
        return "ContactMethod{" +
                "id=" + id +
                ", contactType=" + contactType +
                ", contactValue='" + contactValue + '\'' +
                ", confirmCode='" + confirmCode + '\'' +
                ", confirmCodeCreated=" + confirmCodeCreated +
                ", confirmed=" + confirmed +
                '}';
    }
}
