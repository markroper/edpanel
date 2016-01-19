package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.MessagePersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.message.Message;
import com.scholarscore.models.message.MessageReadState;
import com.scholarscore.models.message.MessageThread;
import com.scholarscore.models.message.MessageThreadParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

/**
 * Created by markroper on 1/17/16.
 */
public class MessageManagerImpl implements MessageManager {
    private final static Logger LOGGER = LoggerFactory.getLogger(NotificationManagerImpl.class);

    @Autowired
    private MessagePersistence messagePersistence;

    @Autowired
    private OrchestrationManager pm;

    private static final String MESSAGE = "message";
    private static final String MESSAGE_THREAD = "message thread";

    public void setMessagePersistence(MessagePersistence messagePersistence) {
        this.messagePersistence = messagePersistence;
    }

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }

    @Override
    public ServiceResponse<EntityId> createMessageThread(MessageThread t) {
        return new ServiceResponse<>(new EntityId(messagePersistence.insertMessageThread(t)));
    }

    @Override
    public ServiceResponse<MessageThread> getMessageThread(Long threadId) {
        return new ServiceResponse<>(messagePersistence.selectMessageThread(threadId));
    }

    @Override
    public ServiceResponse<Void> replaceMessageThread(Long threadId, MessageThread t) {
        messagePersistence.updateMessageThread(t);
        return new ServiceResponse<>((Void) null);
    }

    @Override
    public ServiceResponse<Void> deleteMessageThread(Long threadId) {
        MessageThread t = messagePersistence.selectMessageThread(threadId);
        if(null != t.getParticipants()) {
            for(MessageThreadParticipant p: t.getParticipants()) {
                messagePersistence.deleteThreadParticipant(threadId, p);
            }
        }
        messagePersistence.deleteMessageThread(threadId);
        return new ServiceResponse<>((Void) null);
    }

    @Override
    public ServiceResponse<List<MessageThread>> getAllParticipatingThreads(Long userId) {
        return new ServiceResponse<>(messagePersistence.selectAllThreadsWithParticipatingUser(userId));
    }

    @Override
    public ServiceResponse<EntityId> createMessage(Long threadId, Message m) {
        m.setSent(LocalDateTime.now());
        return new ServiceResponse<>(new EntityId(messagePersistence.insertMessage(threadId, m)));
    }

    @Override
    public ServiceResponse<Void> deleteMessage(Long threadId, Long messageId) {
        messagePersistence.deleteMessage(threadId, messageId);
        return new ServiceResponse<>((Void) null);
    }

    @Override
    public ServiceResponse<Void> replaceMessage(Long threadId, Long messageId, Message m) {
        messagePersistence.updateMessage(threadId, messageId, m);
        return new ServiceResponse<>((Void) null);
    }

    @Override
    public ServiceResponse<Message> getMessage(Long threadId, Long messageId) {
        Message m = messagePersistence.selectMessage(threadId, messageId);
        if(null == m) {
            return new ServiceResponse<>(
                    StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{MESSAGE, messageId}));
        }
        return new ServiceResponse<>(m);
    }

    @Override
    public ServiceResponse<List<Message>> getMessages(Long threadId) {
        return new ServiceResponse<>(messagePersistence.selectMessages(threadId));
    }

    @Override
    public ServiceResponse<List<Message>> getUnreadMessages(Long threadId, Long userId) {
        return new ServiceResponse<>(messagePersistence.selectUnreadMessages(threadId, userId));
    }

    @Override
    public ServiceResponse<List<Message>> getAllUnreadMessagesAllThreads(Long userId) {
        return new ServiceResponse<>(messagePersistence.selectUnreadMessages(userId));
    }

    @Override
    public ServiceResponse<Void> markMessageReadByUser(Long threadId, Long messageId, Long userId) {
        Message m = messagePersistence.selectMessage(threadId, messageId);
        if(null == m) {
            return new ServiceResponse<>(
                    StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{ MESSAGE, messageId}));
        }
        MessageThread thread = m.getThread();
        if(null == thread.getParticipants()) {
            thread.setParticipants(new HashSet<>());
        }
        MessageReadState rs = new MessageReadState();
        rs.setMessageId(m.getId());
        rs.setParticipantId(userId);
        rs.setReadOn(LocalDateTime.now());
        if(null == m.getReadStateList()) {
            m.setReadStateList(new HashSet<>());
        }
        if(!m.getReadStateList().contains(rs)) {
            m.getReadStateList().add(rs);
        }
        messagePersistence.updateMessage(threadId, messageId, m);
        return new ServiceResponse<>((Void) null);
    }
}
