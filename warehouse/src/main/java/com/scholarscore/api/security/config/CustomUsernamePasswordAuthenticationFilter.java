package com.scholarscore.api.security.config;

import java.io.BufferedReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholarscore.models.LoginRequest;

/**
 * The default spring security username and password authentication filter expects the HTTP request to be
 * of content type APPLICATION_FORM_URL_ENCODED. We are fine with this, but also want to support application/json
 * authentication requests, since our API is all REST and JSON driven. This class overrides the Spring Security
 * default authentication filter implementation.
 * 
 * @author markroper
 *
 */
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final String HTTP_VERB = "POST";
    private static final ObjectMapper mapper = new ObjectMapper();
    private LoginRequest resolveLoginRequest(HttpServletRequest req) {
        LoginRequest loginRequest = null;
        try {
            //HttpServletRequest can be read only once
            StringBuffer sb = new StringBuffer();
            String line = null;

            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null){
                sb.append(line);
            }
            loginRequest = mapper.readValue(sb.toString(), LoginRequest.class);
        } catch (Exception e) {
        }
        return loginRequest;
    }
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response){
        if (!request.getMethod().equals(HTTP_VERB)) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        LoginRequest loginRequest = this.resolveLoginRequest(request);
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword());

        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
