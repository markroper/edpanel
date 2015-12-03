package com.scholarscore.api.persistence;

/**
 * User: jordan
 * Date: 12/2/15
 * Time: 5:44 PM
 */
public interface AuthInfoPersistence {
    
    public void updatePassword(Long userId, String newPassword);
    
}
