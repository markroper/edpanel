package com.scholarscore.api.persistence;

import java.util.Collection;

import com.scholarscore.models.user.User;

/**
 * User management interface - provides basic crud operations on users for both internal persistence
 * as well as spring security persistence
 * 
 * @author mattg
 *
 */
public interface UserPersistence {
    Collection<User> selectAllUsers();
    
    User selectUser(Long userId);

    Long createUser(User user);

    Long replaceUser(Long userId, User value);

    Long deleteUser(Long userId);

    User selectUserByName(String username);
}
