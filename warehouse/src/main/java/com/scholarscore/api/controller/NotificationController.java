package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.annotation.StudentAccessible;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.School;
import com.scholarscore.models.notification.Notification;
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
import java.util.List;

/**
 * Created by markroper on 1/10/16.
 */
@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/notifications")
public class NotificationController extends BaseController {
    @ApiOperation(
            value = "Evaluates all notifications for a school & creates triggered notification instances",
            response = EntityId.class)
    @RequestMapping(
            value = "/schools/{schoolId}/evaluations",
            method = RequestMethod.POST,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity evaluateNotifications(
            @ApiParam(name = "schoolId", required = true, value = "The school ID")
            @PathVariable(value="schoolId") Long schoolId) {
        return respond(pm.getNotificationManager().evaluateNotifications(schoolId));
    }

    @ApiOperation(
            value = "Get all notifications defined within a district",
            notes = "Retrieve all notifications",
            response = List.class)
    @RequestMapping(
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody
    ResponseEntity getAllNotifications() {
        return respond(pm.getNotificationManager().getAllNotifications());
    }

    @ApiOperation(
            value = "Get all notifications owned by a user",
            notes = "Retrieve all notifications owned by user",
            response = List.class)
    @RequestMapping(
            value = "/users/{userId}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible(paramName = "userId")
    public @ResponseBody
    ResponseEntity getAllUserNotifications(
            @ApiParam(name = "userId", required = true, value = "The user ID")
            @PathVariable(value="userId") Long userId) {
        return respond(pm.getNotificationManager().getAllNotificationsForUser(userId));
    }

    @ApiOperation(
            value = "Get a notification by ID",
            notes = "Given a notification ID, the endpoint returns the notification",
            response = School.class)
    @RequestMapping(
            value = "/{notificationId}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible
    public @ResponseBody ResponseEntity getNotification(
            @ApiParam(name = "notificationId", required = true, value = "The notification long ID")
            @PathVariable(value="notificationId") Long notificationId) {
        return respond(pm.getNotificationManager().getNotification(notificationId));
    }

    @ApiOperation(
            value = "Create a notification within the district",
            notes = "Creates, assigns an ID to, persists and returns a notification",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    @StudentAccessible
    public @ResponseBody ResponseEntity createNotification(@RequestBody @Valid Notification notification) {
        return respond(pm.getNotificationManager().createNotification(notification));
    }

    @ApiOperation(
            value = "Overwrite an existing notification within a district",
            notes = "Overwrites an existing notification within a district with the ID provided",
            response = EntityId.class)
    @RequestMapping(
            value = "/{notificationId}",
            method = RequestMethod.PUT,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible
    public @ResponseBody ResponseEntity replaceNotification(
            @ApiParam(name = "notificationId", required = true, value = "The notification ID")
            @PathVariable(value="notificationId") Long notificationId,
            @RequestBody @Valid Notification notification) {
        return respond(pm.getNotificationManager().replaceNotification(notificationId, notification));
    }

    @ApiOperation(
            value = "Delete a notification by ID",
            response = Void.class)
    @RequestMapping(
            value = "/{notificationId}",
            method = RequestMethod.DELETE,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible
    public @ResponseBody ResponseEntity deleteNotification(
            @ApiParam(name = "notificationId", required = true, value = "The notification ID")
            @PathVariable(value="notificationId") Long notificationId) {
        return respond(pm.getNotificationManager().deleteNotification(notificationId));
    }

    /*
        TRIGGERED NOTIFICATION MGMT BELOW
     */
    @ApiOperation(
            value = "Get all triggered notifications for a user",
            response = List.class)
    @RequestMapping(
            value = "/users/{userId}/triggerednotifications",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible(paramName = "userId")
    public @ResponseBody
    ResponseEntity getAllUserTriggeredNotifications(
            @ApiParam(name = "userId", required = true, value = "The user ID")
            @PathVariable(value="userId") Long userId,
            @RequestParam(value="includeInactive", required = false) Boolean includeInactive) {
        return respond(pm.getNotificationManager().getAllTriggeredNotificationsForUser(userId, includeInactive));
    }

    @ApiOperation(
            value = "Mark a triggered notification inactive for a user",
            response = Void.class)
    @RequestMapping(
            value = "{notificationId}/triggerednotifications/{triggeredId}/users/{userId}",
            method = RequestMethod.PUT,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    @StudentAccessible(paramName = "userId")
    public @ResponseBody
    ResponseEntity disableTriggeredNotification(
            @ApiParam(name = "notificationId", required = true, value = "The notification ID")
            @PathVariable(value="notificationId") Long notificationId,
            @ApiParam(name = "triggeredId", required = true, value = "The triggered notification ID")
            @PathVariable(value="triggeredId") Long triggeredId,
            @ApiParam(name = "userId", required = true, value = "The user ID")
            @PathVariable(value="userId") Long userId) {
        return respond(pm.getNotificationManager().
                dismissTriggeredNotification(notificationId, triggeredId, userId));
    }
}
