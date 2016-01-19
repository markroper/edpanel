package com.scholarscore.api.manager.notification.calc;

import com.scholarscore.api.manager.OrchestrationManager;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.gpa.Gpa;
import com.scholarscore.models.notification.Notification;
import com.scholarscore.models.notification.TriggeredNotification;
import com.scholarscore.models.notification.window.Duration;
import com.scholarscore.models.notification.window.NotificationWindow;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.user.Person;
import org.apache.commons.lang3.tuple.MutablePair;
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
public class GpaCalc implements NotificationCalculator {
    private final static Logger LOGGER = LoggerFactory.getLogger(GpaCalc.class);

    @Override
    public List<TriggeredNotification> calculate(
            List<? extends Person> subjects, Notification notification, OrchestrationManager manager) {
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
            //There is no time window, so we're getting the latest values and comparing against the trigger value
            ServiceResponse<Collection<Gpa>> gpaResp =
                    manager.getGpaManager().getAllGpasForStudents(studentIds, null, null);
            Double gpaSum = 0D;
            if(null != gpaResp.getValue() && !gpaResp.getValue().isEmpty()) {
                if(null == agg) {
                    //If there is no aggregate function, calculate per student
                    List<TriggeredNotification> triggered = new ArrayList<>();
                    for(Gpa gpa: gpaResp.getValue()) {
                        if ((triggerValue >= gpa.getScore() && !notification.getTriggerWhenGreaterThan()) ||
                                (triggerValue <= gpa.getScore() && notification.getTriggerWhenGreaterThan())) {
                            List<TriggeredNotification> t =
                                    NotificationCalculator.createTriggeredNotifications(
                                            notification, gpa.getScore(), manager, gpa.getStudentId());
                            if(null != t) {
                                triggered.addAll(t);
                            }
                        }
                    }
                    if(!triggered.isEmpty()) {
                        return triggered;
                    }
                } else {
                    for (Gpa gpa : gpaResp.getValue()) {
                        gpaSum += gpa.getScore();
                    }
                    //If the aggregate function is average, adjust the triggered value, otherwise, assume SUM
                    if (AggregateFunction.AVG.equals(agg)) {
                        gpaSum = gpaSum / gpaResp.getValue().size();
                    }
                    //Only trigger if the GPA calc is less than or equal to the trigger value
                    if ((triggerValue >= gpaSum && !notification.getTriggerWhenGreaterThan()) ||
                            (triggerValue <= gpaSum && notification.getTriggerWhenGreaterThan())) {
                        return NotificationCalculator.createTriggeredNotifications(notification, gpaSum, manager);
                    }
                }
            }

        }
        return null;
    }

    private List<TriggeredNotification> calculateTimeWindowTriggerdNotifications(
            Notification notification, OrchestrationManager manager, List<Long> studentIds) {
        AggregateFunction agg = notification.getAggregateFunction();
        NotificationWindow window = notification.getWindow();
        Double triggerValue = notification.getTriggerValue();
        Duration dur = window.getWindow();
        Boolean isPercent = window.getTriggerIsPercent();
        //Resolve the start date for the time window for the notification
        LocalDate start = NotificationCalculator.resolveStartDate(dur, manager, notification);
        //Get all the student GPAs within the date range
        ServiceResponse<Collection<Gpa>> gpaResp =
                manager.getGpaManager().getAllGpasForStudents(studentIds, start, LocalDate.now());
        Map<Long, MutablePair<Gpa, Gpa>> gpasByStudent = new HashMap<>();
        //Find the oldest and newest GPA for each student and put them in a map
        if(null != gpaResp.getValue()) {
            Collection<Gpa> gpas = gpaResp.getValue();
            for(Gpa gpa : gpas) {
                if(!gpasByStudent.containsKey(gpa.getStudentId())) {
                    gpasByStudent.put(gpa.getStudentId(), new MutablePair<>(gpa, gpa));
                }
                MutablePair<Gpa, Gpa> startAndEnd = gpasByStudent.get(gpa.getStudentId());
                //If the current start GPA for the student is after the GPA calc date, replace it
                if(startAndEnd.getLeft().getCalculationDate().isAfter(gpa.getCalculationDate())) {
                    startAndEnd.setLeft(gpa);
                }
                //If the current end GPA for the student is
                if(startAndEnd.getRight().getCalculationDate().isBefore(gpa.getCalculationDate())) {
                    startAndEnd.setRight(gpa);
                }
            }
            if(null == agg) {
                List<TriggeredNotification> triggered = new ArrayList<>();
                //Now that we have the oldest and newest GPA for each student within out time window,
                //sum the start and end values for each student.
                for(Map.Entry<Long, MutablePair<Gpa, Gpa>> entry : gpasByStudent.entrySet()) {
                    Double startValue = entry.getValue().getLeft().getScore();
                    Double endValue = entry.getValue().getRight().getScore();
                    List<TriggeredNotification> t = genTriggeredNotifications(
                            isPercent, notification, endValue, startValue, manager, entry.getKey());
                    if(null != t) {
                        triggered.addAll(t);
                    }
                }
                if(!triggered.isEmpty()) {
                    return triggered;
                }
            } else {
                Double startValue = 0D;
                Double endValue = 0D;
                //Now that we have the oldest and newest GPA for each student within out time window,
                //sum the start and end values for each student.
                for(Map.Entry<Long, MutablePair<Gpa, Gpa>> entry : gpasByStudent.entrySet()) {
                    startValue += entry.getValue().getLeft().getScore();
                    endValue += entry.getValue().getRight().getScore();
                }
                //If the aggregate function is average, divide by size to get the average GPA for students
                //Otherwise assume we're dealing with a SUM.
                if(AggregateFunction.AVG.equals(agg)) {
                    startValue = startValue / gpasByStudent.size();
                    endValue = endValue / gpasByStudent.size();
                }
                return genTriggeredNotifications(isPercent, notification, endValue, startValue, manager, null);
            }
        } else {
            LOGGER.warn("Unexpectedly null GPA response for student list query");
            return null;
        }
        return null;
    }

    private static List<TriggeredNotification> genTriggeredNotifications(
            Boolean isPercent, Notification notification, Double endValue, Double startValue, OrchestrationManager manager, Long subjectFk) {
        //If the Notification is triggered on percent change, we need to calculate the
        //percent difference between the start and end values and compare that pct to the trigger value.
        //Otherwise we compare the absolute value of endValue - startValue to the trigger value.
        if(null != isPercent && isPercent) {
            //Calculate pct different between start and end date
            if(notification.getTriggerValue() <= Math.abs(1D - (endValue / startValue))) {
                return NotificationCalculator.createTriggeredNotifications(
                        notification, 1D - (endValue / startValue), manager, subjectFk);
            }
        } else {
            //abs value of difference between
            if(notification.getTriggerValue() <= Math.abs(endValue - startValue)) {
                return NotificationCalculator.
                        createTriggeredNotifications(notification, endValue - startValue, manager, subjectFk);
            }
        }
        return null;
    }
}
