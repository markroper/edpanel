package com.scholarscore.api.security.config;

import java.util.List;

import com.scholarscore.api.persistence.mysql.StudentPersistence;
import com.scholarscore.api.persistence.mysql.TeacherPersistence;
import com.scholarscore.models.Administrator;
import com.scholarscore.models.Identity;
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
