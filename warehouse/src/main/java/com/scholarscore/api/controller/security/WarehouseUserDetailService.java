package com.scholarscore.api.controller.security;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.scholarscore.api.persistence.mysql.AuthorityPersistence;
import com.scholarscore.api.persistence.mysql.UserPersistence;
import com.scholarscore.models.Authority;
import com.scholarscore.models.User;

public class WarehouseUserDetailService implements UserDetailsService {

	private UserPersistence userPersistence;
	private AuthorityPersistence authorityPersistence;

	public WarehouseUserDetailService(UserPersistence userPersistence, AuthorityPersistence authPersistence) {
		this.userPersistence = userPersistence;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		User user = userPersistence.selectUser(username);
		if (null != user) {
			List<Authority> authorities = authorityPersistence.selectAuthorities(username);
			UserDetailsProxy proxy = new UserDetailsProxy(user, authorities);
			return proxy;
		}
		return null;
	}

}
