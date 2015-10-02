package com.scholarscore.api.manager;

import java.util.Collection;

import com.scholarscore.api.security.config.UserDetailsProxy;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.models.user.User;

/**
 * Allows for the creation of users
 * 
 * @author mattg
 */
public interface UserManager {
    ServiceResponse<Collection<User>> getAllUsers();

    StatusCode userExists(Long userId);
    
    ServiceResponse<User> getUser(Long userId);

    ServiceResponse<Long> createUser(User value);

    ServiceResponse<Long> replaceUser(Long userId, User user);
    
    ServiceResponse<Long> updateUser(Long userId, User user);

    ServiceResponse<Long> deleteUser(Long userId);

    ServiceResponse<User> getCurrentUser();
    
    ServiceResponse<String> startPhoneContactValidation(Long userId);
    
    ServiceResponse<String> completePhoneContactValidation(Long userId, String code);
    
    ServiceResponse<String> startEmailContactValidation(Long userId);
    
    ServiceResponse<String> completeEmailContactValidation(Long userId, String code);
}
