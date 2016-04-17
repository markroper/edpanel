package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.StudentWatch;
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
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/watches")
public class WatchController extends BaseController {


    @ApiOperation(
            value = "Create a student",
            notes = "Creates, assigns an ID to, persists and returns a student",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity create(@RequestBody StudentWatch watch) {
        return respond(pm.getWatchManager().createWatch(watch));
    }

    @ApiOperation(
            value = "Get all watches for a staff member",
            notes = "Gets all the watches for a staff member",
            response = EntityId.class)
    @RequestMapping(
            value = "/staff/{staffId}",
            method = RequestMethod.GET,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getAllForStaff(
            @ApiParam(name = "staffId", required = true, value = "The user ID")
            @PathVariable(value="staffId") Long staffId
    ) {
        return respond(pm.getWatchManager().getAllForStaff(staffId));
    }

    @ApiOperation(
            value = "Delete a watch",
            notes = "Creates, assigns an ID to, persists and returns a student",
            response = EntityId.class)
    @RequestMapping(
            value = "/watches/{watchId}",
            method = RequestMethod.DELETE,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteWatch(
            @ApiParam(name = "watchId", required = true, value = "The watch ID")
            @PathVariable(value="watchId") Long watchId
    ) {
        return respond(pm.getWatchManager().deleteWatch(watchId));
    }



}
