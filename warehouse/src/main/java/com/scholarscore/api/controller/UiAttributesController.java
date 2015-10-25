package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.models.UiAttributes;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Validated
@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/schools/{schoolId}/uiattributes")
public class UiAttributesController extends BaseController {
    @ApiOperation(
            value = "Get UI attributes for school", 
            notes = "Given a school ID, returns the UI attributes", 
            response = UiAttributes.class)
    @RequestMapping(
            method = RequestMethod.GET, 
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getUiAttributes(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId) {
        return respond(pm.getUiAttributesManager().getUiAttributes(schoolId));
    }

    @ApiOperation(
            value = "Create UI attributes for school", 
            notes = "Creates the UI attributes object provided for the school",
            response = Void.class)
    @RequestMapping(
            method = RequestMethod.POST, 
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity createUiAttributes(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @RequestBody @Valid UiAttributes attrs) {
        return respond(pm.getUiAttributesManager().createUiAttributes(schoolId, attrs));
    }
    
    @ApiOperation(
            value = "Replace UI attributes for school", 
            notes = "Replaces the UI attributes object for the school with the object provided",
            response = Void.class)
    @RequestMapping(
            method = RequestMethod.PUT, 
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replaceUiAttributes(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @RequestBody @Valid UiAttributes attrs) {
        return respond(pm.getUiAttributesManager().replaceUiAttributes(schoolId, attrs));
    }

}
