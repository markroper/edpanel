package com.scholarscore.models;

/**
 * Defines the authority the user has within the system
 * 
 * For example ROLE_ADMIN, ROLE_TEACHER, or ROLE_STUDENT are possible roles of a User identity.  
 * 
 * User have one or more Authorities
 * 
 * @author mattg
 *
 */
public class Authority {
	private String username;
	private String authority;
	public String getAuthority() {
		return authority;
	}
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}