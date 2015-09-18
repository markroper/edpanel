package com.scholarscore.api.persistence;

import com.scholarscore.api.persistence.mysql.UserPersistence;
import com.scholarscore.api.security.config.UserDetailsProxy;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.Identity;
import com.scholarscore.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

/**
 * Created by cwallace on 9/16/2015.
 */
public class UserManagerImpl implements UserManager {

    private UserPersistence userPersistence;

    private PersistenceManager pm;

    private static final String USER = "user";

    public void setUserPersistence(UserPersistence userPersistence) {
        this.userPersistence = userPersistence;
    }

    public void setPm(PersistenceManager pm) {
        this.pm = pm;
    }

    @Override
    public ServiceResponse<Collection<User>> getAllUsers() {
        return new ServiceResponse<Collection<User>>(
                userPersistence.selectAllUsers());
    }

    @Override
    public StatusCode userExists(String username) {
        User user = userPersistence.selectUser(username);
        if(null == user) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{USER, username});
        };
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    @Override
    public ServiceResponse<User> getUser(String username) {
        User user = userPersistence.selectUser(username);
        if (null != user) {
            return new ServiceResponse<User>(user);
        }
        return new ServiceResponse<User>(StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[] { USER, username } ));
    }

    @Override
    public ServiceResponse<String> createUser(User value) {
        return new ServiceResponse<String>(userPersistence.createUser(value));
    }

    @Override
    public ServiceResponse<String> replaceUser(String username, User user) {
        return new ServiceResponse<String>(userPersistence.replaceUser(username, user));
    }

    @Override
    public ServiceResponse<String> updateUser(String username, User user) {
        return new ServiceResponse<String>(userPersistence.replaceUser(username, user));
    }

    @Override
    public ServiceResponse<String> deleteUser(String username) {
        return new ServiceResponse<String>(userPersistence.deleteUser(username));
    }

    @Override
    public ServiceResponse<UserDetailsProxy> getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetailsProxy) {
                UserDetailsProxy proxy = (UserDetailsProxy)principal;
                return new ServiceResponse<UserDetailsProxy>(proxy);
            }
        }
        return new ServiceResponse<UserDetailsProxy>(new StatusCode(StatusCodes.NOT_AUTHENTICATED,
                "Not Authenticated"));
    }
}
