package com.scholarscore.api.manager.notification.calc;

import com.scholarscore.api.manager.OrchestrationManager;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.notification.Notification;
import com.scholarscore.models.notification.TriggeredNotification;
import com.scholarscore.models.notification.window.Duration;
import com.scholarscore.models.notification.window.NotificationWindow;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.ui.ScoreAsOfWeek;
import com.scholarscore.models.user.Person;
import org.apache.commons.lang3.tuple.MutablePair;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by markroper on 1/12/16.
 */
public class HwCompletionCalc implements NotificationCalculator {
    @Override
    public List<TriggeredNotification> calculate(List<? extends Person> subjects, Notification notification, OrchestrationManager manager) {
        AggregateFunction agg = notification.getAggregateFunction();
        Double triggerValue = notification.getTriggerValue();
        NotificationWindow window = notification.getWindow();
        List<Long> studentIds = new ArrayList<>();
        for(Person p : subjects) {
            studentIds.add(p.getId());
        }

        if(null != window) {
            return calculateTimeWindowTriggered(notification, manager, studentIds);
        } else {
            LocalDate start = NotificationCalculator.resolveStartDate(Duration.YEAR, manager, notification);
            Double sumOfScores = 0D;
            Long numScores = 0L;
            List<TriggeredNotification> triggered = new ArrayList<>();
            for(Long sid: studentIds) {
                ScoreAsOfWeek currScore = null;
                ServiceResponse<List<ScoreAsOfWeek>> scores =
                        manager.getStudentManager().getStudentHomeworkRates(sid, start, LocalDate.now());
                if(null != scores.getValue()) {
                    for(ScoreAsOfWeek score : scores.getValue()) {
                        if(null == currScore || score.getWeekEnding().isAfter(currScore.getWeekEnding())) {
                            currScore = score;
                        }
                    }
                    if(null == agg) {
                        //evaluate the student independent of the group
                        if((triggerValue >= currScore.getScore() && !notification.getTriggerWhenGreaterThan()) ||
                                (triggerValue <= currScore.getScore() && notification.getTriggerWhenGreaterThan())) {
                            List<TriggeredNotification> t = NotificationCalculator.createTriggeredNotifications(
                                    notification, sumOfScores, manager, sid);
                            if(null != t) {
                                triggered.addAll(t);
                            }
                        }
                    } else {
                        sumOfScores += currScore.getScore();
                        numScores++;
                    }
                }
            }
            if(null == agg) {
                if(!triggered.isEmpty()) {
                    return triggered;
                }
            } else {
                //If the aggregate function is average, adjust the triggered value, otherwise, assume SUM
                if (AggregateFunction.AVG.equals(agg)) {
                    sumOfScores = sumOfScores / numScores;
                }
                //Only trigger if the GPA calc is less than or equal to the trigger value
                if ((triggerValue >= sumOfScores && !notification.getTriggerWhenGreaterThan()) ||
                        (triggerValue <= sumOfScores && notification.getTriggerWhenGreaterThan())) {
                    return NotificationCalculator.createTriggeredNotifications(notification, sumOfScores, manager);
                }
            }
        }

        return null;
    }

    private List<TriggeredNotification> calculateTimeWindowTriggered(
            Notification notification, OrchestrationManager manager, List<Long> studentIds) {
        AggregateFunction agg = notification.getAggregateFunction();
        NotificationWindow window = notification.getWindow();
        Double triggerValue = notification.getTriggerValue();
        Duration dur = window.getWindow();
        Boolean isPercent = window.getTriggerIsPercent();
        //Resolve the start date for the time window for the notification
        LocalDate start = NotificationCalculator.resolveStartDate(dur, manager, notification);
        Map<Long, MutablePair<ScoreAsOfWeek, ScoreAsOfWeek>> hwByStudent = new HashMap<>();
        for(Long sid: studentIds) {
            ServiceResponse<List<ScoreAsOfWeek>> scores =
                    manager.getStudentManager().getStudentHomeworkRates(sid, start, LocalDate.now());
            if(null != scores.getValue()) {
                for(ScoreAsOfWeek score : scores.getValue()) {
                    if(!hwByStudent.containsKey(sid)) {
                        hwByStudent.put(sid, new MutablePair<>(score, score));
                    }
                    MutablePair<ScoreAsOfWeek, ScoreAsOfWeek> startAndEnd = hwByStudent.get(sid);
                    if(startAndEnd.getLeft().getWeekEnding().isAfter(score.getWeekEnding())) {
                        startAndEnd.setLeft(score);
                    }
                    if(startAndEnd.getRight().getWeekEnding().isBefore(score.getWeekEnding())) {
                        startAndEnd.setRight(score);
                    }
                }
            }
        }
        if(null == agg) {
            List<TriggeredNotification> triggered = new ArrayList<>();
            //Now that we have the oldest and newest GPA for each student within out time window,
            //sum the start and end values for each student.
            for(Map.Entry<Long, MutablePair<ScoreAsOfWeek, ScoreAsOfWeek>> entry : hwByStudent.entrySet()) {
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
            for(Map.Entry<Long, MutablePair<ScoreAsOfWeek, ScoreAsOfWeek>> entry : hwByStudent.entrySet()) {
                startValue += entry.getValue().getLeft().getScore();
                endValue += entry.getValue().getRight().getScore();
            }
            if(!startValue.equals(0D) && !endValue.equals(0D)) {
                //If the aggregate function is average, divide by size to get the average GPA for students
                //Otherwise assume we're dealing with a SUM.
                if(AggregateFunction.AVG.equals(agg)) {
                    startValue = startValue / hwByStudent.size();
                    endValue = endValue / hwByStudent.size();
                }
                genTriggeredNotifications(isPercent, notification, endValue, startValue, manager, null);
            }
        }
        return null;
    }

    private static List<TriggeredNotification> genTriggeredNotifications(
            Boolean isPercent, Notification notification, Double endValue, Double startValue, OrchestrationManager manager,  Long subjectId) {
        //If the Notification is triggered on percent change, we need to calculate the
        //percent difference between the start and end values and compare that pct to the trigger value.
        //Otherwise we compare the absolute value of endValue - startValue to the trigger value.
        if(null != isPercent && isPercent) {
            //Calculate pct different between start and end date
            if(notification.getTriggerValue() <= Math.abs(1D - (endValue / startValue))) {
                return NotificationCalculator.createTriggeredNotifications(
                        notification, 1D - (endValue / startValue), manager);
            }
        } else {
            //abs value of difference between
            if(notification.getTriggerValue() <= Math.abs(endValue - startValue)) {
                return NotificationCalculator.
                        createTriggeredNotifications(notification, endValue - startValue, manager);
            }
        }
        return null;
    }
}
