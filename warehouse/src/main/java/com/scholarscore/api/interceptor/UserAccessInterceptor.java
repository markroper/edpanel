package com.scholarscore.api.interceptor;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.annotation.StudentAccessible;
import com.scholarscore.api.security.config.UserDetailsProxy;
import com.scholarscore.api.util.RoleConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Course grained API access by requesting user role is managed in our Spring Security configuration which can
 * be found in @see SecurityConfig.java.  The class managed course grained access control and is called only if the
 * request has already passed through the spring security filter.  The class handles evaluating if a given API
 * endpoint can be accessed by the requesting user, but does deep interrogation of the API path parameters to ensure
 * that student or family requests are only permitted if the resource being requested relates to the user requesting it.
 * this is to prevent a student from accessing the data of another student.
 *
 * The behavior of this class as implemented allows all super admin, admin and teacher users' API
 * requests through that have passed the Spring Security filter, but disallows student API requests unless
 * the API controller method is annotated with @see @StudentAccessible and if the userId path param (if any), is equal
 * to the requesting students ID.
 *
 * TODO: add handling of GUARDIAN role for families, which will be similar but not identical to student
 *
 * Created by markroper on 1/23/16.
 */
public class UserAccessInterceptor extends HandlerInterceptorAdapter {
    final static Logger LOGGER = LoggerFactory.getLogger(UserAccessInterceptor.class);
    private static final String USER_ID_PARAM = "userId";

    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //Always allow OPTIONS requests from browsers
        if(request.getMethod().equals(ApiConsts.HTTP_OPTIONS)) {
            return true;
        }
        UserDetailsProxy udp = getCurrentUserDetails();
        String highestAuthority = null;
        if(null != udp) {
            for (GrantedAuthority ga : udp.getAuthorities()) {
                String curr = ga.getAuthority().replace("ROLE_", "");
                if (RoleConstants.SUPER_ADMINISTRATOR.equals(curr) || RoleConstants.ROLE_MUST_CHANGE_PASSWORD.equals(curr)) {
                    highestAuthority = curr;
                    break;
                } else if (RoleConstants.ADMINISTRATOR.equals(curr)) {
                    highestAuthority = RoleConstants.ADMINISTRATOR;
                } else if (RoleConstants.TEACHER.equals(curr) && !RoleConstants.ADMINISTRATOR.equals(highestAuthority)) {
                    highestAuthority = RoleConstants.TEACHER;
                } else if (RoleConstants.GUARDIAN.equals(curr) &&
                        !RoleConstants.ADMINISTRATOR.equals(highestAuthority) &&
                        !RoleConstants.TEACHER.equals(highestAuthority)) {
                    highestAuthority = RoleConstants.GUARDIAN;
                } else if (RoleConstants.STUDENT.equals(curr) &&
                        !RoleConstants.ADMINISTRATOR.equals(highestAuthority) &&
                        !RoleConstants.TEACHER.equals(highestAuthority) &&
                        !RoleConstants.GUARDIAN.equals(highestAuthority)) {
                    highestAuthority = RoleConstants.STUDENT;
                }
            }
            switch(highestAuthority) {
                case RoleConstants.SUPER_ADMINISTRATOR:
                case RoleConstants.ADMINISTRATOR:
                case RoleConstants.TEACHER:
                case RoleConstants.ROLE_MUST_CHANGE_PASSWORD:
                    return true;
                case RoleConstants.STUDENT:
                case RoleConstants.GUARDIAN:
                    return evaluateMethodAccessibilityForStudentsAndFamilies(request, response, handler, highestAuthority);
                default:
                    LOGGER.error("Unknown user authority found: " + highestAuthority + ". Access denied.");
                    return false;
            }
        } else {
            //these are open endpoints like login, that anyone can hit
            return true;
        }
    }

    /**
     * EdPanel users who are students or families are not permitted to access API endpoints unless those
     * controller methods are annotated with @StudentAccessible.  In this manner, no sensitive endpoints
     * will be exposed without a developer proactively granting permission to that endpoint, limiting the
     * chance of error by omission.
     *
     * If an API controller method is annotated with @StudentAccessible, student requests will be
     * permitted to that endpoint. However, if the API has a userId path parameter, only requests
     * where the userId parameter equals the requesting user's userId will be permitted unless the
     * annotation explicitly declares otherwise as in:
     *
     * @StudentAccessible(userIdParamMustEqualRequestingUserId = false)
     *
     * Additional levels of user type specific handling will have to take place in an ad hoc way within the
     * manager classes.
     *
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @SuppressWarnings("unchecked")
    private boolean evaluateMethodAccessibilityForStudentsAndFamilies(
            HttpServletRequest request, HttpServletResponse response, Object handler, String authority) {
        StudentAccessible sa = ((HandlerMethod) handler).getMethod().getAnnotation(StudentAccessible.class);
        if(null == sa) {
            //The target method is not annotated to permit student access, so return false
            return false;
        } else if(!sa.userIdParamMustEqualRequestingUserId()) {
            //The target method is annotated to permit student access and specifically exempts any userId in the path
            //from having to match the requesting user's id.  Therefore, if the annotation is there and thus configured,
            // always return true.
            return true;
        } else {
            //The method is annotated for access, but we need to resolve whether there is a request
            // param userId, and if there is, that it is equal the requesting student's ID in order to permit
            // the request.
            Map<String, String> pathVariables =
                    (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            Long pathUserId = null;
            String paramName = sa.paramName();
            if(null == paramName) {
                paramName = USER_ID_PARAM;
            }
            if( null != pathVariables) {
                for (Map.Entry<String, String> entry : pathVariables.entrySet()) {
                    if (paramName.equals(entry.getKey())) {
                        try {
                            pathUserId = new Long(entry.getValue());
                        } catch (NumberFormatException e) {
                            //NO OP
                            LOGGER.warn("Unable to parse userId param as Long with param name: "
                                    + paramName + " with value: " + entry.getValue());
                        }
                    }
                }
            }
            if(null == pathUserId) {
                //Thre is no user ID in the path and the student user is permitted, so allow the request
                return true;
            } else {
                UserDetailsProxy p = getCurrentUserDetails();
                return pathUserId.equals(p.getUser().getId());
            }
        }
    }

    private UserDetailsProxy getCurrentUserDetails() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetailsProxy) {
                return (UserDetailsProxy)principal;
            }
        }
        return null;
    }
}
