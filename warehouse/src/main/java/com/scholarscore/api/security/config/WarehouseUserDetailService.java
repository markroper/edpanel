package com.scholarscore.api.security.config;

import com.scholarscore.api.persistence.AuthorityPersistence;
import com.scholarscore.api.persistence.UserPersistence;
import com.scholarscore.models.Authority;
import com.scholarscore.models.user.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public class WarehouseUserDetailService implements UserDetailsService {

	private UserPersistence userPersistence;
	private AuthorityPersistence authorityPersistence;

	public WarehouseUserDetailService(UserPersistence userPersistence,
									  AuthorityPersistence authPersistence) {
		this.userPersistence = userPersistence;
		this.authorityPersistence = authPersistence;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		User identity = userPersistence.selectUserByName(username);
		if (null != identity) {
			List<Authority> authorities = authorityPersistence.selectAuthorities(username);
			UserDetailsProxy proxy = new UserDetailsProxy(identity, authorities);
			return proxy;
		}
		return null;
	}
}
