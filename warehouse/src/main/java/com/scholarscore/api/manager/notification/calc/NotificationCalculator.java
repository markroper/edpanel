package com.scholarscore.api.manager.notification.calc;

import com.scholarscore.api.manager.OrchestrationManager;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import com.scholarscore.models.notification.Notification;
import com.scholarscore.models.notification.TriggeredNotification;
import com.scholarscore.models.notification.group.FilteredStudents;
import com.scholarscore.models.notification.group.NotificationGroup;
import com.scholarscore.models.notification.group.SchoolAdministrators;
import com.scholarscore.models.notification.group.SchoolTeachers;
import com.scholarscore.models.notification.group.SectionStudents;
import com.scholarscore.models.notification.group.SingleAdministrator;
import com.scholarscore.models.notification.group.SingleStudent;
import com.scholarscore.models.notification.group.SingleTeacher;
import com.scholarscore.models.notification.window.Duration;
import com.scholarscore.models.user.Person;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by markroper on 1/12/16.
 */
public interface NotificationCalculator {

    /**
     * Given a triggered notification, resolve the recipient list, generates and returns a list of
     * TriggeredNotification instances, one for each recipient in the recipient list. If the Notification
     * has not been triggered, null is returned.
     *
     * @param subjects The subjects with whose data we are evaluating against the notification trigger
     * @param notification The notification definition
     * @param manager An manager that can be used to resolve dependencies
     * @return
     */
    List<TriggeredNotification> calculate(
            List<? extends Person> subjects, Notification notification, OrchestrationManager manager);

    /**
     * If a notification has been triggered, this method generates and returns the list of TriggeredNotifications
     * for the subscribers to the notification.
     *
     * @param n  The Notification that has been triggered
     * @param triggeringValue The triggering value
     * @param manager The manager used to resolve dependent entities
     * @return
     */
    static List<TriggeredNotification> createTriggeredNotifications(
            Notification n, Double triggeringValue, OrchestrationManager manager) {
        return createTriggeredNotifications(n, triggeringValue, manager, null);
    }

    static List<TriggeredNotification> createTriggeredNotifications(
            Notification n, Double triggeringValue, OrchestrationManager manager, Long subjectId) {
        List<? extends Person> recipients = resolveGroupMembers(n.getSubscribers(), n.getSchoolId(), manager);
        if(null == recipients) {
            return null;
        }
        List<TriggeredNotification> notifications = new ArrayList<>();
        for(Person p: recipients) {
            TriggeredNotification tr = new TriggeredNotification();
            tr.setIsActive(true);
            tr.setNotification(n);
            tr.setTriggeredDate(LocalDate.now());
            tr.setUserIdToNotify(p.getId());
            tr.setValueWhenTriggered(triggeringValue);
            tr.setSubjectUserId(subjectId);
            notifications.add(tr);
        }
        return notifications;
    }

    /**
     * Given a group and a schoolId, resolves the list of Person instances
     * that are currently members of the group defined.
     *
     * @param group Required.  The group being measured in a notification
     * @param schoolId Optional.  The school to limit the group resolution to, if any
     * @return
     */
    static List<? extends Person> resolveGroupMembers(NotificationGroup group, Long schoolId, OrchestrationManager manager) {
        if(group instanceof FilteredStudents) {
            ServiceResponse<Collection<Student>> studsResp =
                    manager.getStudentManager().getStudents(schoolId, (FilteredStudents)group);
            if(null != studsResp.getValue()) {
                return new ArrayList<>(studsResp.getValue());
            }
        } else if(group instanceof SchoolAdministrators) {
            ServiceResponse<Collection<Staff>> adminsResp = manager.getAdminManager().getAllAdministrators();
            if(null != adminsResp) {
                return new ArrayList<>(adminsResp.getValue());
            }
        } else if(group instanceof SchoolTeachers) {
            ServiceResponse<Collection<Staff>> teachersResp = manager.getTeacherManager().getAllTeachers();
            if(null != teachersResp.getValue()) {
                return new ArrayList<>(teachersResp.getValue());
            }
        } else if(group instanceof SectionStudents) {
            ServiceResponse<Section> sectResp =
                    manager.getSectionManager().getSection(schoolId, -1L, -1L, ((SectionStudents) group).getSection().getId());
            if(null != sectResp.getValue()) {
                Section s = sectResp.getValue();
                return s.getEnrolledStudents();
            }
        } else if(group instanceof SingleAdministrator) {
            ServiceResponse<Staff> adminResp =
                    manager.getAdminManager().getAdministrator(((SingleAdministrator) group).getAdministratorId());
            if(null != adminResp.getValue()) {
                Staff a = adminResp.getValue();
                return Arrays.asList(a);
            }
        } else if(group instanceof SingleStudent) {
            ServiceResponse<Student> studResp =
                    manager.getStudentManager().getStudent(((SingleStudent) group).getStudent().getId());
            if(null != studResp.getValue()) {
                Student s = studResp.getValue();
                return Arrays.asList(s);
            }
        } else if(group instanceof SingleTeacher) {
            ServiceResponse<Staff> teacherResp =
                    manager.getTeacherManager().getTeacher(((SingleTeacher) group).getTeacherId());
            if(null != teacherResp.getValue()) {
                Staff t = teacherResp.getValue();
                return Arrays.asList(t);
            }
        }
        return null;
    }

    static LocalDate resolveStartDate(
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
