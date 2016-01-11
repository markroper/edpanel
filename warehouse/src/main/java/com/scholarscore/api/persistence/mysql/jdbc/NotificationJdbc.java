package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.NotificationPersistence;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.notification.Notification;
import com.scholarscore.models.notification.TriggeredNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by markroper on 1/10/16.
 */
@Transactional
public class NotificationJdbc implements NotificationPersistence {
    private static final String NOTIFICATION_BASE_HQL = "select n from " + HibernateConsts.NOTIFICATION_TABLE +
            " join fetch owner o left join fetch o.contactMethods left join fetch o.homeAddress" +
            " join fetch subscribers s" +
            " join fetch subjects su ";
    private static final String TRIGGERED_NOTIFICATION_BASE_HQL = "select t from " +
            HibernateConsts.TRIGGERED_NOTIFICATION_TABLE +
            " join fetch notification n" +
            " join fetch n.owner o left join fetch o.contactMethods left join fetch o.homeAddress" +
            " join fetch n.subscribers s" +
            " join fetch n.subjects su";

    @Autowired
    private HibernateTemplate hibernateTemplate;

    public NotificationJdbc() {
    }

    public NotificationJdbc(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    @Override
    public Notification select(long notificationId) {
        return hibernateTemplate.get(Notification.class, notificationId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Notification> selectAllForUser(long userId) {
        return (List<Notification>)hibernateTemplate.findByNamedParam(
                NOTIFICATION_BASE_HQL + " where o.id = :userId", "userId", userId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Notification> selectAll() {
        return (List<Notification>) hibernateTemplate.find(NOTIFICATION_BASE_HQL);
    }

    @Override
    public Long insertNotification(Notification notification) {
        Notification n = this.hibernateTemplate.merge(notification);
        return n.getId();
    }

    @Override
    public void replaceNotification(long notificationId, Notification notification) {
        notification.setId(notificationId);
        hibernateTemplate.merge(notification);
    }

    @Override
    public void deleteNotification(long notificationId) {
        Notification n = select(notificationId);
        if(null != n) {
            hibernateTemplate.delete(n);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TriggeredNotification> selectTriggeredForUser(long userId, boolean includeInactive) {
        if(includeInactive) {
            return (List<TriggeredNotification>) hibernateTemplate.findByNamedParam(
                    TRIGGERED_NOTIFICATION_BASE_HQL + " where t.userIdToNotify = :userId", "userId", userId);
        } else {
            return (List<TriggeredNotification>) hibernateTemplate.findByNamedParam(
                    TRIGGERED_NOTIFICATION_BASE_HQL + " where t.isActive = (1) and t.userIdToNotify = :userId", "userId", userId);
        }
    }

    @Override
    public TriggeredNotification selectTriggered(long triggeredId) {
        return hibernateTemplate.get(TriggeredNotification.class, triggeredId);
    }

    @Override
    public Long insertTriggeredNotification(long notificationId, long userId, TriggeredNotification triggered) {
        TriggeredNotification n = this.hibernateTemplate.merge(triggered);
        return n.getId();
    }

    @Override
    public void updateTriggeredNotification(long triggeredNotificationId, TriggeredNotification updated) {
        updated.setId(triggeredNotificationId);
        hibernateTemplate.merge(updated);
    }

    @Override
    public void deleteTriggeredNotification(long triggeredNotificationId) {
        TriggeredNotification n = hibernateTemplate.get(TriggeredNotification.class, triggeredNotificationId);
        hibernateTemplate.delete(n);
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }
}
