package com.scholarscore.api.security.config;

import com.scholarscore.api.persistence.UserPersistence;
import com.scholarscore.models.Authority;
import com.scholarscore.models.user.User;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

/**
 * User: jordan
 * Date: 10/6/15
 * Time: 1:27 AM
 */
public class OneTimePassAuthProvider implements AuthenticationProvider {

    public static final String ROLE_MUST_CHANGE_PASSWORD = "ROLE_MUST_CHANGE_PASSWORD";

    private UserPersistence userPersistence;
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = userPersistence.selectUserByName(name);
        if (null != user && null != password) {
            if (password.equals(user.getOneTimePass())) {
                List<GrantedAuthority> grantedAuths = buildMustChangePasswordGrantedAuthorities();
                List<Authority> authorities = buildMustChangePasswordAuthorities(user.getId());
                UserDetailsProxy userDetailsProxy = new UserDetailsProxy(user, authorities);
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetailsProxy, password, grantedAuths);
                return auth;
            } else {
                return null;
            }
        }
        return null;
    }

    private List<Authority> buildMustChangePasswordAuthorities(Long userId) {
        List<Authority> authorities = new ArrayList<>();
        Authority authority = new Authority();
        authority.setUserId(userId);
        authority.setAuthority(ROLE_MUST_CHANGE_PASSWORD);
        authorities.add(authority);
        return authorities;
    }
    
    private List<GrantedAuthority> buildMustChangePasswordGrantedAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        GrantedAuthority authority = new SimpleGrantedAuthority(ROLE_MUST_CHANGE_PASSWORD);
        authorities.add(authority);
        return authorities;
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    public UserPersistence getUserPersistence() {
        return userPersistence;
    }

    public void setUserPersistence(UserPersistence userPersistence) {
        this.userPersistence = userPersistence;
    }
}
