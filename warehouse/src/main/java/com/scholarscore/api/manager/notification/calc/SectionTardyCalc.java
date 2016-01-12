package com.scholarscore.api.manager.notification.calc;

import com.scholarscore.api.manager.OrchestrationManager;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.AttendanceTypes;
import com.scholarscore.models.notification.Notification;
import com.scholarscore.models.notification.TriggeredNotification;
import com.scholarscore.models.user.Person;

import java.util.List;

/**
 * Created by markroper on 1/12/16.
 */
public class SectionTardyCalc extends AttendanceCalcBase implements NotificationCalculator {
    @Override
    public List<TriggeredNotification> calculate(
            List<? extends Person> subjects, Notification notification, OrchestrationManager manager) {
        return calculate(subjects, notification, manager, AttendanceStatus.TARDY, AttendanceTypes.SECTION);
    }

    public List<TriggeredNotification> calculateTimeWindowTriggerdNotifications(
            Notification notification, OrchestrationManager manager, List<Long> studentIds) {
        return null;
    }
}
