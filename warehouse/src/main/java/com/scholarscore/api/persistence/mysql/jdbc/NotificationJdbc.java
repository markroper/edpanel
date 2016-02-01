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
    private static final String NOTIFICATION_JOIN_FETCH_FRAGMENT =
            " left join fetch n.owner o left join fetch o.contactMethods left join fetch o.homeAddress" +
            " left join fetch n.subscribers s" +
            " left join fetch n.subjects su " +
            " left join fetch n.section sect left join fetch sect.term term left join fetch term.schoolYear y left join fetch y.school sch left join fetch sch.address " +
            " left join fetch sect.course course left join fetch course.school cSch left join fetch cSch.address " +
            " left join fetch sect.teachers teachers left join fetch teachers.contactMethods left join fetch teachers.homeAddress" +
            " left join fetch n.assignment ass";

    private static final String NOTIFICATION_BASE_HQL = "select n from " + HibernateConsts.NOTIFICATION_TABLE + " n" +
            NOTIFICATION_JOIN_FETCH_FRAGMENT;
    private static final String TRIGGERED_NOTIFICATION_BASE_HQL = "select t from " +
            HibernateConsts.TRIGGERED_NOTIFICATION_TABLE + " t" +
            " join fetch t.notification n" +
            NOTIFICATION_JOIN_FETCH_FRAGMENT;

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
                NOTIFICATION_BASE_HQL + " where n.owner.id = :userId", "userId", userId);
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
    public List<TriggeredNotification> selectTriggeredForUser(long userId, Boolean includeInactive) {
        if(null != includeInactive && includeInactive) {
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
    @SuppressWarnings("unchecked")
    public List<TriggeredNotification> selectTriggeredActive(long notificationFk) {
        return (List<TriggeredNotification>) hibernateTemplate.findByNamedParam(
                TRIGGERED_NOTIFICATION_BASE_HQL +
                " where t.isActive = (1) and t.notification.id = :notificationFk", "notificationFk", notificationFk);
    }

    @Override
    public Long insertTriggeredNotification(long notificationId, long userId, TriggeredNotification triggered) {
        TriggeredNotification n = this.hibernateTemplate.merge(triggered);
        return n.getId();
    }

    @Override
    public void updateTriggeredNotification(long triggeredNotificationId, TriggeredNotification updated) {
        updated.setId(triggeredNotificationId);
        hibernateTemplate.update(updated);
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
