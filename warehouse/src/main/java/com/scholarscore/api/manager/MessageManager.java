package com.scholarscore.api.manager;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.message.Message;
import com.scholarscore.models.message.MessageThread;

import java.util.List;

/**
 * Created by markroper on 1/17/16.
 */
public interface MessageManager {
    //OPERATIONS ON THREADS
    ServiceResponse<EntityId> createMessageThread(MessageThread t);
    ServiceResponse<Void> replaceMessageThread(Long threadId, MessageThread t);
    ServiceResponse<Void> deleteMessageThread(Long threadId);
    ServiceResponse<List<MessageThread>> getAllParticipatingThreads(Long userId);
    //OPERATIONS ON MESSAGES
    ServiceResponse<EntityId> createMessage(Long threadId, Message m);
    ServiceResponse<Void> deleteMessage(Long threadId, Long messageId);
    ServiceResponse<Void> replaceMessage(Long threadId, Long messageId, Message m);
    ServiceResponse<Message>  getMessage(Long threadId, Long messageId);
    ServiceResponse<List<Message>> getMessages(Long threadId);
    ServiceResponse<List<Message>> getUnreadMessages(Long threadId, Long userId);
    ServiceResponse<List<Message>> getAllUnreadMessagesAllThreads(Long userId);
    ServiceResponse<Void> markMessageReadByUser(Long threadId, Long messageId, Long userId);
}
