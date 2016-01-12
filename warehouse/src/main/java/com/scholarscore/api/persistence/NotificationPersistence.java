package com.scholarscore.api.persistence;

import com.scholarscore.models.notification.Notification;
import com.scholarscore.models.notification.TriggeredNotification;

import java.util.List;

/**
 * Created by markroper on 1/10/16.
 */
public interface NotificationPersistence {

    Notification select(long notification);

    List<Notification> selectAllForUser(long userId);

    List<Notification> selectAll();

    Long insertNotification(Notification notification);

    void replaceNotification(long notificationId, Notification notification);

    void deleteNotification(long notificationId);

    /*
        TRIGGERED NOTIFICATION RELATED
     */
    List<TriggeredNotification> selectTriggeredForUser(long userId, Boolean includeInactive);

    TriggeredNotification selectTriggered(long triggeredId);

    Long insertTriggeredNotification(long notificationId, long userId, TriggeredNotification triggered);

    void updateTriggeredNotification(long triggeredNotificationId, TriggeredNotification updated);

    void deleteTriggeredNotification(long triggeredNotificationId);

}
