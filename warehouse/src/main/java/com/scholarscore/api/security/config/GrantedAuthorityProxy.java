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
		return auth.getAuthority();
	}
}
