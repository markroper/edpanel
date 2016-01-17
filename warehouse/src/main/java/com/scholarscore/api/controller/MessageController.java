package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.message.Message;
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
import java.util.List;

/**
 * Message threads contain a list of participating users on the thread.  These can be changed over time
 * without any impact on messages already on the thread.  All messages are on a thread.  Threads can be created,
 * deleted, and their participants can be modified.  Messages can be marked as read by specfic users.
 *
 * Messages can be queries by thread and by status as being read or not by a given user. There is also a convenience
 * endpoint that returns all unread messages for a user across all message threads that the user is a participant in.
 */
@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/messagethreads")
public class MessageController extends BaseController {
    //CREATE/DELETE THREAD
    @ApiOperation(
            value = "Create a message thread",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity createMessageThread(
            @RequestBody @Valid MessageThread thread) {
        return respond(pm.getMessageManager().createMessageThread(thread));
    }

    @ApiOperation(
            value = "Delete a message thread",
            response = Void.class)
    @RequestMapping(
            value = "{threadId}",
            method = RequestMethod.DELETE,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteNotification(
            @ApiParam(name = "threadId", required = true, value = "The thread long ID")
            @PathVariable(value="threadId") Long threadId) {
        return respond(pm.getMessageManager().deleteMessageThread(threadId));
    }

    @ApiOperation(
            value = "Update an existing thread",
            notes = "Can be used to update participants",
            response = EntityId.class)
    @RequestMapping(
            value = "{threadId}",
            method = RequestMethod.PUT,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replaceNotification(
            @ApiParam(name = "threadId", required = true, value = "The thread long ID")
            @PathVariable(value="threadId") Long threadId,
            @RequestBody @Valid MessageThread thread) {
        return respond(pm.getMessageManager().replaceMessageThread(threadId, thread));
    }

    //CRUD ON MESSAGES
    @ApiOperation(
            value = "Get a message by ID",
            notes = "Given a message ID, the endpoint returns the message",
            response = Message.class)
    @RequestMapping(
            value = "{threadId}/messages/{messageId}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody
    ResponseEntity getNotification(
            @ApiParam(name = "threadId", required = true, value = "The thread long ID")
            @PathVariable(value="threadId") Long threadId,
            @ApiParam(name = "messageId", required = true, value = "The message long ID")
            @PathVariable(value="messageId") Long messageId) {
        return respond(pm.getMessageManager().getMessage(threadId, messageId));
    }

    @ApiOperation(
            value = "Get all messages on thread",
            response = List.class)
    @RequestMapping(
            value = "{threadId}/messages}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody
    ResponseEntity getNotification(
            @ApiParam(name = "threadId", required = true, value = "The thread long ID")
            @PathVariable(value="threadId") Long threadId) {
        return respond(pm.getMessageManager().getMessages(threadId));
    }

    @ApiOperation(
            value = "Create a message on a thread",
            notes = "Creates, assigns an ID to, persists and returns a message",
            response = EntityId.class)
    @RequestMapping(
            value = "{threadId}/messages",
            method = RequestMethod.POST,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity createMessage(
            @ApiParam(name = "threadId", required = true, value = "The thread long ID")
            @PathVariable(value="threadId") Long threadId,
            @RequestBody @Valid Message message) {
        return respond(pm.getMessageManager().createMessage(threadId, message));
    }

    @ApiOperation(
            value = "Overwrite an existing message",
            notes = "Overwrites an existing message on a thread",
            response = EntityId.class)
    @RequestMapping(
            value = "{threadId}/messages/{messageId}",
            method = RequestMethod.PUT,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity replaceMessage(
            @ApiParam(name = "threadId", required = true, value = "The thread long ID")
            @PathVariable(value="threadId") Long threadId,
            @ApiParam(name = "messageId", required = true, value = "The message long ID")
            @PathVariable(value="messageId") Long messageId,
            @RequestBody @Valid Message message) {
        return respond(pm.getMessageManager().replaceMessage(threadId, messageId, message));
    }

    @ApiOperation(
            value = "Delete a message by ID",
            response = Void.class)
    @RequestMapping(
            value = "{threadId}/messages/{messageId}",
            method = RequestMethod.DELETE,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteMessage(
            @ApiParam(name = "threadId", required = true, value = "The thread long ID")
            @PathVariable(value="threadId") Long threadId,
            @ApiParam(name = "messageId", required = true, value = "The message long ID")
            @PathVariable(value="messageId") Long messageId) {
        return respond(pm.getMessageManager().deleteMessage(threadId, messageId));
    }

    @ApiOperation(
            value = "Get all unread messages on thread for user",
            response = List.class)
    @RequestMapping(
            value = "{threadId}/participants/{userId}/messages",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody
    ResponseEntity getUnreadNotifications(
            @ApiParam(name = "threadId", required = true, value = "The thread long ID")
            @PathVariable(value="threadId") Long threadId,
            @ApiParam(name = "userId", required = true, value = "The user's ID")
            @PathVariable(value="userId") Long userId) {
        return respond(pm.getMessageManager().getUnreadMessages(threadId, userId));
    }

    @ApiOperation(
            value = "Get all threads a user is a participant in",
            response = List.class)
    @RequestMapping(
            value = "/participants/{userId}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody
    ResponseEntity getParticipatingThreads(
            @ApiParam(name = "userId", required = true, value = "The user's ID")
            @PathVariable(value="userId") Long userId) {
        return respond(pm.getMessageManager().getAllParticipatingThreads(userId));
    }

    @ApiOperation(
            value = "Get all unread messages for a user across all threads",
            response = List.class)
    @RequestMapping(
            value = "/participants/{userId}/messages",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody
    ResponseEntity getAllUnreadMessagesAllThreads(
            @ApiParam(name = "userId", required = true, value = "The user's ID")
            @PathVariable(value="userId") Long userId) {
        return respond(pm.getMessageManager().getAllUnreadMessagesAllThreads(userId));
    }

    @ApiOperation(
            value = "Mark message as read for user",
            response = List.class)
    @RequestMapping(
            value = "{threadId}/messages/{messageId}/participants/{userId}/readreciepts",
            method = RequestMethod.POST,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody
    ResponseEntity getAllUnreadMessagesAllThreads(
            @ApiParam(name = "threadId", required = true, value = "The thread long ID")
            @PathVariable(value="threadId") Long threadId,
            @ApiParam(name = "messageId", required = true, value = "The message long ID")
            @PathVariable(value="messageId") Long messageId,
            @ApiParam(name = "userId", required = true, value = "The user's ID")
            @PathVariable(value="userId") Long userId) {
        return respond(pm.getMessageManager().markMessageReadByUser(threadId, messageId, userId));
    }
}
