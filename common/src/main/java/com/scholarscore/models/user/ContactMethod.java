package com.scholarscore.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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

    private User user;
    
    private Boolean confirmed = true;
    
    public ContactMethod() { 
    
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = HibernateConsts.CONTACT_METHOD_USER_FK, nullable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        if (confirmed == null) {
            this.confirmed = mergeFrom.confirmed;
        }
        if (user == null) {
            this.user = mergeFrom.user;
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
                && Objects.equals(this.confirmed, other.confirmed)
                && Objects.equals(this.user, other.user);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, contactType,
                contactValue,
                confirmCode,
                confirmCodeCreated,
                confirmed,
                user);
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
                ", user=" + user +
                '}';
    }

    @Transient
    @JsonIgnore
    // email and phone are just a facade on ContactMethods so special handling is needed.
    // This method should be called instead of directly replacing existing contacts with new ones.
    // --
    // This method takes in a set of new contact methods and existing contact methods and merges them.
    // Contact methods only in the 'new' set will be added, contact methods in
    // both old and new sets will be updated and contact methods only in the old set will be preserved.
    public static Set<ContactMethod> mergeContactMethods(Set<ContactMethod> newContactMethods, Set<ContactMethod> existingContactMethods) {

        if ((newContactMethods == null || newContactMethods.isEmpty()) 
                && (existingContactMethods == null || existingContactMethods.isEmpty())) {
            // don't bother doing work, nothing meaningful to do
            if (newContactMethods == null && existingContactMethods == null) {
                return null; 
            } else {
                return new HashSet<ContactMethod>();
            }
        }

        HashMap<ContactType, ContactMethod> mergedContactMethods = new HashMap<>();
        
        // first, put all of the existing contact methods into a set. any not replaced later will be preserved
        if (existingContactMethods != null && !existingContactMethods.isEmpty()) {
            addContactMethodsToMap(existingContactMethods, mergedContactMethods);
        }

        // then put the incoming contact methods into the same set -- this handles both 'add' and 'replace' cases
        // as new contact methods will override existing ones if they are the same type
        if (newContactMethods != null && !newContactMethods.isEmpty()) {
            addContactMethodsToMap(newContactMethods, mergedContactMethods);
        }
        
        return new HashSet<>(mergedContactMethods.values());
    }
    
    private static void addContactMethodsToMap(Set<ContactMethod> contactMethodSet, Map<ContactType, ContactMethod> map) {
        for (ContactMethod contactMethod : contactMethodSet) {
            ContactMethod existingContactMethod = map.get(contactMethod.getContactType());
            // if the new entry is replacing/updating an existing contact method, we need to use the same ID
            if (existingContactMethod != null) {
                contactMethod.setId(existingContactMethod.getId());
            }
            map.put(contactMethod.getContactType(), contactMethod);
        }
    }
}
