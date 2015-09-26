package com.scholarscore.models;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;

/**
 * Defines the base identity to attach to spring security with a username (primary key) and password
 * 
 * @author mattg
 */
@Entity(name = "user")
@Table(name = HibernateConsts.USERS_TABLE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User implements Serializable, IApiModel<User> {
	// v1
	private static final long serialVersionUID = 1L;

	// login name
	private String username;
	private String password;

	// Indicates whether the user is a login user and can login (by default this is disabled until the user has set a username/password)
	private Boolean enabled;
	private Long id;
	
	private String emailAddress;
	private String phoneNumber;
	private Boolean emailConfirmed = false;
	private Boolean phoneConfirmed = false;
	
	public User() { }
	
	public User(User value) {
		this.username = value.username;
		this.password = value.password;
		this.enabled = value.enabled;
		this.emailAddress = value.emailAddress;
		this.phoneNumber = value.phoneNumber;
		this.emailConfirmed = value.emailConfirmed;
		this.phoneConfirmed = value.phoneConfirmed;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = HibernateConsts.USER_ID)
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) { 
		this.id = id;
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
		if (null == phoneNumber) {
			this.phoneNumber = mergeFrom.getPhoneNumber();
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
		return  Objects.equals(this.id, other.id)
        		&& Objects.equals(this.enabled, other.enabled)
                && Objects.equals(this.password, other.password)
                && Objects.equals(this.username, other.username)
				&& Objects.equals(this.emailAddress, other.emailAddress)
				&& Objects.equals(this.phoneNumber, other.phoneNumber)
				&& Objects.equals(this.emailConfirmed, other.emailConfirmed)
				&& Objects.equals(this.phoneConfirmed, other.phoneConfirmed)
				;
    }

	@Override
    public int hashCode() {
        return 31 * super.hashCode()
                + Objects.hash(id, username, enabled, password, emailAddress, phoneNumber, emailConfirmed, phoneConfirmed);
    }

	@Override
	public String toString() {
		return super.toString() + "\n" +
				"User{" +
				"id='" + id + "\'" +
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
