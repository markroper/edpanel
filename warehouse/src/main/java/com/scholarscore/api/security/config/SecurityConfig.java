package com.scholarscore.api.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.scholarscore.api.security.HeaderAuthenticationFilter;
import com.scholarscore.api.security.HeaderUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;

/**
 * https://github.com/jensalm/spring-rest-server/blob/master/src/main/java/com/captechconsulting/config/SecurityConfig.java
 * 
 * @author markroper
 *
 */
@Configuration
@EnableWebMvcSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String ACCESS_DENIED_JSON = "{\"message\":\"You are not privileged to request this resource.\","
            + " \"access-denied\":true,\"cause\":\"AUTHORIZATION_FAILURE\"}";
    private static final String UNAUTHORIZED_JSON = "{\"message\":\"Full authentication is required to access this resource.\","
            + " \"access-denied\":true,\"cause\":\"NOT AUTHENTICATED\"}";

    @Autowired
    private HeaderUtil headerUtil;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().
                withUser("user").password("password").roles("USER").
                and().
                withUser("admin").password("password").roles("USER", "ADMIN");
    }

    /**
     * Let’s examine the configure method step by step.
     *
     *      addFilterBefore(authenticationFilter(), LogoutFilter.class)
     * 
     * This adds our new HeaderAuthenticationFilter to the chain of filters that process each request 
     * to the application. We add it before the LogoutFilter so it will be the first filter in the chain.
     * 
     *      csrf().disable().
     * 
     * This disables the built in Cross Site Request Forgery support. This is used in a html login form 
     * but since we don’t have that we need to disable this support.
     * 
     *      formLogin().successHandler(successHandler).
     *      loginProcessingUrl("/login").
     *      and().
     *      logout().
     *      logoutSuccessUrl("/logout").
     * 
     * Adds our success handler and maps the login and logout filters to their respective endpoints.
     * 
     *      sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
     * 
     * This tells the application not to create sessions in keeping with our stateless application.
     * 
     *      exceptionHandling().
     *          accessDeniedHandler(new CustomAccessDeniedHandler()).
     *          authenticationEntryPoint(new CustomAuthenticationEntryPoint()).
     * 
     * This adds our custom handlers for authentication and authorization.
     * 
     *       authorizeRequests().
     *            antMatchers(HttpMethod.POST, "/login").permitAll().
     *            antMatchers(HttpMethod.POST, "/logout").authenticated().
     *            antMatchers(HttpMethod.GET, "/**").hasRole("USER").
     *            antMatchers(HttpMethod.POST, "/**").hasRole("ADMIN").
     *            antMatchers(HttpMethod.PUT, "/**").hasRole("ADMIN").
     *            antMatchers(HttpMethod.PATCH, "/**").hasRole("ADMIN").
     *            antMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN").
     *            anyRequest().authenticated();
     *        
     * This is the heart of our security. This decides what requests should require what role. 
     * In our example we are letting any POST request to “/login” through by using the permitAll().
     * For the “/logout” we require that the request is correctly authenticated but we don’t care 
     * what role the user has. For all our other matchers we use wildcards but specify the request 
     * method. As a user you only get view permissions using GET and as admin you get all the update 
     * methods POST, PUT, PATCH and DELETE.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthenticationSuccessHandler successHandler = new CustomAuthenticationSuccessHandler();
        successHandler.headerUtil(headerUtil);
        http.
            addFilterBefore(authenticationFilter(), LogoutFilter.class).
            csrf().disable().
            formLogin().successHandler(successHandler).
            loginProcessingUrl("/api/v1/login").
            and().
            logout().
            logoutSuccessUrl("/api/v1/logout").
            and().
            sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
            and().
            exceptionHandling().
            accessDeniedHandler(new CustomAccessDeniedHandler()).
            authenticationEntryPoint(new CustomAuthenticationEntryPoint()).
            and().
            authorizeRequests().
            antMatchers(HttpMethod.POST, "/api/v1/login").permitAll().
            antMatchers(HttpMethod.POST, "/api/v1/logout").authenticated().
            antMatchers(HttpMethod.GET, "/**").hasRole("USER").
            antMatchers(HttpMethod.POST, "/**").hasRole("ADMIN").
            antMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN").
            antMatchers(HttpMethod.PUT, "/**").hasRole("ADMIN").
            antMatchers(HttpMethod.PATCH, "/**").hasRole("ADMIN").
            anyRequest().authenticated();
    }

    private Filter authenticationFilter() {
        HeaderAuthenticationFilter headerAuthenticationFilter = new HeaderAuthenticationFilter();
        headerAuthenticationFilter.userDetailsService(userDetailsService());
        headerAuthenticationFilter.headerUtil(headerUtil);
        return headerAuthenticationFilter;
    }

    /**
     * The access denied handler is called when authorization fails. This means the client is passing in a correct token but the permissions 
     * associated with the role of this user does not allow the client the access.
     * @author markroper
     *
     */
    private static class CustomAccessDeniedHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, 
                HttpServletResponse response, 
                AccessDeniedException accessDeniedException) throws IOException, ServletException {
            response.setContentType("applicaiton/json");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            PrintWriter out = response.getWriter();
            out.print(ACCESS_DENIED_JSON);
            out.flush();
            out.close();

        }
    }

    /**
     * The authentication entry point is called when the client makes a call to a resource 
     * without proper authentication. In other words, the client has not logged in.
     * 
     * In the default Spring Security setup the request would be redirected to a login page of some sort. 
     * In a REST application we want to change that to just return a status of 401 (Unauthorized) and 
     * a static JSON message explaining it.
     * 
     * @author markroper
     *
     */
    private static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, 
                HttpServletResponse response, 
                AuthenticationException authException) throws IOException, ServletException {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter out = response.getWriter();
            out.print(UNAUTHORIZED_JSON);
            out.flush();
            out.close();
        }
    }

    /**
     * The authentication success handler is only called when the client successfully authenticates. 
     * In plain language that means when the client logs in.
     * 
     * @author markroper
     *
     */
    private static class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

        private HeaderUtil headerUtil;

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                            Authentication authentication) throws ServletException, IOException {
            try {
                String token = headerUtil.createAuthToken(((User) authentication.getPrincipal()).getUsername());
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode node = mapper.createObjectNode().put("token", token);
                PrintWriter out = response.getWriter();
                out.print(node.toString());
                out.flush();
                out.close();
            } catch (GeneralSecurityException e) {
                throw new ServletException("Unable to create the auth token", e);
            }
            clearAuthenticationAttributes(request);
        }

        private void headerUtil(HeaderUtil headerUtil) {
            this.headerUtil = headerUtil;
        }
    }

}
