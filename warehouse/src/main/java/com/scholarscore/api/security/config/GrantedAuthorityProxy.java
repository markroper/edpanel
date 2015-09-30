package com.scholarscore.api.security.config;

import org.springframework.security.core.GrantedAuthority;

import com.scholarscore.models.Authority;

@SuppressWarnings("serial")
public class GrantedAuthorityProxy implements GrantedAuthority {
	public GrantedAuthorityProxy(Authority auth) {
		this.auth = auth;
	}
	
	private Authority auth;
	
	@Override
	public String getAuthority() {
		if (auth == null || auth.getAuthority() == null) { return null; }
		String authValue = auth.getAuthority();
		// ugh, spring security requires this prefix
		if (!authValue.startsWith("ROLE_")) {
			return "ROLE_" + auth.getAuthority();
		} else {
			return auth.getAuthority();
		}
	}
}
