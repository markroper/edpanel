package com.scholarscore.api.manager.notification.calc;

import com.scholarscore.api.manager.OrchestrationManager;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.PrepScore;
import com.scholarscore.models.notification.Notification;
import com.scholarscore.models.notification.TriggeredNotification;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.user.Person;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by markroper on 1/12/16.
 */
public class BehaviorScoreCalc implements NotificationCalculator {
    private final static Logger LOGGER = LoggerFactory.getLogger(BehaviorScoreCalc.class);

    @Override
    public List<TriggeredNotification> calculate(
            List<? extends Person> subjects, Notification notification, OrchestrationManager manager) {
        List<Long> studentIds = new ArrayList<>();
        for(Person p : subjects) {
            studentIds.add(p.getId());
        }
        if(null != notification.getWindow()) {
            return calculateTimeWindowTriggeredNotifications(studentIds, notification, manager);
        }
        AggregateFunction agg = notification.getAggregateFunction();
        LocalDate startDate = LocalDate.now().minusDays(6);
        Long[] studIds = new Long[studentIds.size()];
        Date start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        ServiceResponse<List<PrepScore>> resp =
                manager.getStudentManager().getStudentPrepScore(studentIds.toArray(studIds), start, end);
        if(null != resp.getValue()) {
            if(null == agg) {
                //If there is no aggregate function, evaluate each student in the group independently
                List<TriggeredNotification> triggered = new ArrayList<>();
                Map<Long, Double> studentToScore = new HashMap<>();
                for(PrepScore score: resp.getValue()) {
                    Double curr = studentToScore.get(score.getStudentId());
                    if(null == curr) {
                        curr = 0D;
                    }
                    studentToScore.put(score.getStudentId(), ++curr);
                }
                for(Map.Entry<Long, Double> entry: studentToScore.entrySet()) {
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
                Double triggeredValue = 0D;
                for(PrepScore score: resp.getValue()) {
                    triggeredValue += score.getScore();
                }
                //If the aggregate function is average, adjust the triggered value, otherwise, assume SUM
                if(AggregateFunction.AVG.equals(notification.getAggregateFunction())) {
                    triggeredValue = triggeredValue / resp.getValue().size();
                }
                //Only trigger if the GPA calc is less than or equal to the trigger value
                if((notification.getTriggerValue() >= triggeredValue && !notification.getTriggerWhenGreaterThan()) ||
                        (notification.getTriggerValue() <= triggeredValue && notification.getTriggerWhenGreaterThan())) {
                    return NotificationCalculator.createTriggeredNotifications(notification, triggeredValue, manager);
                }
            }
        }
        return null;
    }

    private List<TriggeredNotification> calculateTimeWindowTriggeredNotifications(
            List<Long> studentIds, Notification notification, OrchestrationManager manager){
        Boolean isPercent = notification.getWindow().getTriggerIsPercent();
        Double triggerValue = notification.getTriggerValue();
        AggregateFunction agg = notification.getAggregateFunction();
        LocalDate startDate = NotificationCalculator.resolveStartDate(
                notification.getWindow().getWindow(), manager, notification);
        Long[] studIds = new Long[studentIds.size()];
        Date start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        ServiceResponse<List<PrepScore>> resp =
            manager.getStudentManager().getStudentPrepScore(studentIds.toArray(studIds), start, end);
        Map<Long, MutablePair<PrepScore, PrepScore>> scoresByStudent = new HashMap<>();
        if(null != resp.getValue()) {
            for(PrepScore score: resp.getValue()) {
                if(!scoresByStudent.containsKey(score.getStudentId())) {
                    scoresByStudent.put(score.getStudentId(), new MutablePair<>(score, score));
                }
                MutablePair<PrepScore, PrepScore> startAndEnd = scoresByStudent.get(score.getStudentId());
                if(score.getStartDate().isBefore(startAndEnd.getLeft().getStartDate())) {
                    startAndEnd.setLeft(score);
                }
                if(score.getStartDate().isAfter(startAndEnd.getRight().getStartDate())) {
                    startAndEnd.setRight(score);
                }
            }
            if(null == agg) {
                //Calculate each student separately
                List<TriggeredNotification> triggered = new ArrayList<>();
                for(Map.Entry<Long, MutablePair<PrepScore, PrepScore>> entry : scoresByStudent.entrySet()) {
                    Long startValue = entry.getValue().getLeft().getScore();
                    Long endValue = entry.getValue().getRight().getScore();
                    List<TriggeredNotification> t = genTriggeredNotifications(
                            startValue.doubleValue(), endValue.doubleValue(), manager, isPercent, notification, entry.getKey());
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
                for(Map.Entry<Long, MutablePair<PrepScore, PrepScore>> entry : scoresByStudent.entrySet()) {
                    startValue += entry.getValue().getLeft().getScore();
                    endValue += entry.getValue().getRight().getScore();
                }
                //If the aggregate function is average, divide by size to get the average GPA for students
                //Otherwise assume we're dealing with a SUM.
                if(AggregateFunction.AVG.equals(agg)) {
                    startValue = startValue / scoresByStudent.size();
                    endValue = endValue / scoresByStudent.size();
                }
                return genTriggeredNotifications(startValue, endValue, manager, isPercent, notification, null);
            }
        } else {
            LOGGER.warn("No Behavior scores found in range.");
            return null;
        }
        return null;
    }

    private static List<TriggeredNotification> genTriggeredNotifications(
            Double startValue, Double endValue, OrchestrationManager manager, Boolean isPercent, Notification notification, Long subjectId) {
        //If the Notification is triggered on percent change, we need to calculate the
        //percent difference between the start and end values and compare that pct to the trigger value.
        //Otherwise we compare the absolute value of endValue - startValue to the trigger value.
        if(null != isPercent && isPercent) {
            //Calculate pct different between start and end date
            if(notification.getTriggerValue() <= Math.abs(1D - (endValue / startValue))) {
                return NotificationCalculator.createTriggeredNotifications(
                        notification, 1D - (endValue / startValue), manager, subjectId);
            }
        } else {
            //abs value of difference between
            if(notification.getTriggerValue() <= Math.abs(endValue - startValue)) {
                return NotificationCalculator.createTriggeredNotifications(
                        notification, endValue - startValue, manager, subjectId);
            }
        }
        return null;
    }

}
