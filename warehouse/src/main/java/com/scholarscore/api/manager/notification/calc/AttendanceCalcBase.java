package com.scholarscore.api.manager.notification.calc;

import com.scholarscore.api.manager.OrchestrationManager;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.AttendanceTypes;
import com.scholarscore.models.notification.Notification;
import com.scholarscore.models.notification.TriggeredNotification;
import com.scholarscore.models.notification.window.Duration;
import com.scholarscore.models.notification.window.NotificationWindow;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.user.Person;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by markroper on 1/12/16.
 */
public abstract class AttendanceCalcBase {
    abstract List<TriggeredNotification> calculateTimeWindowTriggerdNotifications(
            Notification notification, OrchestrationManager manager, List<Long> studentIds);

    public List<TriggeredNotification> calculate(
            List<? extends Person> subjects, Notification notification,
            OrchestrationManager manager, AttendanceStatus status, AttendanceTypes type) {
        AggregateFunction agg = notification.getAggregateFunction();
        Double triggerValue = notification.getTriggerValue();
        NotificationWindow window = notification.getWindow();
        List<Long> studentIds = new ArrayList<>();
        for(Person p : subjects) {
            studentIds.add(p.getId());
        }
        //If the Notification uses a time window, calculate appropriately
        if(null != window) {
            return calculateTimeWindowTriggerdNotifications(notification, manager, studentIds);
        } else {
            LocalDate start = NotificationCalculator.resolveStartDate(Duration.YEAR, manager, notification);
            ServiceResponse<Collection<Attendance>> attendanceResp =
                    manager.getAttendanceManager().getAllStudentAttendanceInRange(
                            notification.getSchoolId(), studentIds, start, LocalDate.now());
            if(null != attendanceResp.getValue()) {
                Double sumAttendance = 0D;
                for(Attendance a : attendanceResp.getValue()) {
                    if(type.equals(a.getType())
                            && status.equals(a.getStatus())) {
                        // Don't count this entry if we're dealing with section type and the notification
                        // section id doesn't equal the attendance section id
                        if(null != notification.getSectionId()
                                && null != a.getSection()
                                && !notification.getSectionId().equals(a.getSection().getId())) {
                            continue;
                        }
                        sumAttendance++;
                    }
                }
                //If the aggregate function is average, adjust the triggered value, otherwise, assume SUM
                if(AggregateFunction.AVG.equals(agg)) {
                    sumAttendance = sumAttendance / studentIds.size();
                }
                //Only trigger if the GPA calc is less than or equal to the trigger value
                if((triggerValue >= sumAttendance && !notification.getTriggeWhenGreaterThan()) ||
                        (triggerValue <= sumAttendance && notification.getTriggeWhenGreaterThan())) {
                    return NotificationCalculator.createTriggeredNotifications(notification, sumAttendance, manager);
                }
            }

        }
        return null;
    }
}
