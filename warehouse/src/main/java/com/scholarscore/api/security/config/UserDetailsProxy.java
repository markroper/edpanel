package com.scholarscore.api.security.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.Authority;
import com.scholarscore.models.user.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Simple proxy / wrapper for the purpose of converting a User & Authority object to a UserDetails object
 * 
 * @author mattg
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDetailsProxy implements UserDetails {

	private User identity;
	private List<Authority> authorities;

	public UserDetailsProxy() {
	    
	}
	
	public UserDetailsProxy(User identity, List<Authority> authorities) {
		this.identity = identity;
		this.authorities = authorities;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		for (Authority auth : authorities) {
			grantedAuthorities.add(new GrantedAuthorityProxy(auth));
		}
		return grantedAuthorities;
	}

	@Override
	public String getPassword() {
		return identity.getPassword();
	}

	@Override
	public String getUsername() {
		return identity.getUsername();
	}

	/**
	 * Is the account not expired is the question being answered here
	 * @return
	 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/**
	 * Is the account not locked is the answer here
	 *
	 * @return
	 */
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	/**
	 * Are the credentials not expired?
	 *
	 * @return
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return identity.getEnabled();
	}

	public User getIdentity() {
		return identity;
	}
}
