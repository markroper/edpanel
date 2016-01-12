package com.scholarscore.api.manager.notification.calc;

import com.scholarscore.api.manager.OrchestrationManager;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Term;
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
        if(null == window) {
            return calculateTimeWindowTriggerdNotifications(notification, manager, studentIds);
        } else {
            //There is no time window, so we're getting the latest values and comparing against the trigger value
            ServiceResponse<Collection<Gpa>> gpaResp =
                    manager.getGpaManager().getAllGpasForStudents(studentIds, null, null);
            Double gpaSum = 0D;
            if(null != gpaResp.getValue() && !gpaResp.getValue().isEmpty()) {
                for(Gpa gpa : gpaResp.getValue()) {
                    gpaSum += gpa.getScore();
                }
                //If the aggregate function is average, adjust the triggered value, otherwise, assume SUM
                if(AggregateFunction.AVG.equals(agg)) {
                    gpaSum = gpaSum / gpaResp.getValue().size();
                }
            }
            //Only trigger if the GPA calc is less than or equal to the trigger value
            if(triggerValue >= gpaSum) {
                return NotificationCalculator.createTriggeredNotifications(notification, gpaSum, manager);
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
        LocalDate start = resolveStartDate(dur, manager, notification);
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
            //If the Notification is triggered on percent change, we need to calculate the
            //percent difference between the start and end values and compare that pct to the trigger value.
            //Otherwise we compare the absolute value of endValue - startValue to the trigger value.
            if(null != isPercent && isPercent) {
                //Calculate pct different between start and end date
                if(triggerValue <= Math.abs(1D - (endValue / startValue))) {
                    return NotificationCalculator.createTriggeredNotifications(
                            notification, 1D - (endValue / startValue), manager);
                }
            } else {
                //abs value of difference between
                if(triggerValue <= Math.abs(endValue - startValue)) {
                    return NotificationCalculator.createTriggeredNotifications(
                            notification, endValue - startValue, manager);
                }
            }
        } else {
            LOGGER.error("Unexpectedly null GPA response for student list query");
            return null;
        }
        return null;
    }

    private LocalDate resolveStartDate(
            Duration dur, OrchestrationManager manager, Notification notification) {
        LocalDate start = LocalDate.now();
        switch(dur) {
            case DAY:
                break;
            case WEEK:
                start = LocalDate.now().minusDays(7);
                break;
            case MONTH:
                start = LocalDate.now().minusMonths(1);
                break;
            case TERM:
            case YEAR:
                ServiceResponse<Collection<SchoolYear>> yearsResp =
                        manager.getSchoolYearManager().getAllSchoolYears(notification.getSchoolId());
                if(null != yearsResp.getValue()) {
                    for(SchoolYear year : yearsResp.getValue()) {
                        if(year.getStartDate().isBefore(start) && year.getEndDate().isAfter(start)) {
                            if(dur.equals(Duration.YEAR)) {
                                //We're in the current year, so use this start date as the start date
                                start = year.getStartDate();
                            } else {
                                //We're in the right year, now find the inner most term
                                Term curr = null;
                                for (Term t : year.getTerms()) {
                                    if (t.getStartDate().isBefore(start) && t.getEndDate().isAfter(start)) {
                                        //Resolve the inner-most term for the purpose of this calculation
                                        if (null == curr ||
                                                java.time.Duration.between(
                                                        curr.getStartDate(), curr.getEndDate()).getSeconds() >
                                                        java.time.Duration.between(
                                                                t.getStartDate(), t.getEndDate()).getSeconds()) {
                                            curr = t;
                                        }
                                    }
                                }
                                start = curr.getStartDate();
                            }
                            break;
                        }
                    }
                }
                break;
        }
        return start;
    }
}
