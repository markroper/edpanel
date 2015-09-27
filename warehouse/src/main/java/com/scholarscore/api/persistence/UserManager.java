package com.scholarscore.api.persistence;

import java.util.Collection;

import com.scholarscore.api.security.config.UserDetailsProxy;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.models.Identity;
import com.scholarscore.models.User;

/**
 * Allows for the creation of users
 * 
 * @author mattg
 */
public interface UserManager {
    ServiceResponse<Collection<User>> getAllUsers();

    StatusCode userExists(String username);
    
    ServiceResponse<User> getUser(String username);

    ServiceResponse<String> createUser(User value);

    ServiceResponse<String> replaceUser(String username, User user);
    
    ServiceResponse<String> updateUser(String username, User user);

    ServiceResponse<String> deleteUser(String username);

    ServiceResponse<UserDetailsProxy> getCurrentUser();
    
    ServiceResponse<String> startPhoneContactValidation(String username);
    
    ServiceResponse<String> completePhoneContactValidation(String username, String code);
    
    ServiceResponse<String> startEmailContactValidation(String username);
    
    ServiceResponse<String> completeEmailContactValidation(String username, String code);
}
