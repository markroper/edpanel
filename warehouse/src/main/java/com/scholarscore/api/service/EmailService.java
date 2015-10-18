package com.scholarscore.api.service;

/**
 * User: jordan
 * Date: 9/28/15
 * Time: 5:48 PM
 */
public interface EmailService {
    
    public void sendMessage(String toAddress,
                            String subject,
                            String msg);
}
