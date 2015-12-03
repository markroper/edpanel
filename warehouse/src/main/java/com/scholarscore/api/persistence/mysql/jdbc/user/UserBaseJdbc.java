package com.scholarscore.api.persistence.mysql.jdbc.user;

import com.scholarscore.models.user.User;

import java.security.SecureRandom;
import java.util.Date;

/**
 * Created by markroper on 12/2/15.
 */
public class UserBaseJdbc {
    protected static final int MAX_RETRIES = 15;
    private static final String CHARS = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXYyzZ0123456789";
    private static final int LENGTH = 8;
    private static String generatePassword() {
        SecureRandom rand = new SecureRandom();
        char[] text = new char[LENGTH];
        for (int i = 0; i < LENGTH; i++)
        {
            text[i] = CHARS.charAt(rand.nextInt(CHARS.length()));
        }
        return new String(text);
    }

    /**
     * Controls for the special fields on a User object, making sure that in the create and update cases these
     * values are handled propertly.  The special values are:
     *   oneTimePass
     *   oneTimePassCreated
     *   password
     *   username
     *   enabled
     * <p/>
     * For updating an existing user, never allow a user to change the ID, set a username to null,
     * or change the enabled, oneTimePassCreated, oneTimePass or password values.
     * <p/>
     * For new user creation, never allow a user to be created with a password and always
     * create a oneTimePass, oneTimePassCreated, and auto generate a username if none is provided
     * @param newUser
     * @param previousUser
     */
    protected void transformUserValues(User newUser, User previousUser) {
        if(null != previousUser) {
            //For updating an existing user, never allow a user to change the ID, set a username to null,
            //or change the enabled, oneTimePassCreated, oneTimePass or password values.
            newUser.setId(previousUser.getId());
            //If username is null, set it to the stored value
            if(null == newUser.getUsername()) {
                newUser.setUsername(previousUser.getUsername());
            }
            //Always set password, one time password and enabled to the persisted values
            newUser.setPassword(previousUser.getPassword());
            newUser.setOneTimePass(previousUser.getOneTimePass());
            newUser.setOneTimePassCreated(previousUser.getOneTimePassCreated());
            newUser.setEnabled(previousUser.getEnabled());
        } else {
            //For new user creation, never allow a user to be created with a password and always
            //create a oneTimePass, oneTimePassCreated, and auto generate a username if none is provided
            newUser.setPassword(null);
            //If enabled is true or null, set enabled true and generate a one time password
            if(null == newUser.getEnabled() || newUser.getEnabled()) {
                newUser.setEnabled(true);
            }
            newUser.setOneTimePass(generatePassword());
            newUser.setOneTimePassCreated(new Date());
            if(null == newUser.getUsername()) {
                newUser.setUsername(genUserName(newUser.getName()));
            }
        }
    }

    private static String genUserName(String name) {
        String[] names = name.split(" ");
        return (names[0].charAt(0) + names[names.length - 1]).toLowerCase();
    }
}
