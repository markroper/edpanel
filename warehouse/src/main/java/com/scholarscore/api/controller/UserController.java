package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.ui.PasswordUpdate;
import com.scholarscore.models.user.ContactType;
import com.scholarscore.models.user.User;
import com.scholarscore.models.user.UserWithOneTimePass;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/users")
public class UserController extends BaseController {

	@ApiOperation(
			value = "Get all unverified users with one time passwords",
			notes = "Retrieve all unverified users and include their one time passwords",
			response = List.class)
	@RequestMapping(
			value = "/unverified",
			method = RequestMethod.GET,
			produces = { JSON_ACCEPT_HEADER })
	@SuppressWarnings("rawtypes")
	public @ResponseBody ResponseEntity getAllUnverifiedUsersWithOneTimePasswords(
			@RequestParam(required = false, value = "schoolId") Long schoolId) {
		ServiceResponse<Collection<User>> users;
		if(null != schoolId) {
			users = pm.getUserManager().getAllUsersInSchool(schoolId, false);
		} else {
			users = pm.getUserManager().getAllUsers();
		}
		List<UserWithOneTimePass> usersWrapped = new ArrayList<>();
		if(null != users.getValue()) {
			usersWrapped.addAll(
					users.getValue().stream().map(UserWithOneTimePass::new).collect(Collectors.toList()));
			return respond(new ServiceResponse<>(usersWrapped));
		} else {
			return respond(users);
		}
	}

	@ApiOperation(
	        value = "Get all users", 
	        notes = "Retrieve all users", 
	        response = List.class)
	@RequestMapping(
	        method = RequestMethod.GET, 
	        produces = { JSON_ACCEPT_HEADER })
	@SuppressWarnings("rawtypes")
	public @ResponseBody ResponseEntity getAll(
	        @RequestParam(value = "schoolId") Long schoolId,
            @RequestParam(value = "enabled") Boolean enabled) {
	    if(null != schoolId) {
	        if(null != enabled) {
				return respond(pm.getUserManager().getAllUsersInSchool(schoolId, enabled));
			}
	        return respond(pm.getUserManager().getAllUsersInSchool(schoolId));
	    }
	    return respond(pm.getUserManager().getAllUsers());
	}

	@ApiOperation(
	        value = "Get a user by user ID", 
	        notes = "Given a user ID, the endpoint returns the user", 
	        response = User.class)
	@RequestMapping(
	        value = "/{userId}", 
	        method = RequestMethod.GET, 
	        produces = { JSON_ACCEPT_HEADER })
	@SuppressWarnings("rawtypes")
	public @ResponseBody ResponseEntity get(
	        @ApiParam(name = "userId", required = true, value = "User ID")
	        @PathVariable(value="userId") Long userId) {
	    return respond(pm.getUserManager().getUser(userId));
	}
	
	@ApiOperation(
	        value = "Create a user", 
	        notes = "Creates and returns a user",
	        response = String.class)
	@RequestMapping(
	        method = RequestMethod.POST, 
	        produces = {JSON_ACCEPT_HEADER})
	@SuppressWarnings("rawtypes")
	public @ResponseBody ResponseEntity create(@RequestBody @Valid User user) {
	    return respond(pm.getUserManager().createUser(user));
	}
	
	@ApiOperation(
	        value = "Overwrite an existing user", 
	        notes = "Overwrites an existing user entity",
	        response = String.class)
	@RequestMapping(
	        value = "/{userId}", 
	        method = RequestMethod.PUT, 
	        produces = { JSON_ACCEPT_HEADER })
	@SuppressWarnings("rawtypes")
	public @ResponseBody ResponseEntity replace(
	        @ApiParam(name = "userId", required = true, value = "User ID")
	        @PathVariable(value="userId") Long userId,
	        @RequestBody @Valid User user) {
	    return respond(pm.getUserManager().replaceUser(userId, user));
	}
	
