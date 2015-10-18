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
	private Long userId;
	private String authority;
	public String getAuthority() {
		return authority;
	}
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * Each class's Builder holds a copy of each attribute that the parent POJO has. We build up these properties using
	 * a pattern of with[Attribute](Attribute attribute) and return the same instance of the Builder so that one can easily
	 * chain setting attributes together.
	 */
	public static class AuthorityBuilder{
		private Long userId;
		private String authority;

		public AuthorityBuilder withUserId(final Long userId){
			this.userId = userId;
			return this;
		}

		public AuthorityBuilder withAuthority(final String authority){
			this.authority = authority;
			return this;
		}

		public Authority build(){
			Authority authIntstance = new Authority();
			authIntstance.setUserId(userId);
			authIntstance.setAuthority(authority);
			return authIntstance;
		}
	}
}
