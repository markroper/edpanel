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
public class GoalCreatedCalc implements NotificationCalculator {
    private final static Logger LOGGER = LoggerFactory.getLogger(GpaCalc.class);

    @Override
    public List<TriggeredNotification> calculate(
            List<? extends Person> subjects, Notification notification, OrchestrationManager manager) {
        List<TriggeredNotification> triggered = new ArrayList<>();

        for(Person p : subjects) {
            List<TriggeredNotification> t =
                    NotificationCalculator.createTriggeredNotifications(
                            notification, 0D, manager, p.getId());
            if (null != t) {
                triggered.addAll(t);
            }
        }

        if (triggered.isEmpty()) {
            return null;
        } else {
            return triggered;
        }
    }

}
