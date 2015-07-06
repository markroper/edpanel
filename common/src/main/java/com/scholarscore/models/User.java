package com.scholarscore.models;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Defines the base identity to attach to spring security with a username (primary key) and password
 * 
 * @author mattg
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User extends ApiModel implements Serializable, IApiModel<User> {
	// v1
	private static final long serialVersionUID = 1L;

	public User() {	
	}
	
	public User(User value) {
		super(value);
	}

	private String password;
	
	// or login name
	private String username;
	// full name
	private String name;
	
	// Indicates whether the user is a login user and can login (by default this is disabled until the user has set a username/password)
	private Boolean enabled;

	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
        final User other = (User) obj;
        return Objects.equals(this.enabled, other.enabled)
                && Objects.equals(this.password, other.password)
                && Objects.equals(this.username, other.username);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode()
                + Objects.hash(username, enabled, password);
    }
}
