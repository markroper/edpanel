package com.scholarscore.api.persistence;

import com.scholarscore.models.message.Message;
import com.scholarscore.models.message.MessageThread;
import com.scholarscore.models.message.MessageThreadParticipant;

import java.util.List;

/**
 * Created by markroper on 1/17/16.
 */
public interface MessagePersistence {
    //THREAD RELATED
    Long insertMessageThread(MessageThread t);
    MessageThread selectMessageThread(long threadId);
    void updateMessageThread(MessageThread t);
    void deleteMessageThread(long threadId);
    List<MessageThread> selectAllThreadsWithParticipatingUser(long userId);
    void deleteThreadParticipant(long threadId, MessageThreadParticipant p);
    //MESSAGE RELATED
    Long insertMessage(long threadId, Message m);
    void deleteMessage(long threadId, long messageId);
    void updateMessage(long threadId, long messageId, Message m);
    Message selectMessage(long threadId, long messageId);
    List<Message> selectMessages(long threadId);
    List<Message> selectUnreadMessages(long threadId, long userId);
    List<Message> selectUnreadMessages(long userId);
}
