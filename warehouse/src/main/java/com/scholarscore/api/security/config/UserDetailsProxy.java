package com.scholarscore.api.security.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.scholarscore.models.Authority;
import com.scholarscore.models.User;

/**
 * Simple proxy / wrapper for the purpose of converting a User & Authority object to a UserDetails object
 * 
 * @author mattg
 */
public class UserDetailsProxy implements UserDetails {

	private User user;
	private List<Authority> authorities;

	public UserDetailsProxy(User user, List<Authority> authorities) {
		this.user = user;
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
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return user.getEnabled();
	}

}
