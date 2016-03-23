package com.scholarscore.api.manager;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.notification.Notification;
import com.scholarscore.models.notification.TriggeredNotification;

import java.util.List;

/**
 * Created by markroper on 1/10/16.
 */
public interface NotificationManager {
    ServiceResponse<Notification> getNotification(Long notificationId);

    ServiceResponse<List<Notification>> getAllNotificationsForUser(Long userId);

    ServiceResponse<List<Notification>> getAllNotifications();

    ServiceResponse<Void> evaluateNotifications(Long schoolId);

    ServiceResponse<EntityId> createNotification(Notification notification);

    ServiceResponse<Void> replaceNotification(Long notificationId, Notification notification);

    ServiceResponse<Void> deleteNotification(Long notificationId);

    ServiceResponse<List<TriggeredNotification>> getAllTriggeredNotificationsForUser(Long userId, Boolean includeInactive);

    ServiceResponse<Void> dismissTriggeredNotification(Long notificationId, Long triggeredId, Long userId);

    public ServiceResponse<Long> createGoalNotifications(long schoolId, long studentId, long goalId);
}
