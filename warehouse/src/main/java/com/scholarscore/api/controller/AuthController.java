package com.scholarscore.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.user.User;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * Defines methods for getting stats on the authenticated user
 *
 * Created by mgreenwood on 9/14/15.
 */
@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/auth")
public class AuthController extends BaseController {

    @SuppressWarnings("rawtypes")
    @ApiOperation(
            value = "Get current user by authentication token",
            notes = "If the user is presently signed in, return the type of user associated with the identity (teacher, administrator, student)",
            response = User.class)
    @RequestMapping(
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    public @ResponseBody
    ResponseEntity getCurrentIdentity() {
        ServiceResponse<User> identity = pm.getUserManager().getCurrentUser();
        return respond(identity);
    }
}
