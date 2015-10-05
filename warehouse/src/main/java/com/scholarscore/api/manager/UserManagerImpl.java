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
import com.scholarscore.api.service.SingleAccountGmailService;
import com.scholarscore.api.service.TextMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
        return new ServiceResponse<Long>(userPersistence.deleteUser(userId));
    }

    @Override
    public ServiceResponse<User> getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetailsProxy) {
                UserDetailsProxy proxy = (UserDetailsProxy)principal;
                return new ServiceResponse<User>(proxy.getUser());
            }
        }
        return new ServiceResponse<User>(new StatusCode(StatusCodes.NOT_AUTHENTICATED,
                "{\"error\": \"Not Authenticated\"}"));
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
        } else if (selectedContactMethod.getConfirmed()) {
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
        // of different contact mediums and messages... if/else statements on Enums like this are bad because 
        // problems arise quietly (or silently) when new enum values are added 
        if (ContactType.EMAIL.equals(selectedContactMethod.getContactType())) {
            String toAddress = "jodamn@gmail.com";
            String subject = "(DEV) email confirmation from EdPanel";
            String message = "Hello! Please enter this code when prompted by edpanel: ( " + code + " ). "
                    + "\n -- OR --"
                    + "\n click here to confirm: https://myedpanel.com/warehouse/v1/" + user.getUsername() + "/validation/email/" + code + "";
//            EmailService emailService = new SingleAccountGmailService();
            emailService.sendMessage(toAddress, subject, message);
        } else if (ContactType.PHONE.equals(selectedContactMethod.getContactType())) {
            // TODO Jordan: implement phone message dispatch here
            // ... provide this code to the user via the specific medium of the contact
            // make it abstract and springified so it's relatively easy to add new message types in the future
            System.out.println("!! !! !! Here is where a " + selectedContactMethod.getContactType()
                    +  " message would really be sent to " + selectedContactMethod.getContactValue() + "...");
            String toPhoneNumber = "";
            
            String toNumber = "9785400002";
            String msg = "Confirm code from EdPanel: " + code;
            textService.sendMessage(toNumber, msg);
        } else {
            // this sucks - see above 
            logger.error("ERROR! Unknown enum type seen in switch statement in UserManagerImpl");
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
            if (selectedContactMethod.getConfirmed()) {
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
    
    private String generateCode() { 
        return new BigInteger(130, random).toString(32);
    }
}
