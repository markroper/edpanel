package com.scholarscore.api.manager.notification.calc;

import com.scholarscore.api.manager.OrchestrationManager;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.grade.StudentSectionGrade;
import com.scholarscore.models.notification.Notification;
import com.scholarscore.models.notification.TriggeredNotification;
import com.scholarscore.models.notification.window.NotificationWindow;
import com.scholarscore.models.query.AggregateFunction;
import com.scholarscore.models.user.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
        //TODO: we need to make student section grades like GPA, one per day synced from PS
        return null;
    }
}
