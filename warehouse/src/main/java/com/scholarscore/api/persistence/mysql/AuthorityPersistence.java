package com.scholarscore.api.persistence.mysql;

import java.util.List;

import com.scholarscore.models.Authority;

/**
 * Defines the pattern for accessing authorities by a username
 * 
 * @author mattg
 *
 */
public interface AuthorityPersistence {
	public List<Authority> selectAuthorities(String username);
	public void createAuthority(Authority authority);
	public void deleteAuthority(Long userId);
}
