package com.scholarscore.api.security.config;

import java.util.List;

import com.scholarscore.api.persistence.AuthorityPersistence;
import com.scholarscore.api.persistence.StudentPersistence;
import com.scholarscore.api.persistence.TeacherPersistence;
import com.scholarscore.api.persistence.UserPersistence;
import com.scholarscore.models.Identity;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.scholarscore.models.Authority;
import com.scholarscore.models.user.Administrator;
import com.scholarscore.models.user.User;

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
		Identity identity = userPersistence.getIdentity(username);
		if (null != identity) {
			List<Authority> authorities = authorityPersistence.selectAuthorities(username);
			UserDetailsProxy proxy = new UserDetailsProxy(identity, authorities);
			return proxy;
		}
		return null;
	}
}
