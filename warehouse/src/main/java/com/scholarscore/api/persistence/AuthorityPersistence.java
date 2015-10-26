package com.scholarscore.api.persistence;

import com.scholarscore.models.Authority;

import java.util.List;

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
