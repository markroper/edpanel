package com.scholarscore.api.persistence;

import java.util.Collection;

import com.scholarscore.models.Identity;
import com.scholarscore.models.User;

/**
 * User management interface - provides basic crud operations on users for both internal persistence
 * as well as spring security persistence
 * 
 * @author mattg
 *
 */
public interface UserPersistence {
    Collection<User> selectAllUsers();

    Identity getIdentity(String username);
    
    User selectUser(String username);

    String createUser(User user);

    String replaceUser(String username, User value);

    String deleteUser(String username);
}
