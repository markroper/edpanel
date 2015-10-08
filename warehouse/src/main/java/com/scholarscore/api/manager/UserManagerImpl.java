package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.UserPersistence;
import com.scholarscore.api.security.config.UserDetailsProxy;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.user.ContactMethod;
import com.scholarscore.models.user.ContactType;
import com.scholarscore.models.user.User;

import com.scholarscore.api.service.EmailService;
import com.scholarscore.api.service.TextMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.constraints.Null;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * Created by cwallace on 9/16/2015.
 */
public class UserManagerImpl implements UserManager {

    
    
    final static Logger logger = LoggerFactory.getLogger(UserManagerImpl.class);
    
    private static boolean IGNORE_CASE_ON_CONFIRM_CONTACT = true;
    private static boolean IGNORE_CASE_ON_ONE_TIME_PASSWORD = false;
    
    private UserPersistence userPersistence;

    @Autowired
    private EmailService emailService;
    @Autowired
    private TextMessageService textService;

    @Autowired
    protected AuthenticationManager authenticationManager;
    
    private OrchestrationManager pm;

    private static final String USER = "user";

    private SecureRandom random = new SecureRandom();

    public void setUserPersistence(UserPersistence userPersistence) {
        this.userPersistence = userPersistence;
    }

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }

    @Override
    public ServiceResponse<Collection<User>> getAllUsers() {
        return new ServiceResponse<Collection<User>>(
                userPersistence.selectAllUsers());
    }

    @Override
    public ServiceResponse<Collection<User>> getAllUsersInSchool(Long schoolId) {
        return new ServiceResponse<Collection<User>>(
                userPersistence.selectAllUsersInSchool(schoolId));
    }

    @Override
    public ServiceResponse<Collection<User>> getAllUsersInSchool(Long schoolId,
            boolean enabled) {
        return new ServiceResponse<Collection<User>>(
                userPersistence.selectAllUsersInSchool(schoolId, enabled));
    }
    
    @Override
    public StatusCode userExists(Long userId) {
        User user = userPersistence.selectUser(userId);
        if(null == user) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{USER, userId});
        };
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }

    @Override
    public ServiceResponse<User> getUser(Long userId) {
        User user = userPersistence.selectUser(userId);
        if (null != user) {
            return new ServiceResponse<User>(user);
        }
        return new ServiceResponse<User>(StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{USER, userId}));
    }

    @Override
    public ServiceResponse<Long> createUser(User value) {
        return new ServiceResponse<Long>(userPersistence.createUser(value));
    }

    @Override
    public ServiceResponse<Long> replaceUser(Long userId, User user) {
        return new ServiceResponse<Long>(userPersistence.replaceUser(userId, user));
    }

    @Override
    public ServiceResponse<Long> updateUser(Long userId, User user) {
        return new ServiceResponse<Long>(userPersistence.replaceUser(userId, user));
    }

    @Override
    public ServiceResponse<Long> deleteUser(Long userId) {
        return new ServiceResponse<>(userPersistence.deleteUser(userId));
    }

    @Override
    public ServiceResponse<User> getCurrentUser() {
        UserDetailsProxy proxy = getCurrentUserDetails();
        if (proxy != null) {
            return new ServiceResponse<>(proxy.getUser());
        }
        return new ServiceResponse<>(new StatusCode(StatusCodes.NOT_AUTHENTICATED,
                "{\"error\": \"Not Authenticated\"}"));
    }

    private UserDetailsProxy getCurrentUserDetails() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetailsProxy) {
                return (UserDetailsProxy)principal;
            }
        }
        return null;
    }

    @Override
    public ServiceResponse<String> startContactValidation(Long userId, ContactType contactType) {
        // TODO Jordan: test!!
        // TODO Jordan: clean up these returns, right now they return mashed-up messages
        User user = userPersistence.selectUser(userId);
        if (null == user) {
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{USER, userId}));
        }
        ContactMethod selectedContactMethod = getContactMethod(user.getContactMethods(), contactType);
        if (selectedContactMethod == null) {
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{"contact method with type not found", contactType}));
        } else if (selectedContactMethod.confirmed()) {
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.OK, new Object[]{"contact of type " + contactType + " already confirmed."}));
        }
        
        // OK, we have everything we need to proceed with contact validation. Now, we need to....
        // ... generate a NEW code and store it in the DB along with a timestamp
        String code = generateCode();
        Date codeCreated = new Date();
        selectedContactMethod.setConfirmCode(code);
        selectedContactMethod.setConfirmCodeCreated(codeCreated);
        updateUser(user.getId(), user);

        // TODO: this could be unified into a generic messaging service that encapsulates the differences 
        // of different contact mediums and messages... too many if/else statements on an Enum type like this 
        // become easy to overlook later when new enum values are added 
        if (ContactType.EMAIL.equals(selectedContactMethod.getContactType())) {
            String toAddress = selectedContactMethod.getContactValue();
            String subject = "(DEV) email confirmation from EdPanel";
            String message = "Hello! Please enter this code when prompted by edpanel: ( " + code + " ). "
                    + "\n -- OR --"
                    + "\n click here to confirm: https://myedpanel.com/warehouse/v1/" + user.getUsername() + "/validation/email/" + code + "";
            emailService.sendMessage(toAddress, subject, message);
        } else if (ContactType.PHONE.equals(selectedContactMethod.getContactType())) {
            String toNumber = selectedContactMethod.getContactValue();
            String msg = "Confirm code from EdPanel: " + code;
            textService.sendMessage(toNumber, msg);
        } else {
            // this sucks - see above 
            logger.error("ERROR! Unknown enum type seen in switch statement in UserManagerImpl");
            throw new UnsupportedOperationException("Cannot start contact validation for unknown contact type: " + contactType);
        }
        
        return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.OK, new Object[] {"email has been sent"}));
    }

    private @Null ContactMethod getContactMethod(@Null Set<ContactMethod> contactMethods, @Null ContactType contactType) {
        if (contactMethods == null || contactMethods.size() <= 0 || contactType == null) { return null; } 
        for (ContactMethod method : contactMethods) {
            if (contactType.name().equalsIgnoreCase(method.getContactValue())) {
                return method;
            }
        }
        return null;
    }

    @Override
    public ServiceResponse<String> confirmContactValidation(Long userId, ContactType contactType, String providedCode) {
        User user = userPersistence.selectUser(userId);
        // this endpoint may not require authentication, so return generic errors so it cannot be used to glean information about existing users or validations
        ServiceResponse<String> genericError =  
                new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{"oops... something went wrong."}));
        if (null == user) { return genericError; }
        ContactMethod selectedContactMethod = getContactMethod(user.getContactMethods(), contactType);
        if (selectedContactMethod == null || selectedContactMethod.getConfirmCode() == null) {
            return genericError;
        }

        String confirmCode = selectedContactMethod.getConfirmCode();
        
        boolean codeMatched = IGNORE_CASE_ON_CONFIRM_CONTACT ? confirmCode.equalsIgnoreCase(providedCode) : confirmCode.equals(providedCode);
        if (codeMatched) {
            if (selectedContactMethod.confirmed()) {
                logger.warn("User somehow has valid confirm code for an already-confirmed contact. Clearing these values...");
            } else {
                selectedContactMethod.setConfirmed(true);
            }
            selectedContactMethod.setConfirmCodeCreated(null);
            selectedContactMethod.setConfirmCode(null);
            updateUser(user.getId(), user);
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.OK, new Object[]{"validation successful!"}));
        } else {
            return genericError;
        }
    }
    
   /* Initiates a password reset for a particular user. This method is intended to be invoked without  
    * authentication and thus it does not return any information regarding if a username exists and, if it does, 
    * if the newly-generated one-time password has been sent to a user's contact method (email, text) or delivered
    * to a school administrator for manual delivery.
    * 
    * Generates a one-time password and saves it in the database, then either (a) delivers it to the user, if the user
    * has a validated contact method on file, or if not (b) alerts the school administrator that a user needs
    * a one-time password delivered to them.
    */
    @Override
    public ServiceResponse<String> startPasswordReset(String username) {
        User user = userPersistence.selectUserByName(username);
        if (null == user) {
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{USER, username}));
        }
        
        String code = generateCode();
        Date codeCreated = new Date();
        user.setOneTimePass(code);
        user.setOneTimePassCreated(codeCreated);
        updateUser(user.getId(), user);

        boolean passwordSent = false;
        // if we can find a validated/confirmed contact, send OTP there.
        if (user.getContactMethods() != null) { 
            for (ContactMethod method : user.getContactMethods()) {
                if (method.confirmed()) {
                    // TODO Jordan: (Reset Password) send password via confirmed method here;
                    passwordSent = true;
                    break;
                }
            }
        }
        
        if (!passwordSent) {
            // if no validated contact, notify admin 
            // -- NOTE: notifying admins in this situation is blocked until we implement notifications at all.
            // but admins will see the OTP in their list of 'users needing passwords' since the user doesn't have a valid contact.
            logger.info("Username " + user.getUsername() + " requested password reset but has no valid contact."
                    + "\n The admin will now see this user's one-time password on their administration list.");
        }

        // since this is an unauthenticated endpoint,
        // return no data so as to not reveal anything about which usernames are valid
        return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.OK, new Object[]{"No Data"}));
    }

    // TODO Jordan: don't take password in the URL, take it in the payload
    
    /* Resets the user's password.
     */
    @Override
    public ServiceResponse<String> resetPassword(Long userId, String newPassword) {
        User user = userPersistence.selectUser(userId);
        if (null == user) {
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{USER, userId}));
        }
        // TODO Jordan: right here we need more than just "logged-in person is user vs admin" level permissions
        // we need to be able to say, the userId we are resetting the password for is the logged in user
        // for now, assume this comment will be replaced with a block that returns a service response if the 
        // user logged in is not the userId that the password reset is for. maybe just take no id?

        // always clear onetime password + creation date when password is reset
        user.setOneTimePass(null);
        user.setOneTimePassCreated(null);
        user.setPassword(newPassword);
        updateUser(user.getId(), user);
        
        // TODO Jordan: right here, if the user is logged in with temporary password (ROLE_ONLY_CHANGE_PASSWORD) 
        // should switch them to  login with their real password
         Authentication authRequest = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        Authentication authenticated = authenticationManager.authenticate(authRequest);
        if (authenticated.isAuthenticated()) {
            logger.info("!! !! !! Oh hell yes. Reauthenticated user after password change");
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authenticated);
        } else {
            // oh boy...
            throw new RuntimeException("Failed to re-authenticate user after change of password");
        }
        
        return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.OK, new Object[]{"Password successfully reset"}));
    }
    
    private String generateCode() { 
        return new BigInteger(130, random).toString(32);
    }
}
