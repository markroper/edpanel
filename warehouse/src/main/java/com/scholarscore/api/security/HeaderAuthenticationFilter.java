package com.scholarscore.api.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This filter will look for the header, check it for validity and then add the new header to the response. 
 * It will also look up the principal (user information) and set it in a threadlocal based SecurityContextHolder 
 * that is internal to Spring Security. This will allow Spring Security to validate what the user is allowed to 
 * do. We are not doing any authorization here, this is only the preparation for authentication. The actual 
 * authentication check is done by Spring Security against the UserDetailsService which we will look at next.
 * 
 * http://www.captechconsulting.com/blog/jens-alm/versioned-validated-and-secured-rest-services-spring-40-3?_ga=1.59506861.1421458840.1423081476
 * 
 * @author markroper
 *
 */
public class HeaderAuthenticationFilter extends GenericFilterBean {
    
    private UserDetailsService userDetailsService;
 
    private HeaderUtil headerUtil;
 
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
 
        UserDetails userDetails = loadUserDetails((HttpServletRequest) request);
        SecurityContext contextBeforeChainExecution = createSecurityContext(userDetails);
 
        try {
            SecurityContextHolder.setContext(contextBeforeChainExecution);
            if (contextBeforeChainExecution.getAuthentication() != null && contextBeforeChainExecution.getAuthentication().isAuthenticated()) {
                String userName = (String) contextBeforeChainExecution.getAuthentication().getPrincipal();
                headerUtil.addHeader((HttpServletResponse) response, userName);
            }
            filterChain.doFilter(request, response);
        }
        finally {
            // Clear the context and free the threadlocal
            SecurityContextHolder.clearContext();
        }
    }
 
    private SecurityContext createSecurityContext(UserDetails userDetails) {
        if (userDetails != null) {
            SecurityContextImpl securityContext = new SecurityContextImpl();
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
            securityContext.setAuthentication(authentication);
            return securityContext;
        }
        return SecurityContextHolder.createEmptyContext();
    }
 
    private UserDetails loadUserDetails(HttpServletRequest request) {
        String userName = headerUtil.getUserName(request);
 
        return userName != null
                ? userDetailsService.loadUserByUsername(userName)
                : null;
    }
    
    public void userDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void headerUtil(HeaderUtil headerUtil) {
        this.headerUtil = headerUtil;
    }
 
}