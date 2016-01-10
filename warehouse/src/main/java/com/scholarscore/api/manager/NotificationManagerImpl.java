package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.NotificationPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.notification.Notification;
import com.scholarscore.models.notification.TriggeredNotification;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by markroper on 1/10/16.
 */
public class NotificationManagerImpl implements NotificationManager {
    @Autowired
    private NotificationPersistence notificationPersistence;

    @Autowired
    private OrchestrationManager pm;

    private static final String NOTIFICATION = "Notification";

    public void setNotificationPersistence(NotificationPersistence notificationPersistence) {
        this.notificationPersistence = notificationPersistence;
    }

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }

    @Override
    public ServiceResponse<Notification> getNotification(Long notificationId) {
        return null;
    }

    @Override
    public ServiceResponse<List<Notification>> getAllNotificationsForUser(Long userId) {
        return null;
    }

    @Override
    public ServiceResponse<List<Notification>> getAllNotifications() {
        return null;
    }

    @Override
    public ServiceResponse<Void> evaluateNotifications(Long schoolId) {
        return null;
    }

    @Override
    public ServiceResponse<EntityId> createNotification(Notification notification) {
        return null;
    }

    @Override
    public ServiceResponse<Void> replaceNotification(Long notificationId, Notification notification) {
        return null;
    }

    @Override
    public ServiceResponse<Void> deleteNotification(Long notificationId) {
        return null;
    }

    @Override
    public ServiceResponse<List<TriggeredNotification>> getAllTriggeredNotificationsForUser(Long userId, Boolean includeInactive) {
        return null;
    }

    @Override
    public ServiceResponse<Void> dismissTriggeredNotification(Long notificationId, Long triggeredId, Long userId) {
        return null;
    }
}
