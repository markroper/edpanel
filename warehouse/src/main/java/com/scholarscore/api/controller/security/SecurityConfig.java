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
@EnableWebSecurity(debug=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	// Example on how to override the UserDao:
	//https://github.com/intrade/inventory/blob/master/src/main/java/com/springapp/mvc/InitApp/SecurityConfig.java
	
	@Autowired
	private DataSource dataSource;
	
	private String usersQuery = "select username, password, enabled from users where username = ?";
	private String authoritiesQuery = "select username, authority from authorities " +
            "where username=?";
		
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth//.inMemoryAuthentication()
        	.jdbcAuthentication()
        	.dataSource(dataSource)
        	.usersByUsernameQuery(getUsersQuery())
        	.authoritiesByUsernameQuery(getAuthoritiesQuery());
        	// create a backup user for administration in case the database is wiped and we can't login
        	// also useful for testing in the event JDBC isn't working - this will create a new record
        	// in the database as a side effect, seems to create duplicate entries for the same user via
        	// this approach
        	
//        	.withUser("mroper").password("admin").roles("STUDENT", "TEACHER", "ADMIN")
//        	.and()
//        	.withUser("mattg").password("admin").roles("STUDENT", "TEACHER", "ADMIN");
    }

    //http://stackoverflow.com/questions/22749767/using-jdbcauthentication-in-spring-security-with-hibernate
    /*
    protected void configure(HttpSecurity http) throws Exception {
    	http.csrf().disable()
    		.authorizeRequests()
    			.antMatchers("/swagger/*");
    }
    */

	private String getAuthoritiesQuery() {
		return authoritiesQuery;
	}

	private String getUsersQuery() {
		return usersQuery;
	}
}