	@ApiOperation(
	        value = "Update an existing user", 
	        notes = "Updates an existing user properties. Will not overwrite existing values with null.",
	        response = String.class)
	@RequestMapping(
	        value = "/{userId}", 
	        method = RequestMethod.PATCH, 
	        produces = { JSON_ACCEPT_HEADER })
	@SuppressWarnings("rawtypes")
	public @ResponseBody ResponseEntity update(
	        @ApiParam(name = "userId", required = true, value = "User ID")
	        @PathVariable(value="userId") Long userId,
	        @RequestBody User user) {
	    return respond(pm.getUserManager().updateUser(userId, user));
	}
	
	@ApiOperation(
	        value = "Delete a user", 
	        response = Void.class)
	@RequestMapping(
	        value = "/{userId}", 
	        method = RequestMethod.DELETE, 
	        produces = { JSON_ACCEPT_HEADER })
	@SuppressWarnings("rawtypes")
	public @ResponseBody ResponseEntity delete(
	        @ApiParam(name = "userId", required = true, value = "User ID")
	        @PathVariable(value="userId") Long userId) {
	    return respond(pm.getUserManager().deleteUser(userId));
	}

	@ApiOperation(
			value = "Start validation for phone contact info",
			response = Void.class)
	@RequestMapping(
			value = "/{userId}/validation/{contactType}",
			method = RequestMethod.POST,
			produces = { JSON_ACCEPT_HEADER })
	@SuppressWarnings("rawtypes")
	public @ResponseBody ResponseEntity startContactValidation(
			@ApiParam(name = "userId", required = true, value = "User ID")
			@PathVariable(value="userId") Long userId,
			@ApiParam(name = "contactType", required = true, value = "Contact Type")
			@PathVariable(value="contactType") ContactType contactType)
	{
		return respond(pm.getUserManager().startContactValidation(userId, contactType));
	}

	@ApiOperation(
			value = "Complete validation for contact info",
			response = Void.class)
	@RequestMapping(
			value = "/{userId}/validation/{contactType}/{confirmCode}",
			// This is made a GET so that it can be accessed directly as a link
			// from the user's email
			method = RequestMethod.GET,
			produces = { JSON_ACCEPT_HEADER })
	@SuppressWarnings("rawtypes")
	public @ResponseBody ResponseEntity confirmContactValidation(
			@ApiParam(name = "userId", required = true, value = "User ID")
			@PathVariable(value = "userId") Long userId,
			@ApiParam(name = "contactType", required = true, value = "Contact Type")
			@PathVariable(value="contactType") ContactType contactType,
			@ApiParam(name = "confirmCode", required = true, value = "Validation code sent to phone")
			@PathVariable(value = "confirmCode") String confirmCode
	) {
		return respond(pm.getUserManager().confirmContactValidation(userId, contactType, confirmCode));
	}

	@ApiOperation(
			value = "Start password reset",
			response = Void.class)
	@RequestMapping(
			value = "/requestPasswordReset/{username}",
			// This is made a GET so that it can be accessed directly as a link
			// from the user's email
			method = RequestMethod.POST,
			produces = { JSON_ACCEPT_HEADER })
	@SuppressWarnings("rawtypes")
	public @ResponseBody ResponseEntity startPasswordReset(
			@ApiParam(name = "username", required = true, value = "username")
			@PathVariable(value = "username") String username
	) {
		return respond(pm.getUserManager().startPasswordReset(username));
	}

	@ApiOperation(
			value = "Set new password",
			response = Void.class)
	@RequestMapping(
			value = "/passwordReset/{userId}",
			method = RequestMethod.PUT,
			produces = { JSON_ACCEPT_HEADER })
	@SuppressWarnings("rawtypes")
	public @ResponseBody ResponseEntity submitPassword(
			@ApiParam(name = "userId", required = true, value = "User ID")
			@PathVariable(value = "userId") Long userId,
			@RequestBody @Valid PasswordUpdate passwordUpdate
	) {
		String password = passwordUpdate.getNewPassword();
		return respond(pm.getUserManager().resetPassword(userId, password) );
	}
	
}
