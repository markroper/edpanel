package com.scholarscore.api.manager.notification.calc;

import com.scholarscore.api.manager.OrchestrationManager;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.attendance.Attendance;
import com.scholarscore.models.attendance.AttendanceStatus;
import com.scholarscore.models.attendance.AttendanceType;
import com.scholarscore.models.notification.Notification;
import com.scholarscore.models.notification.TriggeredNotification;
import com.scholarscore.models.notification.window.Duration;
import com.scholarscore.models.notification.window.NotificationWindow;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.user.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by markroper on 1/12/16.
 */
public abstract class AttendanceCalcBase {
    private final static Logger LOGGER = LoggerFactory.getLogger(AttendanceCalcBase.class);

    public List<TriggeredNotification> calculate(
            List<? extends Person> subjects, Notification notification,
            OrchestrationManager manager, AttendanceStatus status, AttendanceType type) {
        AggregateFunction agg = notification.getAggregateFunction();
        Double triggerValue = notification.getTriggerValue();
        NotificationWindow window = notification.getWindow();
        List<Long> studentIds = new ArrayList<>();
        for(Person p : subjects) {
            studentIds.add(p.getId());
        }
        //If the Notification uses a time window, calculate appropriately
        LocalDate start = null;
        if(null != window) {
            if(window.getTriggerIsPercent()) {
                LOGGER.warn("Percent change not supported for attendance notifications");
                return null;
            }
            start = NotificationCalculator.resolveStartDate(notification.getWindow().getWindow(), manager, notification);
        } else {
            start = NotificationCalculator.resolveStartDate(Duration.YEAR, manager, notification);
        }
        ServiceResponse<Collection<Attendance>> attendanceResp =
                manager.getAttendanceManager().getAllStudentAttendanceInRange(
                        notification.getSchoolId(), studentIds, start, LocalDate.now());
        if(null != attendanceResp.getValue()) {
            if(null == agg) {
                List<TriggeredNotification> triggered = new ArrayList<>();
                Map<Long, Double> studentToAttendanceCount = new HashMap<>();
                //If there is no aggregate function, evaluate each subject separately
                for(Attendance a : attendanceResp.getValue()) {
                    if(type.equals(a.getType())
                            && status.equals(a.getStatus())) {
                        // Don't count this entry if we're dealing with section type and the notification
                        // section id doesn't equal the attendance section id
                        if(null != notification.getSection()
                                && null != a.getSection()
                                && !notification.getSection().getId().equals(a.getSection().getId())) {
                            continue;
                        }
                        Double curr = studentToAttendanceCount.get(a.getStudent().getId());
                        if(null == curr) {
                            studentToAttendanceCount.put(a.getStudent().getId(), 0D);
                            curr = 0D;
                        }
                        studentToAttendanceCount.put(a.getStudent().getId(), ++curr);
                    }
                }
                for(Map.Entry<Long, Double> entry: studentToAttendanceCount.entrySet()) {
                    Double triggeredValue = entry.getValue();
                    if((triggeredValue <= notification.getTriggerValue() && !notification.getTriggerWhenGreaterThan()) ||
                            (triggeredValue >= notification.getTriggerValue() && notification.getTriggerWhenGreaterThan())) {
                        triggered.addAll(NotificationCalculator.createTriggeredNotifications(
                                notification, triggeredValue, manager, entry.getKey()));
                    }
                }
                if(!triggered.isEmpty()) {
                    return triggered;
                }
            } else {
                Double sumAttendance = 0D;
                for(Attendance a : attendanceResp.getValue()) {
                    if(type.equals(a.getType())
                            && status.equals(a.getStatus())) {
                        // Don't count this entry if we're dealing with section type and the notification
                        // section id doesn't equal the attendance section id
                        if(null != notification.getSection()
                                && null != a.getSection()
                                && !notification.getSection().getId().equals(a.getSection().getId())) {
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
                if((triggerValue >= sumAttendance && !notification.getTriggerWhenGreaterThan()) ||
                        (triggerValue <= sumAttendance && notification.getTriggerWhenGreaterThan())) {
                    return NotificationCalculator.createTriggeredNotifications(notification, sumAttendance, manager);
                }
            }
        }
        return null;
    }
}
