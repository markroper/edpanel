package com.scholarscore.api.controller.security;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

/**
 * Initialized at container startup and injects the security configuration entity to determine how
 * to authenticate the controllers in this module
 * 
 * @author mattg
 *
 */
public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
	
	public SecurityWebApplicationInitializer() {
		super(SecurityConfig.class);
	}
}
