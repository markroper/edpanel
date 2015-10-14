package com.scholarscore.models.user;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.scholarscore.models.ApiModel;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.IApiModel;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Fetch;

/**
 * Defines the base identity to attach to spring security with a username (primary key) and password
 * 
 * @author mattg
 */
@Entity(name = "user")
@Table(name = HibernateConsts.USERS_TABLE)
@Inheritance(strategy=InheritanceType.JOINED)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties( { "contactMethods" })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Student.class, name="STUDENT"),
    @JsonSubTypes.Type(value = Administrator.class, name = "ADMINISTRATOR"),
    @JsonSubTypes.Type(value = Teacher.class, name = "TEACHER")
})
public abstract class User extends ApiModel implements Serializable, IApiModel<User> {
	private static final long serialVersionUID = 1L;
	// login name
	private String username;
	private String password;
	// Indicates whether the user is a login user and can login (by default this is disabled until the user has set a username/password)
	private Boolean enabled = false;
	private String oneTimePass;
	private Date oneTimePassCreated;

	private Set<ContactMethod> contactMethods;
	
	// this optional boolean is usually null, but will be set to true in the special case 
	// where the user has logged in with a temporary/one-time password. 
	// If this value is true, this user will be severely limited in the endpoints
	// they are capable of accessing (until they call the resetPassword endpoint)
	private Boolean mustResetPassword; 
	
	public User() { }
	
	public User(User value) {
	    super(value);
		this.username = value.username;
		this.password = value.password;
		this.enabled = value.enabled;
		this.oneTimePass = value.oneTimePass;
		this.oneTimePassCreated = value.oneTimePassCreated;
	}

	@OneToMany
	@JoinColumn(name = HibernateConsts.CONTACT_METHOD_USER_FK, nullable = false)
	@Fetch(FetchMode.JOIN)
	@Cascade(CascadeType.ALL)
	public Set<ContactMethod> getContactMethods() {
		return contactMethods;
	}

	public void setContactMethods(Set<ContactMethod> contactMethods) {
		this.contactMethods = contactMethods;
	}

	@Transient
	public String getEmail() {
		if (contactMethods != null) {
			ContactMethod email = getContact(ContactType.EMAIL);
			return email == null ? null : email.getContactValue();
		}
		return null;
	}
	
	// TODO Jordan: test this! (also make it generic so both phone and email can use
	public void setEmail(String newEmail) { 
		if (contactMethods == null) {
			contactMethods = new HashSet<>();
		}
		
		// initialize in case we don't have an email record
		ContactMethod emailContactMethod = new ContactMethod();
		emailContactMethod.setContactType(ContactType.EMAIL);

		ContactMethod existingEmailContact = getContact(ContactType.EMAIL);
		boolean emailExistsInContactMethods = (existingEmailContact != null);

		if (emailExistsInContactMethods) { 
			emailContactMethod = existingEmailContact;
		}

		emailContactMethod.setContactValue(newEmail);

		if (!emailExistsInContactMethods) {
			contactMethods.add(emailContactMethod);
		}
		setContactMethods(contactMethods);
	}
	
	@JsonIgnore
	@Transient
	// this util method is used to directly access the email/phone field from within the contact methods
	private ContactMethod getContact(ContactType contactType) {
		if (getContactMethods() == null) { return null; }
		for (ContactMethod method : getContactMethods()) {
			if (method.getContactType().equals(contactType)) {
				return method;
			}
		}
		return null;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = HibernateConsts.USER_ID)
	public Long getId() {
		return super.getId();
	}

	@Column(name = HibernateConsts.USER_PASSWORD)
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = HibernateConsts.USER_NAME)
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = HibernateConsts.USER_ENABLED)
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Column(name = HibernateConsts.USER_ONETIME_PASS)
	public String getOneTimePass() {
		return oneTimePass;
	}

	public void setOneTimePass(String oneTimePass) {
		this.oneTimePass = oneTimePass;
	}

	@Column(name = HibernateConsts.USER_ONETIME_PASS_CREATED)
	public Date getOneTimePassCreated() {
		return oneTimePassCreated;
	}

	public void setOneTimePassCreated(Date oneTimePassCreated) {
		this.oneTimePassCreated = oneTimePassCreated;
	}

	@Transient
    public abstract String getSourceSystemId();

    public abstract void setSourceSystemId(String string);
    
	@Transient
	public abstract UserType getType();
	
	/**
	 * This prevents a jackson exception but is a no-op
	 * @param t
	 */
	public void setType(UserType t){}
	
	@Override
	public void mergePropertiesIfNull(User mergeFrom) {
		super.mergePropertiesIfNull(mergeFrom);
        if (null == username) {
        	this.username = mergeFrom.getUsername();
        }
        if (null == password) {
        	this.password = mergeFrom.getPassword();
        }
        if (null == enabled) {
        	this.enabled = mergeFrom.getEnabled();
        }
    }
	
	@Override
    public boolean equals(Object obj) {
	    if (!super.equals(obj)) {
            return false;
        }
		if (this == obj) return true;
		if (getClass() != obj.getClass()) return false;

        final User other = (User) obj;
		return Objects.equals(this.enabled, other.enabled)
                && Objects.equals(this.password, other.password)
                && Objects.equals(this.username, other.username)
				;
    }

	@Override
    public int hashCode() {
        return 31 * super.hashCode()
                + Objects.hash(username, enabled, password);
    }

	@Override
	public String toString() {
		return super.toString() + "\n" +
				"User{" +
				", password='" + password + '\'' +
				", username='" + username + '\'' +
				", enabled=" + enabled +
				'}';
	}
}
