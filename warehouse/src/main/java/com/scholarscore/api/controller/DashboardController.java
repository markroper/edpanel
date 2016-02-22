package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.annotation.StudentAccessible;
import com.scholarscore.models.dashboard.Dashboard;
import com.scholarscore.models.message.MessageThread;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * Created by markroper on 2/16/16.
 */
@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/schools/{schoolId}/dashboards")
public class DashboardController extends BaseController {
    @ApiOperation(
            value = "Get the appropriate dashboard for the requesting user",
            response = MessageThread.class)
    @RequestMapping(
            method = RequestMethod.GET,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    @StudentAccessible
    public @ResponseBody
    ResponseEntity getDashboard(
            @ApiParam(name = "schoolId", required = true, value = "The school ID")
            @PathVariable(value="schoolId") Long schoolId) {
        return respond(pm.getDashboardManager().getDashboard(schoolId));
    }

    @ApiOperation(
            value = "Get a dashboard",
            response = MessageThread.class)
    @RequestMapping(
            value = "/{dashId}",
            method = RequestMethod.GET,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    @StudentAccessible
    public @ResponseBody
    ResponseEntity getDashboard(
            @ApiParam(name = "schoolId", required = true, value = "The school ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "dashId", required = true, value = "The dashboard's long ID")
            @PathVariable(value="dashId") Long dashId) {
        return respond(pm.getDashboardManager().getDashboard(schoolId, dashId));
    }

    @ApiOperation(
            value = "Delete a dashboard",
            response = Void.class)
    @RequestMapping(
            value = "/{dashId}",
            method = RequestMethod.DELETE,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteDashboard(
            @ApiParam(name = "schoolId", required = true, value = "The school ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "dashId", required = true, value = "The dashboard ID")
            @PathVariable(value="dashId") Long dashId) {
        return respond(pm.getDashboardManager().deleteDashboard(schoolId, dashId));
    }

    @ApiOperation(
            value = "Update an existing dashboard",
            response = Void.class)
    @RequestMapping(
            value = "/{dashId}",
            method = RequestMethod.PUT,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible
    public @ResponseBody ResponseEntity replaceDashboard(
            @ApiParam(name = "schoolId", required = true, value = "The school ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "dashId", required = true, value = "The dashboard long ID")
            @PathVariable(value="dashId") Long dashId,
            @RequestBody @Valid Dashboard dash) {
        return respond(pm.getDashboardManager().replaceDashboard(schoolId, dashId, dash));
    }

    @ApiOperation(
            value = "Create an existing dashboard",
            response = Void.class)
    @RequestMapping(
            method = RequestMethod.POST,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible
    public @ResponseBody ResponseEntity createDashboard(
            @ApiParam(name = "schoolId", required = true, value = "The school ID")
            @PathVariable(value="schoolId") Long schoolId,
            @RequestBody @Valid Dashboard dash) {
        return respond(pm.getDashboardManager().createDashboard(schoolId, dash));
    }
}
