package com.scholarscore.api.util;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * User: jordan
 * Date: 11/23/15
 * Time: 12:13 AM
 * 
 * This file likely temporary, at least in its current form. 
 * The password should be hashed before insertion into the database.
 * Today, insertion of new users and setting their passwords is not as 
 * managed as it should be -- currently passwords are posted like any other data field. 
 * This method must be used on passwords being stored.
 */
public class PasswordUtil {
    
    // use the currently-configured password hashing algorithm to produce a password hash
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }
    
}
