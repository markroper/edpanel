package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.MessagePersistence;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.message.Message;
import com.scholarscore.models.message.MessageThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;

import java.util.List;

/**
 * Created by markroper on 1/17/16.
 */
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
        MessageThread thread = this.hibernateTemplate.merge(t);
        return thread.getId();
    }

    @Override
    public void updateMessageThread(MessageThread t) {
        hibernateTemplate.merge(t);
    }

    @Override
    public void deleteMessageThread(long threadId) {
        MessageThread t = hibernateTemplate.get(MessageThread.class, threadId);
        if(null != t) {
            hibernateTemplate.delete(t);
        }
    }

    @Override
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
        this.hibernateTemplate.update(m);
    }

    @Override
    public Message selectMessage(long threadId, long messageId) {
        return hibernateTemplate.get(Message.class, messageId);
    }

    @Override
    public List<Message> selectMessages(long threadId) {
        return(List<Message>)
                hibernateTemplate.findByNamedParam(
                        MESSAGE_HQL + " where :threadId = t.is", "threadId", threadId);
    }

    @Override
    public List<Message> selectUnreadMessages(long threadId, long userId) {
        String[] params = new String[]{"threadId", "userId"};
        Object[] paramValues = new Object[]{ threadId, userId };
        return (List<Message>)
        hibernateTemplate.findByNamedParam(
                MESSAGE_HQL + " where :threadId = t.is and p.participantId = :userId and l.participantId is null"
                , params, paramValues);
    }

    @Override
    public List<Message> selectUnreadMessages(long userId) {
        return (List<Message>)
                hibernateTemplate.findByNamedParam(
                        MESSAGE_HQL + " where p.participantId = :userId and l.participantId is null"
                        , "userIf", userId);
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }
}
