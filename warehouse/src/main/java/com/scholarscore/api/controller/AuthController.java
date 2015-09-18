package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.security.config.UserDetailsProxy;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.Identity;
import com.wordnik.swagger.annotations.ApiOperation;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Defines methods for getting stats on the authenticated user
 *
 * Created by mgreenwood on 9/14/15.
 */
@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/auth")
public class AuthController extends BaseController {

    @ApiOperation(
            value = "Get current user by authentication token",
            notes = "If the user is presently signed in, return the type of user associated with the identity (teacher, administrator, student)",
            response = Identity.class)
    @RequestMapping(
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    public @ResponseBody
    ResponseEntity getCurrentIdentity() {
        ServiceResponse<UserDetailsProxy> identity = pm.getUserManager().getCurrentUser();
        return respond(identity);
    }
}
