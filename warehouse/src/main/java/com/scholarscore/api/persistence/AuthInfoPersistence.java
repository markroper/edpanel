package com.scholarscore.api.persistence;

/**
 * User: jordan
 * Date: 12/2/15
 * Time: 5:44 PM
 */
public interface AuthInfoPersistence {
    
    /*
     * Sets the user's one-time password. This one-time password is not as secure
     * as a genuine password, as it can be read out by the school administrators. 
     */
    public void setOneTimePassword(Long userId, String oneTimePassword);
    
    /*
     * Update the user's password to the specified value and 
     * clear any one-time password information if present 
     */
    public void updatePassword(Long userId, String newPassword);
    
}
