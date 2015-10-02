package com.scholarscore.models.user;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.scholarscore.models.ApiModel;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.IApiModel;

/**
 * Defines the base identity to attach to spring security with a username (primary key) and password
 * 
 * @author mattg
 */
@Entity(name = "user")
@Table(name = HibernateConsts.USERS_TABLE)
@Inheritance(strategy=InheritanceType.JOINED)
@JsonInclude(JsonInclude.Include.NON_NULL)
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
	
	private String emailAddress;
	private String emailConfirmCode;
	private Date emailConfirmCodeTime;
	private String phoneNumber;
	private String phoneConfirmCode;
	private Date phoneConfirmCodeTime;

	private Boolean emailConfirmed;
	private Boolean phoneConfirmed;
	
	public User() { }
	
	public User(User value) {
	    super(value);
		this.username = value.username;
		this.password = value.password;
		this.enabled = value.enabled;
		this.emailAddress = value.emailAddress;
		this.emailConfirmCode = value.emailConfirmCode;
		this.emailConfirmCodeTime = value.emailConfirmCodeTime;
		this.phoneNumber = value.phoneNumber;
		this.phoneConfirmCode = value.phoneConfirmCode;
		this.phoneConfirmCodeTime = value.phoneConfirmCodeTime;
		this.emailConfirmed = value.emailConfirmed;
		this.phoneConfirmed = value.phoneConfirmed;
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
	
	@Column(name = HibernateConsts.USER_EMAIL_ADDRESS)
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Column(name = HibernateConsts.USER_PHONE_NUMBER)
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Column(name = HibernateConsts.USER_EMAIL_CONFIRMED)
	public Boolean getEmailConfirmed() {
		return emailConfirmed;
	}

	public void setEmailConfirmed(Boolean emailConfirmed) {
		this.emailConfirmed = emailConfirmed;
	}

	@Column(name = HibernateConsts.USER_PHONE_CONFIRMED)
	public Boolean getPhoneConfirmed() {
		return phoneConfirmed;
	}

	public void setPhoneConfirmed(Boolean phoneConfirmed) {
		this.phoneConfirmed = phoneConfirmed;
	}

	@Column(name = HibernateConsts.USER_EMAIL_CONFIRM_CODE)
	public String getEmailConfirmCode() {
		return emailConfirmCode;
	}

	public void setEmailConfirmCode(String emailConfirmCode) {
		this.emailConfirmCode = emailConfirmCode;
	}

	@Column(name = HibernateConsts.USER_EMAIL_CONFIRM_CODE_GENERATED_TIME)
	public Date getEmailConfirmCodeTime() {
		return emailConfirmCodeTime;
	}

	public void setEmailConfirmCodeTime(Date emailConfirmCodeTime) {
		this.emailConfirmCodeTime = emailConfirmCodeTime;
	}

	@Column(name = HibernateConsts.USER_PHONE_CONFIRM_CODE)
	public String getPhoneConfirmCode() {
		return phoneConfirmCode;
	}

	public void setPhoneConfirmCode(String phoneConfirmCode) {
		this.phoneConfirmCode = phoneConfirmCode;
	}

	@Column(name = HibernateConsts.USER_PHONE_CONFIRM_CODE_GENERATED_TIME)
	public Date getPhoneConfirmCodeTime() {
		return phoneConfirmCodeTime;
	}

	public void setPhoneConfirmCodeTime(Date phoneConfirmCodeTime) {
		this.phoneConfirmCodeTime = phoneConfirmCodeTime;
	}

	@Override
	public void mergePropertiesIfNull(User mergeFrom) {
        if (null == username) {
        	this.username = mergeFrom.getUsername();
        }
        if (null == password) {
        	this.password = mergeFrom.getPassword();
        }
        if (null == enabled) {
        	this.enabled = mergeFrom.getEnabled();
        }
		if (null == emailAddress) {
			this.emailAddress = mergeFrom.getEmailAddress();
		}
		if (null == emailConfirmCode) {
			this.emailConfirmCode = mergeFrom.getEmailConfirmCode();
		}
		if (null == emailConfirmCodeTime) {
			this.emailConfirmCodeTime = mergeFrom.getEmailConfirmCodeTime();
		}
		if (null == phoneNumber) {
			this.phoneNumber = mergeFrom.getPhoneNumber();
		}
		if (null == phoneConfirmCode) {
			this.phoneConfirmCode = mergeFrom.getPhoneConfirmCode();
		}
		if (null == phoneConfirmCodeTime) {
			this.phoneConfirmCodeTime = mergeFrom.getPhoneConfirmCodeTime();
		}
		if (null == emailConfirmed) {
			this.emailConfirmed = mergeFrom.getEmailConfirmed();
		}
		if (null == phoneConfirmed) {
			this.phoneConfirmed = mergeFrom.getPhoneConfirmed();
		}
    }
	
	@Override
    public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;

        final User other = (User) obj;
		return Objects.equals(this.enabled, other.enabled)
                && Objects.equals(this.password, other.password)
                && Objects.equals(this.username, other.username)
				&& Objects.equals(this.emailAddress, other.emailAddress)
				&& Objects.equals(this.emailConfirmCode, other.emailConfirmCode)
				&& Objects.equals(this.emailConfirmCodeTime, other.emailConfirmCodeTime)
				&& Objects.equals(this.phoneNumber, other.phoneNumber)
				&& Objects.equals(this.phoneConfirmCode, other.phoneConfirmCode)
				&& Objects.equals(this.phoneConfirmCodeTime, other.phoneConfirmCodeTime)
				&& Objects.equals(this.emailConfirmed, other.emailConfirmed)
				&& Objects.equals(this.phoneConfirmed, other.phoneConfirmed)
				;
    }

	@Override
    public int hashCode() {
        return 31 * super.hashCode()
                + Objects.hash(username, enabled, password, emailAddress, emailConfirmCode, emailConfirmCodeTime, 
				phoneNumber, phoneConfirmCode, phoneConfirmCodeTime, emailConfirmed, phoneConfirmed);
    }

	@Override
	public String toString() {
		return super.toString() + "\n" +
				"User{" +
				", password='" + password + '\'' +
				", username='" + username + '\'' +
				", enabled=" + enabled +
				", id=" + id +
				", emailAddress='" + emailAddress + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
				", emailConfirmed=" + emailConfirmed +
				", phoneConfirmed=" + phoneConfirmed +
				'}';
	}
}
