package com.scholarscore.api.persistence.mysql;

import java.util.Collection;

import com.scholarscore.models.User;

/**
 * User management interface - provides basic crud operations on users for both internal persistence
 * as well as spring security persistence
 * 
 * @author mattg
 *
 */
public interface UserPersistence {
    public Collection<User> selectAllUsers();
    
    public User selectUser(String username);

    public String createUser(User user);

    public String replaceUser(String username, User value);

    public String deleteUser(String username);
}
