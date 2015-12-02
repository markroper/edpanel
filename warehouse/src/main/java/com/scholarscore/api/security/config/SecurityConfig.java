package com.scholarscore.api.security.config;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.persistence.AdministratorPersistence;
import com.scholarscore.api.persistence.StudentPersistence;
import com.scholarscore.api.persistence.TeacherPersistence;
import com.scholarscore.api.util.RoleConstants;
import com.scholarscore.util.EdPanelObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

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
@ImportResource({"classpath:/userauth.xml"})
@EnableWebMvcSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String OPTIONS_VERB = "OPTIONS";
    private static final String LOGIN_ENDPOINT = ApiConsts.API_V1_ENDPOINT + "/login";
    private static final String QUERY_ENDPOINT = ApiConsts.API_V1_ENDPOINT + "/schools/*/queries/results";
    private static final String LOGOUT_ENDPOINT = ApiConsts.API_V1_ENDPOINT + "/logout";
    private static final String CONFIRM_EMAIL_ENDPOINT = ApiConsts.API_V1_ENDPOINT + "/users/*/validation/email/*";
    private static final String CONFIRM_PHONE_ENDPOINT = ApiConsts.API_V1_ENDPOINT + "/users/*/validation/phone/*";
    private static final String INITIATE_CHANGE_PASSWORD_ENDPOINT = ApiConsts.API_V1_ENDPOINT + "/users/requestPasswordReset/*";
    private static final String CHANGE_PASSWORD_ENDPOINT = ApiConsts.API_V1_ENDPOINT + "/users/passwordReset/*/*";
    private static final String ACCESS_DENIED_JSON = "{\"message\":\"You are not privileged to request this resource.\","
            + " \"access-denied\":true,\"cause\":\"AUTHORIZATION_FAILURE\"}";
    private static final String UNAUTHORIZED_JSON = "{\"message\":\"Authentication is required to access this resource.\","
            + " \"access-denied\":true,\"cause\":\"NOT AUTHENTICATED\"}";
    private static final String INVALID_CREDENTIALS_JSON = "{\"error\":\"Invalid credentials supplied\"}";
    
    // err on the side of caution
    private static final int BCRYPT_STRENGTH = 12;

    /**
     * Adds CORS headers to the HTTP response provided.
     * 
     * @param response
     */
    public static void addCorsHeaders(HttpServletResponse response) {
        //TODO: move the allow-origin host to a spring injected config file
        response.setHeader("Access-Control-Allow-Origin", "https://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PATCH, PUT");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
    
    @Autowired
    private DataSource dataSource;

    @Autowired
    private StudentPersistence studentPersistence;

    @Autowired
    private TeacherPersistence teacherPersistence;

    @Autowired
    private AdministratorPersistence administratorPersistence;

    @Autowired
    private UserDetailsService customUserDetailService;

    @Autowired
    private OneTimePassAuthProvider oneTimePassAuthProvider;
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());  // regular password check, using bcrypt hash
        auth.authenticationProvider(oneTimePassAuthProvider);   // check user's password against any saved one-time password
    }
    
    @Bean(name="authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(customUserDetailService);
        return authenticationProvider;
    }
    
    @Override
    protected UserDetailsService userDetailsService() { 
        return customUserDetailService;
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
        http.formLogin();
        CustomAuthenticationSuccessHandler successHandler = new CustomAuthenticationSuccessHandler();
        CustomAuthenticationFailureHandler failureHandler = new CustomAuthenticationFailureHandler();
        FormLoginConfigurer formLogin = new FormLoginConfigurer();
        formLogin.
            successHandler(successHandler).
            failureHandler(failureHandler).
            loginProcessingUrl(LOGIN_ENDPOINT);
        http.apply(formLogin);
        
        http.
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
            addLogoutHandler(new CustomLogoutHandler()).
            logoutUrl(LOGOUT_ENDPOINT).
            logoutSuccessHandler(new CustomLogoutSuccessHandler()).
            and().
            sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).
            and().
            exceptionHandling().
            accessDeniedHandler(new CustomAccessDeniedHandler()).
            authenticationEntryPoint(new CustomAuthenticationEntryPoint()).
            and().
            authorizeRequests().
            antMatchers(HttpMethod.POST, LOGIN_ENDPOINT).permitAll().
            antMatchers(HttpMethod.OPTIONS, LOGIN_ENDPOINT).permitAll().
            antMatchers(HttpMethod.OPTIONS, "/**").permitAll().
            antMatchers(HttpMethod.GET, CONFIRM_EMAIL_ENDPOINT).permitAll().
            antMatchers(HttpMethod.GET, CONFIRM_PHONE_ENDPOINT).permitAll().
            antMatchers(HttpMethod.GET, INITIATE_CHANGE_PASSWORD_ENDPOINT).permitAll().
            antMatchers(HttpMethod.PUT, CHANGE_PASSWORD_ENDPOINT).hasAnyRole(append(AUTHENTICATED, RoleConstants.ROLE_MUST_CHANGE_PASSWORD)).
            antMatchers(HttpMethod.POST, LOGOUT_ENDPOINT).hasAnyRole(AUTHENTICATED).
            antMatchers(HttpMethod.GET, "/**").hasAnyRole(AUTHENTICATED).
            antMatchers(HttpMethod.POST, QUERY_ENDPOINT).hasAnyRole(AUTHENTICATED).
            antMatchers(HttpMethod.POST, "/**").hasRole(RoleConstants.ADMINISTRATOR).
            antMatchers(HttpMethod.DELETE, "/**").hasRole(RoleConstants.ADMINISTRATOR).
            antMatchers(HttpMethod.PUT, "/**").hasRole(RoleConstants.ADMINISTRATOR).
            antMatchers(HttpMethod.PATCH, "/**").hasRole(RoleConstants.ADMINISTRATOR).
            anyRequest().denyAll();
    }
    
    // use this instead of 'authenticated()' to exclude special-purpose roles
    // like ROLE_MUST_CHANGE_PASSWORD
    private static final String[] AUTHENTICATED = { 
            RoleConstants.ADMINISTRATOR,
            RoleConstants.TEACHER,
            RoleConstants.STUDENT,
            RoleConstants.GUARDIAN,
            RoleConstants.SUPER_ADMINISTRATOR };

    private static <T> T[] append(T[] arr, T lastElement) {
        final int N = arr.length;
        arr = java.util.Arrays.copyOf(arr, N+1);
        arr[N] = lastElement;
        return arr;
    }

    private static class CustomLogoutHandler implements LogoutHandler {

        @Override
        public void logout(HttpServletRequest request,
                HttpServletResponse response, Authentication authentication) {
            addCorsHeaders(response);
            response.setStatus(HttpServletResponse.SC_OK); 
        }
        
    }
    private static class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

        @Override
        public void onLogoutSuccess(HttpServletRequest request,
                HttpServletResponse response, Authentication authentication)
                throws IOException, ServletException {
            response.setStatus(HttpServletResponse.SC_OK);   
        }
        
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
            SecurityConfig.addCorsHeaders(response);
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
            String responseBody = "";
            SecurityConfig.addCorsHeaders(response);
            if(request.getMethod().equals(OPTIONS_VERB)) {
                response.setContentType(ApiConsts.APPLICATION_JSON);
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setContentType(ApiConsts.APPLICATION_JSON);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                responseBody = UNAUTHORIZED_JSON;
            }
            PrintWriter out = response.getWriter();
            out.print(responseBody);
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
            SecurityConfig.addCorsHeaders(response);
            PrintWriter out = response.getWriter();

            // This mode should be the only active mode at this point, but in the event the other
            // modes of operation (returning an identity, or returning a User) still function based
            // on the builder pattern established in the authentication token then we'll handle those
            // cases and emit the appropriate JSON there as well.
            if (authentication.getPrincipal() instanceof UserDetailsProxy) {
                UserDetailsProxy proxyUser = (UserDetailsProxy)authentication.getPrincipal();
                com.scholarscore.models.user.User user = proxyUser.getUser();
                // don't include any of these user details
                user.setPassword(null);
                user.setOneTimePass(null);
                user.setOneTimePassCreated(null);
                String value = EdPanelObjectMapper.MAPPER.writeValueAsString(user);
                out.print(value);
            } else if (authentication.getPrincipal() instanceof User) {
                User principal = (User)authentication.getPrincipal();
                out.print(EdPanelObjectMapper.MAPPER.writeValueAsString(principal));
            } else {
                logger.error("authentication.getPrincipal() is not instanceof UserDetailsProxy or User");
                throw new ClassCastException("authentication.getPrincipal() is not instanceof UserDetailsProxy or User");
            }
            out.flush();
            out.close(); 
            clearAuthenticationAttributes(request);
        }
    }
    
    private static class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

        @Override
        public void onAuthenticationFailure(HttpServletRequest request,
                HttpServletResponse response, AuthenticationException exception)
                throws IOException, ServletException {
            SecurityConfig.addCorsHeaders(response);
            PrintWriter out = response.getWriter();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(INVALID_CREDENTIALS_JSON);
            out.flush();
            out.close();
        }
        
    }
}
