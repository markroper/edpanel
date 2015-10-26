package com.scholarscore.api.security.config;

import com.scholarscore.api.ApiConsts;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Form login defaults to only support form submissions, not JSON.  We're overriding this behavior to parse
 * JSON username/password submissions as well.  This approach to overriding the form behavior is courtesy of:
 * 
 * {@link http://stackoverflow.com/questions/21474726/how-to-configure-spring-security-3-2-to-use-dao-authentication-and-custom-authen}
 * 
 * @author markroper
 * @see SecurityConfig
 */
public class FormLoginConfigurer<H extends HttpSecurityBuilder<H>> extends AbstractAuthenticationFilterConfigurer<H,FormLoginConfigurer<H>,UsernamePasswordAuthenticationFilter> {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    
    public FormLoginConfigurer() {
        super(new CustomUsernamePasswordAuthenticationFilter(),null);
        usernameParameter(USERNAME);
        passwordParameter(PASSWORD);
    }
    
    public FormLoginConfigurer<H> loginPage(String loginPage) {
        return super.loginPage(loginPage);
    }
    
    public FormLoginConfigurer<H> usernameParameter(String usernameParameter) {
        getAuthenticationFilter().setUsernameParameter(usernameParameter);
        return this;
    }
    
    public FormLoginConfigurer<H> passwordParameter(String passwordParameter) {
        getAuthenticationFilter().setPasswordParameter(passwordParameter);
        return this;
    }
    
    @Override
    public void init(H http) throws Exception {
        super.init(http);
        initDefaultLoginFilter(http);
    }
    
    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(
            String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, ApiConsts.HTTP_POST);
    }
    
    private String getUsernameParameter() {
        return getAuthenticationFilter().getUsernameParameter();
    }
    
    private String getPasswordParameter() {
        return getAuthenticationFilter().getPasswordParameter();
    }
    
    private void initDefaultLoginFilter(H http) {
        DefaultLoginPageGeneratingFilter loginPageGeneratingFilter = http.getSharedObject(DefaultLoginPageGeneratingFilter.class);
        if(loginPageGeneratingFilter != null && !isCustomLoginPage()) {
            loginPageGeneratingFilter.setFormLoginEnabled(true);
            loginPageGeneratingFilter.setUsernameParameter(getUsernameParameter());
            loginPageGeneratingFilter.setPasswordParameter(getPasswordParameter());
            loginPageGeneratingFilter.setLoginPageUrl(getLoginPage());
            loginPageGeneratingFilter.setFailureUrl(getFailureUrl());
            loginPageGeneratingFilter.setAuthenticationUrl(getLoginProcessingUrl());
        }
    }
}
