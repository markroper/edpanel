package com.scholarscore.models.user;

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
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
		this.contactMethods = value.contactMethods;
	}

	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
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
		return getContactValue(ContactType.EMAIL);
	}
	
	public void setEmail(String newEmail) { 
		setContactValue(newEmail, ContactType.EMAIL);
	}
	
	@Transient
	public String getPhone() { 
		return getContactValue(ContactType.PHONE);
	}
	
	public void setPhone(String newPhone) { 
		setContactValue(newPhone, ContactType.PHONE);
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

	@Transient
	@JsonIgnore
	private String getContactValue(ContactType contactType) {
		ContactMethod contactMethod = getContact(contactType);
		return contactMethod == null ? null : contactMethod.getContactValue();
	}

	@Transient
	@JsonIgnore
	public void setContactValue(String newContactValue, ContactType contactType) {
		if (contactMethods == null) {
			contactMethods = new HashSet<>();
		}

		// initialize in case we don't have an email record
		ContactMethod contactMethod = new ContactMethod();
		contactMethod.setContactType(contactType);

		ContactMethod existingContact = getContact(contactType);
		boolean contactWithTypeAlreadyExists = (existingContact != null);

		if (contactWithTypeAlreadyExists) {
			// reuse the existing record ID
			contactMethod.setId(existingContact.getId());
		}

		contactMethod.setContactValue(newContactValue);
		contactMethod.setUser(this);

		if (!contactWithTypeAlreadyExists) {
			contactMethods.add(contactMethod);
		}
		setContactMethods(contactMethods);
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = HibernateConsts.USER_ID)
	public Long getId() {
		return super.getId();
	}

	@Column(name = HibernateConsts.USER_PASSWORD, columnDefinition = "char")
	@JsonIgnore
	public String getPassword() { 
		return password;
	}

	@JsonIgnore
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
	@JsonIgnore
	public Boolean getEnabled() {
		return enabled;
	}

	@JsonIgnore
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Column(name = HibernateConsts.USER_ONETIME_PASS)
	@JsonIgnore
	public String getOneTimePass() {
		return oneTimePass;
	}

	@JsonIgnore
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
		if (null == oneTimePass) {
			this.oneTimePass = mergeFrom.getOneTimePass();
		}
		if (null == oneTimePassCreated) {
			this.oneTimePassCreated = mergeFrom.getOneTimePassCreated();
		}
		
		if (null == contactMethods) {
			this.contactMethods = mergeFrom.getContactMethods();
		} else {
			// contact methods require special handling
			ContactMethod.mergeContactMethods(this.contactMethods, mergeFrom.getContactMethods());
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

	/**
	 * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
	 * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
	 * chain setting attributes together.
	 */
	public static abstract class UserBuilder<U extends UserBuilder<U, T>, T extends User> extends ApiModelBuilder<U, T>{
		// login name
		private String username;
		private String password;
		private Boolean enabled = false;

		public U withUsername(final String username){
			this.username = username;
			return me();
		}

		public U withPassword(final String password){
			this.password = password;
			return me();
		}

		public U withEnabled(final Boolean enabled){
			this.enabled = enabled;
			return me();
		}

		public T build(){
			T user = super.build();
			user.setUsername(username);
			user.setPassword(password);
			user.setEnabled(enabled);
			return user;
		}

		public abstract T getInstance();
	}
}
