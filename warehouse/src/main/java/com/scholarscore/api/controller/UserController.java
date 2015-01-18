package com.scholarscore.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scholarscore.models.User;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/api/v1/users")
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
	    return respond(getUserManager().getAllUsers());
	}
	
	@ApiOperation(
	        value = "Get a user by username", 
	        notes = "Given a user username, the endpoint returns the user", 
	        response = User.class)
	@RequestMapping(
	        value = "/{username}", 
	        method = RequestMethod.GET, 
	        produces = { JSON_ACCEPT_HEADER })
	@SuppressWarnings("rawtypes")
	public @ResponseBody ResponseEntity get(
	        @ApiParam(name = "username", required = true, value = "User login name")
	        @PathVariable(value="username") String username) {
	    return respond(getUserManager().getUser(username));
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
	    return respond(getUserManager().createUser(user));
	}
	
	@ApiOperation(
	        value = "Overwrite an existing user", 
	        notes = "Overwrites an existing user entity",
	        response = String.class)
	@RequestMapping(
	        value = "/{username}", 
	        method = RequestMethod.PUT, 
	        produces = { JSON_ACCEPT_HEADER })
	@SuppressWarnings("rawtypes")
	public @ResponseBody ResponseEntity replace(
	        @ApiParam(name = "username", required = true, value = "User login name")
	        @PathVariable(value="username") String username,
	        @RequestBody @Valid User user) {
	    return respond(getUserManager().replaceUser(username, user));
	}
	
	@ApiOperation(
	        value = "Update an existing user", 
	        notes = "Updates an existing user properties. Will not overwrite existing values with null.",
	        response = String.class)
	@RequestMapping(
	        value = "/{username}", 
	        method = RequestMethod.PATCH, 
	        produces = { JSON_ACCEPT_HEADER })
	@SuppressWarnings("rawtypes")
	public @ResponseBody ResponseEntity update(
	        @ApiParam(name = "username", required = true, value = "User login name")
	        @PathVariable(value="username") String username,
	        @RequestBody @Valid User user) {
	    return respond(getUserManager().updateUser(username, user));
	}
	
	@ApiOperation(
	        value = "Delete a user", 
	        response = Void.class)
	@RequestMapping(
	        value = "/{username}", 
	        method = RequestMethod.DELETE, 
	        produces = { JSON_ACCEPT_HEADER })
	@SuppressWarnings("rawtypes")
	public @ResponseBody ResponseEntity delete(
	        @ApiParam(name = "username", required = true, value = "User login name")
	        @PathVariable(value="username") String username) {
	    return respond(getUserManager().deleteUser(username));
	}
	

}
