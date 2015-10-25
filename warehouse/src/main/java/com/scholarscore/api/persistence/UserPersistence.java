package com.scholarscore.api.persistence;

import com.scholarscore.models.user.User;

import java.util.Collection;

/**
 * User management interface - provides basic crud operations on users for both internal persistence
 * as well as spring security persistence
 * 
 * @author mattg
 *
 */
public interface UserPersistence {
    Collection<User> selectAllUsers();
    
    Collection<User> selectAllUsersInSchool(Long schoolId);
    
    Collection<User> selectAllUsersInSchool(Long schoolId, boolean enabled);
    
    User selectUser(Long userId);

    Long createUser(User user);

    Long replaceUser(Long userId, User value);

    Long deleteUser(Long userId);

    User selectUserByName(String username);
}
