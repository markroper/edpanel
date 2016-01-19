package com.scholarscore.api.manager.notification.calc;

import com.scholarscore.api.manager.OrchestrationManager;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.notification.Notification;
import com.scholarscore.models.notification.TriggeredNotification;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.user.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by markroper on 1/12/16.
 */
public class AssignmentGradeCalc implements NotificationCalculator {
    private final static Logger LOGGER = LoggerFactory.getLogger(AssignmentGradeCalc.class);
    @Override
    public List<TriggeredNotification> calculate(
            List<? extends Person> subjects, Notification notification, OrchestrationManager manager) {
        if(null == notification.getSchoolId() ||
                null == notification.getSection() ||
                null == notification.getAssignment()) {
            LOGGER.error("An assignment notification with ID: " + notification.getId()
                    + " lacks schoolId, notificationId, or assignmentId");
            return null;
        }
        if(null != notification.getWindow()) {
            LOGGER.warn("An assignment notification with ID: " + notification.getId()
                    + " has a time window, which is ignored and makes no sense in this context");
        }
        AggregateFunction agg = notification.getAggregateFunction();
        Double triggerValue = notification.getTriggerValue();
        List<Long> studentIds = new ArrayList<>();
        for(Person p : subjects) {
            studentIds.add(p.getId());
        }
        ServiceResponse<Collection<StudentAssignment>> assResp =
                manager.getStudentAssignmentManager().getAllStudentAssignments(
                notification.getSchoolId(), -1L, -1L, notification.getSection().getId(), notification.getAssignment().getId());
        if(null != assResp.getValue()) {
            if(null == agg) {
                List<TriggeredNotification> triggered = new ArrayList<>();
                //If there is no aggregate function, evaluate each subject separately
                for(StudentAssignment sa: assResp.getValue()) {
                    if((null != sa.getExempt() && sa.getExempt()) ||
                            null == sa.getAwardedPoints() || null == sa.getAvailablePoints()) {
                        continue;
                    }
                    Double triggeredValue = sa.getAwardedPoints() / sa.getAvailablePoints();
                    //If the calculated value is less than the trigger value, send alert
                    if((triggeredValue <= notification.getTriggerValue() && !notification.getTriggerWhenGreaterThan()) ||
                            (triggeredValue >= notification.getTriggerValue() && notification.getTriggerWhenGreaterThan())) {
                        triggered.addAll(NotificationCalculator.createTriggeredNotifications(
                                notification, triggeredValue, manager, sa.getStudent().getId()));
                    }
                }
                if(!triggered.isEmpty()) {
                    return triggered;
                }
            } else {
                Double triggeredValue = 0D;
                int numAssignments = 0;
                for(StudentAssignment sa: assResp.getValue()) {
                    if((null != sa.getExempt() && sa.getExempt()) ||
                            null == sa.getAwardedPoints() || null == sa.getAvailablePoints()) {
                        continue;
                    }
                    numAssignments++;
                    triggeredValue += sa.getAwardedPoints() / sa.getAvailablePoints();
                }
                //If we're dealing with an average, calculate it, otherwise assume sum
                if(AggregateFunction.AVG.equals(agg) && numAssignments > 0) {
                    triggeredValue = triggerValue / numAssignments;
                }
                //If the calculated value is less than the trigger value, send alert
                if((triggeredValue <= notification.getTriggerValue() && !notification.getTriggerWhenGreaterThan()) ||
                        (triggeredValue >= notification.getTriggerValue() && notification.getTriggerWhenGreaterThan())) {
                    return NotificationCalculator.createTriggeredNotifications(notification, triggeredValue, manager);
                }
            }
        }
        return null;
    }
}
