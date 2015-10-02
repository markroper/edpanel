package com.scholarscore.api;

import com.scholarscore.api.controller.base.IntegrationBase;
import org.testng.annotations.Test;

/**
 * User: jordan
 * Date: 10/1/15
 * Time: 1:37 PM
 */
public class UserRolesTest extends IntegrationBase {
    
    @Test
    public void testUserRoles() { 
        // positive anon - do something that anyone can do (permissed as anon)
        // negative anon - do something that anon is not allowed to do and confirm they can't
        
        invalidateCookie();
        
        // positive user
        // negative user
        authenticate("user", "user");
        invalidateCookie();
        
        // positive admin
        // negative admin
        authenticate("mroper", "admin");
        invalidateCookie();
        
    }
}
