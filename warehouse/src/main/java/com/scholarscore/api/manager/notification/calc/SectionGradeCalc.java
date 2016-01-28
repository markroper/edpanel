package com.scholarscore.api.manager.notification.calc;

import com.scholarscore.api.manager.OrchestrationManager;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.grade.SectionGrade;
import com.scholarscore.models.grade.StudentSectionGrade;
import com.scholarscore.models.notification.Notification;
import com.scholarscore.models.notification.TriggeredNotification;
import com.scholarscore.models.notification.window.Duration;
import com.scholarscore.models.notification.window.NotificationWindow;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.ui.ScoreAsOfWeek;
import com.scholarscore.models.user.Person;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by markroper on 1/12/16.
 */
public class SectionGradeCalc implements NotificationCalculator {
    private final static Logger LOGGER = LoggerFactory.getLogger(SectionGradeCalc.class);

    @Override
    public List<TriggeredNotification> calculate(List<? extends Person> subjects, Notification notification, OrchestrationManager manager) {
        if(null == notification.getSection()) {
            LOGGER.warn("Section grade notification with no section encountered and unable to be evaluated for this reason.");
            return null;
        }
        AggregateFunction agg = notification.getAggregateFunction();
        Double triggerValue = notification.getTriggerValue();
        NotificationWindow window = notification.getWindow();
        List<Long> studentIds = new ArrayList<>();
        for (Person p : subjects) {
            studentIds.add(p.getId());
        }
        if (null != window) {
            return calculateTimeWindowTriggered(notification, manager, studentIds);
        } else {
            if(null == agg) {
                List<TriggeredNotification> triggered = new ArrayList<>();
                for(Long sid: studentIds) {
                    ServiceResponse<StudentSectionGrade> ssgResp =
                            manager.getStudentSectionGradeManager().getStudentSectionGrade(
                                    notification.getSchoolId(), -1L, -1L, notification.getSection().getId(), sid);
                    if(null != ssgResp.getValue()) {
                        Double grade = ssgResp.getValue().getOverallGrade().getScore();
                        if ((triggerValue >= grade && !notification.getTriggerWhenGreaterThan()) ||
                                (triggerValue <= grade && notification.getTriggerWhenGreaterThan())) {
                            triggered.addAll(NotificationCalculator.createTriggeredNotifications(
                                    notification, grade, manager, sid));
                        }
                    }
                }
                if(!triggered.isEmpty()) {
                    return triggered;
                }
            } else {
                Double gradeSum = 0D;
                Long num = 0L;
                for(Long sid: studentIds) {
                    ServiceResponse<StudentSectionGrade> ssgResp =
                            manager.getStudentSectionGradeManager().getStudentSectionGrade(
                                    notification.getSchoolId(), -1L, -1L, notification.getSection().getId(), sid);
                    if(null != ssgResp.getValue()) {
                        num++;
                        gradeSum += ssgResp.getValue().getOverallGrade().getScore();
                    }
                }
                //If the aggregate function is average, adjust the triggered value, otherwise, assume SUM
                if(AggregateFunction.AVG.equals(agg) && num > 0) {
                    gradeSum = gradeSum / num;
                }
                if((triggerValue >= gradeSum && !notification.getTriggerWhenGreaterThan()) ||
                        (triggerValue <= gradeSum && notification.getTriggerWhenGreaterThan())) {
                    return NotificationCalculator.createTriggeredNotifications(notification, gradeSum, manager);
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
        Map<Long, MutablePair<SectionGrade, SectionGrade>> gradesByStudent = new HashMap<>();
        for(Long sid: studentIds) {
            ServiceResponse<List<ScoreAsOfWeek>> scores =
                    manager.getStudentManager().getStudentHomeworkRates(sid, start, LocalDate.now());
            SectionGrade beforeGrade = manager.getStudentSectionGradeManager().
                    getStudentSectionGradeAsOfDate(sid, notification.getSection().getId(), start);
            if(null == beforeGrade) {
                beforeGrade = new SectionGrade();
            }
            ServiceResponse<StudentSectionGrade> currGrade =
                    manager.getStudentSectionGradeManager().getStudentSectionGrade(
                            notification.getSchoolId(), -1L, -1L, notification.getSection().getId(), sid);
            SectionGrade afterGrade = new SectionGrade();
            if(null != currGrade.getValue()) {
                afterGrade = currGrade.getValue().getOverallGrade();
            }
            gradesByStudent.put(sid, new MutablePair<>(beforeGrade, afterGrade));
        }
        if(null == agg) {
            List<TriggeredNotification> triggered = new ArrayList<>();
            //Now that we have the oldest and newest GPA for each student within out time window,
            //sum the start and end values for each student.
            for(Map.Entry<Long, MutablePair<SectionGrade, SectionGrade>> entry : gradesByStudent.entrySet()) {
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
            for(Map.Entry<Long, MutablePair<SectionGrade, SectionGrade>> entry : gradesByStudent.entrySet()) {
                startValue += entry.getValue().getLeft().getScore();
                endValue += entry.getValue().getRight().getScore();
            }
            if(!startValue.equals(0D) && !endValue.equals(0D)) {
                //If the aggregate function is average, divide by size to get the average GPA for students
                //Otherwise assume we're dealing with a SUM.
                if(AggregateFunction.AVG.equals(agg)) {
                    startValue = startValue / gradesByStudent.size();
                    endValue = endValue / gradesByStudent.size();
                }
                return genTriggeredNotifications(isPercent, notification, endValue, startValue, manager, null);
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
                        notification, 1D - (endValue / startValue), manager, subjectId);
            }
        } else {
            //abs value of difference between
            if(notification.getTriggerValue() <= Math.abs(endValue - startValue)) {
                return NotificationCalculator.
                        createTriggeredNotifications(notification, endValue - startValue, manager, subjectId);
            }
        }
        return null;
    }
}
