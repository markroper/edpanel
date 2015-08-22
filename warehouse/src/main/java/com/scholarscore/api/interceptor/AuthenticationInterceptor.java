package com.scholarscore.api.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.scholarscore.api.security.config.SecurityConfig;

@Component
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        SecurityConfig.addCorsHeaders(response);
        return true;
    }
}
