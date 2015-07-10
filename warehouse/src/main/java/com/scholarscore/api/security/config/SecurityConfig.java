package com.scholarscore.api.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.scholarscore.api.ApiConsts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Use MVC Security with JDBC Authentication as oppose to static authentication using username/password
 * entries. 
 *
 * The persistence of users is maintained in the JdbcUserDetailsManager class
 * which statically defines its update/create DML statements within the class itself and while it can be
 * extends to override the values for the DML, there appears to be a fair bit of rewriting injection required
 * in order to get an extended instance within the stack.
 *
 * There are comments on stack overflow indicating this is a Jira defect and might be revisited.  Regardless, 
 * what this means is if the DB schema name (scholar_warehouse) isn't declared as part of the datasource connection
 * it will expect that the default connection contains the tables (which it won't without specifying the database 
 * name in the connect string).  Additionally it means that the table names and columns must be the same as expected 
 * via JdbcUserDetailsManager
 *
 * @see org.springframework.security.provisioning.JdbcUserDetailsManager
 *
 * @author mattg
 * @since 2015-01-16
 * 
 * https://github.com/jensalm/spring-rest-server/blob/master/src/main/java/com/captechconsulting/config/SecurityConfig.java
 *
 * @author markroper
 *
 */
@Configuration
@ImportResource("classpath:/dataSource.xml")
@EnableWebMvcSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String USER_ROLE = "USER";
    private static final String ADMIN_ROLE = "ROLE";
    private static final String LOGIN_ENDPOINT = ApiConsts.API_V1_ENDPOINT + "/login";
    private static final String LOGOUT_ENDPOINT = ApiConsts.API_V1_ENDPOINT + "/logout";
    private static final String ACCESS_DENIED_JSON = "{\"message\":\"You are not privileged to request this resource.\","
            + " \"access-denied\":true,\"cause\":\"AUTHORIZATION_FAILURE\"}";
    private static final String UNAUTHORIZED_JSON = "{\"message\":\"Full authentication is required to access this resource.\","
            + " \"access-denied\":true,\"cause\":\"NOT AUTHENTICATED\"}";

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserDetailsService customUserDetailService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .and()
                .userDetailsService(customUserDetailService);
    }

    /**
     * Let’s examine the configure method step by step.
     * 
     *      csrf().disable().
     * 
     * This disables the built in Cross Site Request Forgery support. Needs to be enabled later.
     * 
     *      formLogin().successHandler(successHandler).
     *      loginProcessingUrl("/login").
     *      and().
     *      logout().
     *      logoutSuccessUrl("/logout").
     * 
     * Adds our success handler and maps the login and logout filters to their respective endpoints.
     * 
     *      sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).
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
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        CustomAuthenticationSuccessHandler successHandler = new CustomAuthenticationSuccessHandler();
        FormLoginConfigurer formLogin = new FormLoginConfigurer();
        formLogin.
            successHandler(successHandler).
            loginProcessingUrl(LOGIN_ENDPOINT);
        http.apply(formLogin);
        
        http.
            addFilterBefore(new CustomUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class).
            //Require https:
            requiresChannel().
            anyRequest().
            requiresSecure().
            and().
            portMapper().
            http(80).mapsTo(443).
            http(8085).mapsTo(8443).
            and(). 
            csrf().disable().
            logout().
            logoutSuccessUrl(LOGOUT_ENDPOINT).
            and().
            sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).
            and().
            exceptionHandling().
            accessDeniedHandler(new CustomAccessDeniedHandler()).
            authenticationEntryPoint(new CustomAuthenticationEntryPoint()).
            and().
            authorizeRequests().
            antMatchers(HttpMethod.POST, LOGIN_ENDPOINT).permitAll().
            antMatchers(HttpMethod.POST, LOGOUT_ENDPOINT).authenticated().
            antMatchers(HttpMethod.GET, "/**").hasRole(USER_ROLE).
            antMatchers(HttpMethod.POST, "/**").hasRole(ADMIN_ROLE).
            antMatchers(HttpMethod.DELETE, "/**").hasRole(ADMIN_ROLE).
            antMatchers(HttpMethod.PUT, "/**").hasRole(ADMIN_ROLE).
            antMatchers(HttpMethod.PATCH, "/**").hasRole(ADMIN_ROLE).
            anyRequest().authenticated();
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
            response.setContentType(ApiConsts.APPLICATION_JSON);
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
            response.setContentType(ApiConsts.APPLICATION_JSON);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter out = response.getWriter();
            out.print(UNAUTHORIZED_JSON);
            out.flush();
            out.close();
        }
    }

    /**
     * The authentication success handler is only called when the client successfully authenticates. 
     * In plain language that means when the client logs in. This is here to prevent the Spring default
     * behavior doesn't forward us to the root page of the webapp (swagger).
     * 
     * @author markroper
     *
     */
    private static class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                            Authentication authentication) throws ServletException, IOException {
            PrintWriter out = response.getWriter();
            out.print("");
            out.flush();
            out.close(); 
            clearAuthenticationAttributes(request);
        }
    }
}