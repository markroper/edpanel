package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.MessagePersistence;
import com.scholarscore.api.security.config.UserDetailsProxy;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.message.Message;
import com.scholarscore.models.message.MessageReadState;
import com.scholarscore.models.message.MessageThread;
import com.scholarscore.models.message.MessageThreadParticipant;
import com.scholarscore.models.user.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        UserDetailsProxy udp = pm.getUserManager().getCurrentUserDetails();
        MessageThread t = messagePersistence.selectMessageThread(threadId);
        UserType typ = udp.getUser().getType();
        if(typ.equals(UserType.ADMINISTRATOR) || typ.equals(UserType.SUPER_ADMIN)) {
            return new ServiceResponse<>(t);
        }
        if(null != t && null != t.getParticipants()) {
            boolean match = false;
            MessageThreadParticipant mp = new MessageThreadParticipant();
            mp.setParticipantId(udp.getUser().getId());
            mp.setThreadId(threadId);
            if(t.getParticipants().contains(mp)) {
                match = true;
            }
            if(match) {
                return new ServiceResponse<>(t);
            }
        }
        return new ServiceResponse<>(
                StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{MESSAGE_THREAD, threadId}));
    }

    @Override
    public ServiceResponse<Void> replaceMessageThread(Long threadId, MessageThread t) {
        ServiceResponse<MessageThread> tResp = getMessageThread(threadId);
        if(null == tResp.getCode() || tResp.getCode().isOK()) {
            messagePersistence.updateMessageThread(t);
            return new ServiceResponse<>((Void) null);
        } else {
            return new ServiceResponse<>(tResp.getCode());
        }
    }

    @Override
    public ServiceResponse<Void> deleteMessageThread(Long threadId) {
        ServiceResponse<MessageThread> tResp = getMessageThread(threadId);
        if(null == tResp.getCode() || tResp.getCode().isOK()) {
            MessageThread t = tResp.getValue();
            if(null != t.getParticipants()) {
                for(MessageThreadParticipant p: t.getParticipants()) {
                    messagePersistence.deleteThreadParticipant(threadId, p);
                }
            }
            messagePersistence.deleteMessageThread(threadId);
            return new ServiceResponse<>((Void) null);
        } else {
            return new ServiceResponse<>(tResp.getCode());
        }
    }

    @Override
    public ServiceResponse<List<MessageThread>> getAllParticipatingThreads(Long userId) {
        return new ServiceResponse<>(messagePersistence.selectAllThreadsWithParticipatingUser(userId));
    }

    @Override
    public ServiceResponse<EntityId> createMessage(Long threadId, Message m) {
        ServiceResponse<MessageThread> tResp = getMessageThread(threadId);
        if(null == tResp.getCode() || tResp.getCode().isOK()) {
            m.setSent(LocalDateTime.now());
            return new ServiceResponse<>(new EntityId(messagePersistence.insertMessage(threadId, m)));
        } else {
            return new ServiceResponse<>(tResp.getCode());
        }
    }

    @Override
    public ServiceResponse<Void> deleteMessage(Long threadId, Long messageId) {
        ServiceResponse<MessageThread> tResp = getMessageThread(threadId);
        if(null == tResp.getCode() || tResp.getCode().isOK()) {
            messagePersistence.deleteMessage(threadId, messageId);
            return new ServiceResponse<>((Void) null);
        } else {
            return new ServiceResponse<>(tResp.getCode());
        }
    }

    @Override
    public ServiceResponse<Void> replaceMessage(Long threadId, Long messageId, Message m) {
        ServiceResponse<Message> mResp = getMessage(threadId, messageId);
        if(mResp.getCode().isOK()) {
            Message msg = mResp.getValue();
            if(msg.getSentBy().equals(pm.getUserManager().getCurrentUserDetails().getUser().getId())) {
                messagePersistence.updateMessage(threadId, messageId, m);
                return new ServiceResponse<>((Void) null);
            } else {
                return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.FORBIDDEN, null));
            }
        } else {
            return new ServiceResponse<>(mResp.getCode());
        }
    }

    @Override
    public ServiceResponse<Message> getMessage(Long threadId, Long messageId) {
        ServiceResponse<MessageThread> tResp = getMessageThread(threadId);
        if(null == tResp.getCode() || tResp.getCode().isOK()) {
            Message m = messagePersistence.selectMessage(threadId, messageId);
            if(null == m) {
                return new ServiceResponse<>(
                        StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{MESSAGE, messageId}));
            }
            return new ServiceResponse<>(m);
        } else {
            return new ServiceResponse<>(tResp.getCode());
        }
    }

    @Override
    public ServiceResponse<List<Message>> getMessages(Long threadId) {
        Long currUserId = pm.getUserManager().getCurrentUserDetails().getUser().getId();
        List<Message> msgs = messagePersistence.selectMessages(threadId);
        UserType t = pm.getUserManager().getCurrentUserDetails().getUser().getType();
        if(t.equals(UserType.SUPER_ADMIN) || t.equals(UserType.ADMINISTRATOR)) {
            return new ServiceResponse<>(msgs);
        }
        List<Message> filtered = new ArrayList<>();
        if(null != msgs) {
            for(Message m: msgs) {
                MessageThreadParticipant p = new MessageThreadParticipant();
                p.setThreadId(m.getThread().getId());
                p.setParticipantId(currUserId);
                if(m.getThread().getParticipants().contains(p)) {
                    filtered = msgs;
                }
                break;
            }
        }
        return new ServiceResponse<>(filtered);
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
        ServiceResponse<Message> mResp = getMessage(threadId, messageId);
        if(null != mResp.getCode() && !mResp.getCode().isOK()) {
            return new ServiceResponse<>(mResp.getCode());
        }
        Message m = mResp.getValue();
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
