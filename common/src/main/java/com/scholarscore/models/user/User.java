package com.scholarscore.models.user;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.ApiModel;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.IApiModel;

import javax.persistence.*;

/**
 * Defines the base identity to attach to spring security with a username (primary key) and password
 * 
 * @author mattg
 */
@Entity(name = "user")
@Table(name = HibernateConsts.USERS_TABLE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class User extends ApiModel implements Serializable, IApiModel<User> {
	private static final long serialVersionUID = 1L;
	// login name
	private String username;
	private String password;
	// Indicates whether the user is a login user and can login (by default this is disabled until the user has set a username/password)
	private Boolean enabled;
	private Long id;
	
	public User() { }
	
	public User(User value) {
		this.username = value.username;
		this.password = value.password;
		this.enabled = value.enabled;
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
    }
	
	@Override
    public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;

        final User other = (User) obj;
		return  Objects.equals(this.id, other.id)
        		&& Objects.equals(this.enabled, other.enabled)
                && Objects.equals(this.password, other.password)
                && Objects.equals(this.username, other.username);
    }

	@Override
    public int hashCode() {
        return 31 * super.hashCode()
                + Objects.hash(id, username, enabled, password);
    }

	@Override
	public String toString() {
		return super.toString() + "\n" +
				"User{" +
				"id='" + id + "\'" +
				"password='" + password + '\'' +
				", username='" + username + '\'' +
				", enabled=" + enabled +
				'}';
	}
}
