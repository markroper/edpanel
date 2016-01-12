package com.scholarscore.api.manager.notification;

import com.scholarscore.api.manager.OrchestrationManager;
import com.scholarscore.api.manager.notification.calc.AssignmentGradeCalc;
import com.scholarscore.api.manager.notification.calc.BehaviorScoreCalc;
import com.scholarscore.api.manager.notification.calc.GpaCalc;
import com.scholarscore.api.manager.notification.calc.HwCompletionCalc;
import com.scholarscore.api.manager.notification.calc.NotificationCalculator;
import com.scholarscore.api.manager.notification.calc.SchoolAbsenceCalc;
import com.scholarscore.api.manager.notification.calc.SchoolTardyCalc;
import com.scholarscore.api.manager.notification.calc.SectionAbsenceCalc;
import com.scholarscore.api.manager.notification.calc.SectionGradeCalc;
import com.scholarscore.api.manager.notification.calc.SectionTardyCalc;
import com.scholarscore.models.notification.Notification;
import com.scholarscore.models.notification.TriggeredNotification;
import com.scholarscore.models.user.Person;

import java.util.List;

/**
 * Created by markroper on 1/11/16.
 */
public class NotificationTriggerEvaluator {

    OrchestrationManager manager;

    public NotificationTriggerEvaluator(OrchestrationManager manager) {
        this.manager = manager;
    }

    /**
     * Given a Notification instance, the method evaluates whether that notification
     * should is triggered, given the current state of the underlying data.  If it is triggered,
     * a list of TriggeredNotifications is generated and returned with one triggered notification
     * generated per individual subscriber to the notification.
     *
     * @param notification The notification to evaluate
     * @return
     */
    public List<TriggeredNotification> evaluate(Notification notification) {
        List<? extends Person> subjects =
                NotificationCalculator.resolveGroupMembers(
                        notification.getSubjects(), notification.getSchoolId(), manager);
        //Create the correct factory
        NotificationCalculator calculator;
        switch(notification.getMeasure()) {
            case GPA:
                calculator = new GpaCalc();
                break;
            case SECTION_GRADE:
                calculator = new SectionGradeCalc();
                break;
            case ASSIGNMENT_GRADE:
                calculator = new AssignmentGradeCalc();
                break;
            case BEHAVIOR_SCORE:
                calculator = new BehaviorScoreCalc();
                break;
            case HOMEWORK_COMPLETION:
                calculator = new HwCompletionCalc();
                break;
            case SCHOOL_ABSENCE:
                calculator = new SchoolAbsenceCalc();
                break;
            case SCHOOL_TARDY:
                calculator = new SchoolTardyCalc();
                break;
            case SECTION_ABSENCE:
                calculator = new SectionAbsenceCalc();
                break;
            case SECTION_TARDY:
                calculator = new SectionTardyCalc();
                break;
            default:
                calculator = null;
                break;
        }
        if(null != calculator) {
            return calculator.calculate(subjects, notification, manager);
        }
        return null;
    }
}
