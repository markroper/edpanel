package com.scholarscore.api.controller.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.message.Message;
import com.scholarscore.models.message.MessageReadState;
import com.scholarscore.models.message.MessageThread;
import com.scholarscore.models.message.MessageThreadParticipant;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import java.util.HashSet;
import java.util.List;

/**
 * Created by markroper on 1/18/16.
 */
public class MessageValidatingExecutor {
    private final IntegrationBase serviceBase;

    public MessageValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }

    public Message createMessage(Long threadId, Message s, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.POST,
                serviceBase.getMessageEndpoint(threadId),
                null,
                s);
        EntityId MessageId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(MessageId, "unexpected null day ID returned from create call for case: " + msg);
        return retrieveAndValidateCreatedMessage(s, MessageId, msg);
    }

    public Message markMessageReadForUser(long threadId, long messageId, long userId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.POST,
                serviceBase.getMarkMessageReadEndpoint(threadId, messageId, userId),
                null,
                null);
        Void MessageId = serviceBase.validateResponse(response, new TypeReference<Void>(){});
        Assert.assertNull(MessageId, "unexpected null day ID returned from create call for case: " + msg);
        Message m = this.get(threadId, messageId, msg);
        boolean hasRs = false;
        for(MessageReadState r: m.getReadStateList()) {
            if(r.getParticipantId().equals(userId));
            hasRs = true;
            break;
        }
        Assert.assertTrue(hasRs, "Message not marked read for user");
        return m;
    }

    public void createNegative(long threadId, Message s, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.POST,
                serviceBase.getMessageEndpoint(threadId),
                null,
                s);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected status code returned for case: " + msg);
    }

    public void createThreadNegative(Long threadId, Message s, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.POST,
                serviceBase.getMessageThreadsEndpoint(threadId),
                null,
                s);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected status code returned for case: " + msg);
    }

    public MessageThread createThread(MessageThread s, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.POST,
                serviceBase.getMessageThreadsEndpoint(),
                null,
                s);
        EntityId MessageId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(MessageId, "unexpected null day ID returned from create call for case: " + msg);
        return retrieveAndValidateCreatedThread(s, MessageId, msg);
    }


    public Message get(long threadId, long messageId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getMessageEndpoint(threadId, messageId),
                null);
        Message Message = serviceBase.validateResponse(response, new TypeReference<Message>(){});
        Assert.assertNotNull(Message, "Unexpected null day for case: " + msg);
        return Message;
    }

    public MessageThread getThread(long threadId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getMessageThreadsEndpoint(threadId),
                null);
        MessageThread message = serviceBase.validateResponse(response, new TypeReference<MessageThread>(){});
        Assert.assertNotNull(message, "Unexpected null day for case: " + msg);
        return message;
    }

    public List<Message> getAllThreadMessages(long threadId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getMessageEndpoint(threadId),
                null);
        List<Message> Messages = serviceBase.validateResponse(response, new TypeReference<List<Message>>(){});
        Assert.assertNotNull(Messages, "Unexpected null day for case: " + msg);
        return Messages;
    }

    public List<Message> getUnreadMessagesForUserOnThread(
            long threadId, long userId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getUnreadMessagesForUserEndpoint(threadId, userId),
                null);
        List<Message> Messages = serviceBase.validateResponse(response, new TypeReference<List<Message>>(){});
        Assert.assertNotNull(Messages, "Unexpected null day for case: " + msg);
        return Messages;
    }

    public List<Message> getAllUnreadMessagesForUserAllThreads(long userId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getUnreadMessagesForUserEndpont(userId),
                null);
        List<Message> Messages = serviceBase.validateResponse(response, new TypeReference<List<Message>>(){});
        Assert.assertNotNull(Messages, "Unexpected null day for case: " + msg);
        return Messages;
    }

    public void getNegative(long threadId, long messageId, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getMessageEndpoint(threadId, messageId),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected status code returned while retrieving Message: " + msg);
    }

    public void deleteThread(long threadId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.DELETE,
                serviceBase.getMessageThreadsEndpoint(threadId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
    }

    public void delete(long threadId, long messageId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.DELETE,
                serviceBase.getMessageEndpoint(threadId, messageId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(threadId, messageId, HttpStatus.NOT_FOUND, msg);
    }

    public void deleteNegative(long threadId, long messageId, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE,
                serviceBase.getMessageEndpoint(threadId, messageId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }

    public Message retrieveAndValidateCreatedMessage(Message submitted, EntityId id, String msg) {
        submitted.setId(id.getId());
        Message created = this.get(submitted.getThread().getId(), id.getId(), msg);
        submitted.setSent(created.getSent());
        if(null == submitted.getReadStateList()) {
            submitted.setReadStateList(new HashSet<>());
        }
        Assert.assertEquals(created, submitted, msg + " - these should be equal but they are not:\nCREATED:\n"
                + created + "\n" + "SUBMITTED:\n" + submitted);
        return created;
    }
    public MessageThread retrieveAndValidateCreatedThread(MessageThread submitted, EntityId id, String msg) {
        submitted.setId(id.getId());
        MessageThread created = this.getThread(id.getId(), msg);
        if(null != submitted.getParticipants()) {
            HashSet<MessageThreadParticipant> newSet = new HashSet<>();
            for (MessageThreadParticipant p : submitted.getParticipants()) {
                p.setThreadId(created.getId());
                newSet.add(p);
            }
            submitted.setParticipants(newSet);
        } else {
            submitted.setParticipants(new HashSet<>());
        }
        if(null != submitted.getTopic()) {
            submitted.getTopic().setId(created.getTopic().getId());
        }
        Assert.assertEquals(created, submitted, msg + " - these should be equal but they are not:\nCREATED:\n"
                + created + "\n" + "SUBMITTED:\n" + submitted);
        return created;
    }
}
