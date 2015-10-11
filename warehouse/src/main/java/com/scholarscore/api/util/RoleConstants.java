package com.scholarscore.api.util;

/**
 * The canonical set of constants for supported roles in the system.  These end up as inserted values
 * in the authorities table, which joins to the users table in the database and is used for authentication.
 * 
 * @author markroper
 *
 */
public abstract class RoleConstants {
    public static final String ROLE_MUST_CHANGE_PASSWORD = "MUST_CHANGE_PASSWORD";
            
    public static final String TEACHER = "TEACHER";
    public static final String STUDENT = "STUDENT";
    public static final String ADMINISTRATOR = "ADMINISTRATOR";
    public static final String GUARDIAN = "GUARDIAN";
    public static final String SUPER_ADMINISTRATOR = "SUPER_ADMINISTRATOR";
}
