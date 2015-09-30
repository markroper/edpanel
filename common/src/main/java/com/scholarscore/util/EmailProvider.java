package com.scholarscore.util;

/**
 * User: jordan
 * Date: 9/28/15
 * Time: 5:48 PM
 */
public interface EmailProvider {
    
    public void sendEmail(String toAddress, 
                          String subject,
                          String msg);
}
