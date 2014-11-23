package com.scholarscore.endpoints;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: jordan
 * Date: 11/22/14
 * Time: 8:28 PM
 */
@Controller
@RequestMapping("/endpoint")
public class Endpoint {

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody String someOtherData() {
        return "{\"message\": \"You are cool!\"}";

    }
}
