package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.MessagePersistence;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.message.Message;
import com.scholarscore.models.message.MessageReadState;
import com.scholarscore.models.message.MessageThread;
import com.scholarscore.models.message.MessageThreadParticipant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by markroper on 1/17/16.
 */
@Transactional
public class MessageJdbc implements MessagePersistence {
    private static final String MESSAGE_HQL = "select m from " + HibernateConsts.MESSAGE_TABLE + " m " +
            " left join fetch m.thread t left join fetch t.participants p left join fetch m.readStateList l";
    private static final String MESSAGE_THREAD_HQL = "select t from " + HibernateConsts.MESSAGE_THREAD_TABLE +
            " t left join fetch t.participants p";

    private static final String PARTICIPANTS_HQL =
            "select p from " + HibernateConsts.MESSAGE_THREAD_PARTICIPANT_TABLE + " p";

    @Autowired
    private HibernateTemplate hibernateTemplate;

    public MessageJdbc() {
    }

    public MessageJdbc(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }


    @Override
    public Long insertMessageThread(MessageThread t) {
        Set<MessageThreadParticipant> ps = t.getParticipants();
        t.setParticipants(null);
        MessageThread thread = this.hibernateTemplate.merge(t);
        if(null != ps) {
            for(MessageThreadParticipant p : ps) {
                p.setThreadId(thread.getId());
            }
            thread.setParticipants(ps);
            this.hibernateTemplate.merge(thread);
        }
        return thread.getId();
    }

    @Override
    public MessageThread selectMessageThread(long threadId) {
        return hibernateTemplate.get(MessageThread.class, threadId);
    }

    @Override
    public void updateMessageThread(MessageThread t) {
        if(null != t.getParticipants()) {
            for(MessageThreadParticipant p: t.getParticipants()) {
                p.setThreadId(t.getId());
            }
        }
        hibernateTemplate.merge(t);
    }

    public void deleteThreadParticipant(long threadId, MessageThreadParticipant p) {
        hibernateTemplate.delete(p);
    }

    @Override
    public void deleteMessageThread(long threadId) {
        MessageThread t = hibernateTemplate.get(MessageThread.class, threadId);
        if(null != t) {
            hibernateTemplate.delete(t);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<MessageThread> selectAllThreadsWithParticipatingUser(long userId) {
        return (List<MessageThread>)
                hibernateTemplate.findByNamedParam(
                        MESSAGE_THREAD_HQL + " where :userId = p.participantId", "userId", userId);
    }

    @Override
    public Long insertMessage(long threadId, Message m) {
        if(null == m.getThread()) {
            m.setThread(new MessageThread());
        }
        m.getThread().setId(threadId);
        Message mess = this.hibernateTemplate.merge(m);
        return mess.getId();
    }

    @Override
    public void deleteMessage(long threadId, long messageId) {
        Message m = selectMessage(threadId, messageId);
        if(null != m) {
            hibernateTemplate.delete(m);
        }
    }

    @Override
    public void updateMessage(long threadId, long messageId, Message m) {
        if(null == m.getThread()) {
            m.setThread(new MessageThread());
        }
        m.getThread().setId(threadId);
        m.setId(messageId);
        this.hibernateTemplate.merge(m);
    }

    @Override
    public Message selectMessage(long threadId, long messageId) {
        return hibernateTemplate.get(Message.class, messageId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Message> selectMessages(long threadId) {
        Set<Message> messageSet = new HashSet<>();
        List<Message> messageList = (List<Message>)
                hibernateTemplate.findByNamedParam(
                        MESSAGE_HQL + " where :threadId = t.id", "threadId", threadId);
        for(Message m: messageList) {
            if(!messageSet.contains(m)) {
                messageSet.add(m);
            }
        }
        return new ArrayList<>(messageSet);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Message> selectUnreadMessages(long threadId, long userId) {
        String[] params = new String[]{ "threadId", "userId" };
        Object[] paramValues = new Object[]{ threadId, userId };
        Set<Message> messageSet = new HashSet<>();
        List<Message> messageList = (List<Message>) hibernateTemplate.findByNamedParam(
                MESSAGE_HQL + " where :threadId = t.id and p.participantId = :userId",
                params, paramValues);
        for(Message m: messageList) {
            if(!messageSet.contains(m)) {
                if(null != m.getReadStateList()) {
                    boolean hasSeen = false;
                    for(MessageReadState s: m.getReadStateList()) {
                        if(s.getParticipantId().equals(userId)) {
                            hasSeen = true;
                            break;
                        }
                    }
                    if(!hasSeen) {
                        messageSet.add(m);
                    }
                } else {
                    messageSet.add(m);
                }
            }
        }
        return new ArrayList<>(messageSet);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Message> selectUnreadMessages(long userId) {
        Set<Message> messageSet = new HashSet<>();
        List<Message> messageList = (List<Message>) hibernateTemplate.findByNamedParam(
                MESSAGE_HQL + " where p.participantId = :userId", "userId", userId);
        for(Message m: messageList) {
            if(!messageSet.contains(m)) {
                if(null != m.getReadStateList()) {
                    boolean hasSeen = false;
                    for(MessageReadState s: m.getReadStateList()) {
                        if(s.getParticipantId().equals(userId)) {
                            hasSeen = true;
                            break;
                        }
                    }
                    if(!hasSeen) {
                        messageSet.add(m);
                    }
                } else {
                    messageSet.add(m);
                }
            }
        }
        return new ArrayList<>(messageSet);
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }
}
