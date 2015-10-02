package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.UserPersistence;
import com.scholarscore.api.security.config.UserDetailsProxy;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.user.User;

import com.scholarscore.util.EmailProvider;
import com.scholarscore.util.EmailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Date;

/**
 * Created by cwallace on 9/16/2015.
 */
public class UserManagerImpl implements UserManager {

    private UserPersistence userPersistence;

    private OrchestrationManager pm;

    private static final String USER = "user";

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
        return new ServiceResponse<User>(StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[] { USER, userId } ));
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
    public ServiceResponse<String> startPhoneContactValidation(Long userId) {
        User user = userPersistence.selectUser(userId);
        if (null == user) {
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{USER, userId}));
        }
        // TODO Jordan: implement!
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // TODO Jordan: test!!
    
    // TODO Jordan: hash saved code so that even DB hack can't necessarily get it?
    
    @Override
    public ServiceResponse<String> startEmailContactValidation(Long userId) {
        User user = userPersistence.selectUser(userId);
        if (null == user) {
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{USER, userId}));
        } else if (StringUtils.isEmpty(user.getEmailAddress())) {
            // TODO Jordan: this returns "The message with id user has not set email address! could not be found"
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{"message","user has not set email address!"}));
        } else if (user.getEmailConfirmed()) {
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.OK, new Object[] {"email already confirmed"}));
        }

        // if null user, return <something bad>
        // if user has null email, return 4?? - "email not set"
        // if user has already confirmed email, return 2?? - no action "email already confirmed"

        // otherwise, anytime this endpoint is hit and we get this far -
        // ... generate a NEW code and store it in the DB
        // ... also store the creation date of the code for expiration purposes
        // (these first 2 steps can be shared with phone validation)
        String code = generateCode();
        Date codeCreated = new Date();
        user.setEmailConfirmCode(code);
        user.setEmailConfirmCodeTime(codeCreated);

        // save the changes
        updateUser(user.getId(), user);
        // ... send an email
        // TODO Jordan: implement sending an email containing this code to the user!!
//        System.out.println("!! !! !! Here is where an EMAIL would really be sent to " + user.getEmailAddress() + "...");

        // springify after initial testing
        EmailProvider provider = new EmailService();
        
        String toAddress = "jodamn@gmail.com";
        
        String subject = "(DEV) email confirmation from EdPanel";
        
        String message = "Hello! Please enter this code when prompted by edpanel: ( " + code + " ). "
                + "\nLater, a link will show up here that can be clicked."
                + "https://myedpanel.com/warehouse/v1/" + user.getUsername() + "/validation/email/" + code + "";
        
        provider.sendEmail(toAddress, subject, message);

        return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.OK, new Object[] {"email has been sent"}));
        
//        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ServiceResponse<String> completePhoneContactValidation(Long userId, String code) {
        User user = userPersistence.selectUser(userId);
        if (null != user) {
            throw new UnsupportedOperationException("Not implemented yet");
//            return new ServiceResponse<User>(user);
        }
        // TODO Jordan: implement!
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ServiceResponse<String> completeEmailContactValidation(Long userId, String code) {
        User user = userPersistence.selectUser(userId);
        if (null == user) {
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{USER, userId}));
        } else if (StringUtils.isEmpty(user.getEmailAddress())) {
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{"email", "email is empty"}));
        } else if (user.getEmailConfirmed()) {
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.OK, new Object[] {"email already confirmed"}));
        }

        String userEmailCode = user.getEmailConfirmCode();
        if (userEmailCode == null) {
            // TODO Jordan return new type of failure, probably "invalid code"
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{"bad code!!"}));
        }
        
        if (userEmailCode.equalsIgnoreCase(code)) {
            user.setEmailConfirmed(true);
            updateUser(user.getId(), user);
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.OK, new Object[]{"validation successful!"}));
        } else {
            // !! "invalid code"
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{"bad code!!"}));
        }
    }
    
    private String generateCode() { 
        // TODO Jordan this is expensive, make it static or something
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }
}
