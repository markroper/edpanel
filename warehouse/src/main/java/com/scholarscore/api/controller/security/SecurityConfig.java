package com.scholarscore.api.controller.security;


import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import com.scholarscore.api.persistence.mysql.UserPersistence;

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
 * @see JdbcUserDetailsManager
 * 
 * @author mattg
 * @since 2015-01-16
 */
@Configuration
@ImportResource("classpath:/dataSource.xml")
@EnableWebSecurity(debug=false)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private UserDetailsService customUserDetailService;
		
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth//.inMemoryAuthentication()
        	.jdbcAuthentication()
        	.dataSource(dataSource)
        	.and()
        	.userDetailsService(customUserDetailService);
    }
    
    public void configure(HttpSecurity http) throws Exception {
    	super.configure(http);
    	// at the moment this will cause POST/PATCH failures via swagger and just POST generally - disable csrf
    	// until a csrf token is generated
    	http.csrf().disable();
//    	http
//    		.csrf().disable()
//    		.authorizeRequests().anyRequest().authenticated()
//    		.and()
//    	.formLogin()
//            .loginPage("/spring/index").permitAll()
//            .loginProcessingUrl("/spring/login").permitAll()
//            .usernameParameter("login")
//            .passwordParameter("password")
//            .successHandler(new CustomAuthenticationSuccessHandler())
//            .failureHandler(new CustomAuthenticationFailureHandler())
//            .and()
//        .logout()
//            .logoutUrl("/spring/logout")
//            .logoutSuccessUrl("/spring/index").permitAll();
    		
    }

    //http://stackoverflow.com/questions/22749767/using-jdbcauthentication-in-spring-security-with-hibernate
    /*
    protected void configure(HttpSecurity http) throws Exception {
    	http.csrf().disable()
    		.authorizeRequests()
    			.antMatchers("/swagger/*");
    }
    */
}
