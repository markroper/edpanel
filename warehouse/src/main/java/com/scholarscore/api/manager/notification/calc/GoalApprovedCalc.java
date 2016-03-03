package com.scholarscore.api.manager.notification.calc;

import com.scholarscore.api.manager.OrchestrationManager;
import com.scholarscore.models.notification.Notification;
import com.scholarscore.models.notification.TriggeredNotification;
import com.scholarscore.models.user.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cwallace on 3/1/16.
 */
public class GoalApprovedCalc implements NotificationCalculator {
    private final static Logger LOGGER = LoggerFactory.getLogger(GpaCalc.class);

    @Override
    public List<TriggeredNotification> calculate(
            List<? extends Person> subjects, Notification notification, OrchestrationManager manager) {
        List<TriggeredNotification> triggered = new ArrayList<>();
        //If this is a one time goal (probably yes) and already triggered we don't want to create more triggered noti
        if (notification.getOneTime() && notification.getTriggered()) {
            return null;
        }
        for(Person p : subjects) {
            List<TriggeredNotification> t =
                    NotificationCalculator.createTriggeredNotifications(
                            notification, 0D, manager, p.getId());
            if (null != t) {
                triggered.addAll(t);
            }
        }
        //These should only activate once, so when we make teh triggered notification kill the real one
        //manager.getNotificationManager().deleteNotification(notification.getId());

        if (triggered.isEmpty()) {
            return null;
        } else {
            return triggered;
        }
    }

}
