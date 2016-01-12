package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.NotificationPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
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

    private static final String NOTIFICATION = "notification";
    private static final String TRIGGERED_NOTIFICATION = "triggered notification";

    public void setNotificationPersistence(NotificationPersistence notificationPersistence) {
        this.notificationPersistence = notificationPersistence;
    }

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }

    @Override
    public ServiceResponse<Notification> getNotification(Long notificationId) {
        Notification n = notificationPersistence.select(notificationId);
        if(null == n) {
            return new ServiceResponse<>(
                    StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{ NOTIFICATION, notificationId}));
        };
        return new ServiceResponse<>(n);
    }

    @Override
    public ServiceResponse<List<Notification>> getAllNotificationsForUser(Long userId) {
        StatusCode code = pm.getUserManager().userExists(userId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(notificationPersistence.selectAllForUser(userId));
    }

    @Override
    public ServiceResponse<List<Notification>> getAllNotifications() {
        return new ServiceResponse<>(notificationPersistence.selectAll());
    }

    @Override
    public ServiceResponse<Void> evaluateNotifications(Long schoolId) {
        //TODO: implement the notification evaluation
        ServiceResponse<List<Notification>> notificationResponse = getAllNotifications();
        if(null != notificationResponse.getValue()) {
            List<Notification> notifications = notificationResponse.getValue();

        } else {
            return new ServiceResponse<>(notificationResponse.getCode());
        }
        return null;
    }

    @Override
    public ServiceResponse<EntityId> createNotification(Notification notification) {
        return new ServiceResponse<>(new EntityId(notificationPersistence.insertNotification(notification)));
    }

    @Override
    public ServiceResponse<Void> replaceNotification(Long notificationId, Notification notification) {
        notificationPersistence.replaceNotification(notificationId, notification);
        return new ServiceResponse<>((Void) null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ServiceResponse<Void> deleteNotification(Long notificationId) {
        notificationPersistence.deleteNotification(notificationId);
        return new ServiceResponse<>((Void) null);
    }

    @Override
    public ServiceResponse<List<TriggeredNotification>>
            getAllTriggeredNotificationsForUser(Long userId, Boolean includeInactive) {
        StatusCode code = pm.getUserManager().userExists(userId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(notificationPersistence.selectTriggeredForUser(userId ,includeInactive));
    }

    @Override
    public ServiceResponse<Void> dismissTriggeredNotification(Long notificationId, Long triggeredId, Long userId) {
        StatusCode code = pm.getUserManager().userExists(userId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        TriggeredNotification tn = notificationPersistence.selectTriggered(triggeredId);
        if(null == tn) {
            code = StatusCodes.getStatusCode(
                            StatusCodeType.MODEL_NOT_FOUND,
                            new Object[]{ TRIGGERED_NOTIFICATION, triggeredId});
            return new ServiceResponse<>(code);
        }
        tn.setIsActive(false);
        notificationPersistence.updateTriggeredNotification(notificationId, tn);
        return new ServiceResponse<>((Void) null);
    }
}
