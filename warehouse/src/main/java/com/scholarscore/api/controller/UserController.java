package com.scholarscore.api.controller;

import java.util.List;

import javax.validation.Valid;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.manager.UserManager;
import com.scholarscore.models.user.User;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/users")
public class UserController extends BaseController {


	@ApiOperation(
	        value = "Get all users", 
	        notes = "Retrieve all users", 
	        response = List.class)
	@RequestMapping(
	        method = RequestMethod.GET, 
	        produces = { JSON_ACCEPT_HEADER })
	@SuppressWarnings("rawtypes")
	public @ResponseBody ResponseEntity getAll() {
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
	        @RequestBody @Valid User user) {
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

	// TODO Jordan: for these next endpoints, ensure the user being edited is the same as the user logged in (or administrator, maybe)
	@ApiOperation(
			value = "Start validation for phone contact info",
			response = Void.class)
	@RequestMapping(
			value = "/{userId}/validation/phone",
			method = RequestMethod.POST,
			produces = { JSON_ACCEPT_HEADER })
	@SuppressWarnings("rawtypes")
	public @ResponseBody ResponseEntity startPhoneContactValidation(
			@ApiParam(name = "userId", required = true, value = "User ID")
			@PathVariable(value="userId") Long userId) {
		return respond(pm.getUserManager().startPhoneContactValidation(userId));
	}

	@ApiOperation(
			value = "Start validation for email contact info",
			response = Void.class)
	@RequestMapping(
			value = "/{userId}/validation/email",
			method = RequestMethod.POST,
			produces = { JSON_ACCEPT_HEADER })
	@SuppressWarnings("rawtypes")
	public @ResponseBody ResponseEntity startEmailContactValidation(
			@ApiParam(name = "userId", required = true, value = "User ID")
			@PathVariable(value="userId") Long userId) {
		return respond(pm.getUserManager().startEmailContactValidation(userId));
	}

	// TODO Jordan: I guess the user doesn't have to be logged in for these next endpoints?
	// (maybe their email client opens into a different default browser, I worry we can't safely assume 'logged in')
	@ApiOperation(
			value = "Complete validation for phone contact info",
			response = Void.class)
	@RequestMapping(
			value = "/{userId}/validation/phone/{phoneCode}",
			method = RequestMethod.GET,
			produces = { JSON_ACCEPT_HEADER })
	@SuppressWarnings("rawtypes")
	public @ResponseBody ResponseEntity completePhoneContactValidation(
			@ApiParam(name = "userId", required = true, value = "User ID")
			@PathVariable(value="userId") Long userId,
			@ApiParam(name = "phoneCode", required = true, value = "Validation code sent to phone")
			@PathVariable(value="phoneCode") String phoneCode
			) {
		return respond(pm.getUserManager().completePhoneContactValidation(userId, phoneCode));
	}

	@ApiOperation(
			value = "Complete validation for email contact info",
			response = Void.class)
	@RequestMapping(
			value = "/{userId}/validation/email/{emailCode}",
			method = RequestMethod.GET,
			produces = { JSON_ACCEPT_HEADER })
	@SuppressWarnings("rawtypes")
	public @ResponseBody ResponseEntity completeEmailContactValidation(
			// This is made a GET so that it can be accessed directly as a link
			// from the user's email
			@ApiParam(name = "userId", required = true, value = "User ID")
			@PathVariable(value="userId") Long userId,
			@ApiParam(name = "emailCode", required = true, value = "Validation code sent to email")
			@PathVariable(value="emailCode") String emailCode) {
		return respond(pm.getUserManager().completeEmailContactValidation(userId, emailCode));
	}
	

}
