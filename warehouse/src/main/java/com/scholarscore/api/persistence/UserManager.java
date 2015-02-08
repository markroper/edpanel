package com.scholarscore.api.persistence;

import java.util.Collection;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.models.User;

/**
 * Allows for the creation of users
 * 
 * @author mattg
 */
public interface UserManager {
    public ServiceResponse<Collection<User>> getAllUsers();

    public StatusCode userExists(String username);
    
    public ServiceResponse<User> getUser(String username);

    public ServiceResponse<String> createUser(User value);

    public ServiceResponse<String> replaceUser(String username, User user);
    
    public ServiceResponse<String> updateUser(String username, User user);

    public ServiceResponse<String> deleteUser(String username);
}
