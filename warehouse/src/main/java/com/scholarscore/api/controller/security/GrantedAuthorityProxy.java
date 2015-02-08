package com.scholarscore.api.controller.security;

import org.springframework.security.core.GrantedAuthority;

import com.scholarscore.models.Authority;

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
