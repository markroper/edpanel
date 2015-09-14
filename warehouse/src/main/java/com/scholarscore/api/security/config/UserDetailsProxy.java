package com.scholarscore.api.security.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.scholarscore.models.Identity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.scholarscore.models.Authority;
import com.scholarscore.models.User;

/**
 * Simple proxy / wrapper for the purpose of converting a User & Authority object to a UserDetails object
 * 
 * @author mattg
 */
@SuppressWarnings("serial")
public class UserDetailsProxy implements UserDetails {

	private Identity identity;
	private List<Authority> authorities;

	public UserDetailsProxy(Identity identity, List<Authority> authorities) {
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
		return identity.getLogin().getPassword();
	}

	@Override
	public String getUsername() {
		return identity.getLogin().getUsername();
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
		return identity.getLogin().getEnabled();
	}

}
