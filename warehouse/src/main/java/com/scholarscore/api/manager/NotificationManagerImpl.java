package com.scholarscore.api.manager;

import com.scholarscore.api.manager.notification.NotificationTriggerEvaluator;
import com.scholarscore.api.persistence.NotificationPersistence;
import com.scholarscore.api.security.config.UserDetailsProxy;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.goal.Goal;
import com.scholarscore.models.notification.Notification;
import com.scholarscore.models.notification.NotificationMeasure;
import com.scholarscore.models.notification.TriggeredNotification;
import com.scholarscore.models.notification.group.NotificationGroup;
import com.scholarscore.models.notification.group.NotificationGroupType;
import com.scholarscore.models.notification.group.SingleStudent;
import com.scholarscore.models.notification.group.SingleTeacher;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by markroper on 1/10/16.
 */
public class NotificationManagerImpl implements NotificationManager {
    private final static Logger LOGGER = LoggerFactory.getLogger(NotificationManagerImpl.class);

    @Autowired
    private NotificationPersistence notificationPersistence;

    @Autowired
    private OrchestrationManager pm;

    private static final String NOTIFICATION = "notification";
    private static final String TRIGGERED_NOTIFICATION = "triggered notification";

    public void setNotificationPersistence(NotificationPersistence notificationPersistence) {
        this.notificationPersistence = notificationPersistence;
    }

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }

    @Override
    public ServiceResponse<Notification> getNotification(Long notificationId) {
        //Return the requested notification if the requester is the owner or in the subjects
        Notification n = notificationPersistence.select(notificationId);
        boolean permitted = userCanAccessNotification(n, pm.getUserManager().getCurrentUserDetails());
        if(permitted) {
            return new ServiceResponse<>(n);
        } else {
            return new ServiceResponse<>(
                    StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND,
                            new Object[]{ NOTIFICATION, notificationId}));
        }
    }

    private static final boolean userCanAccessNotification(Notification n, UserDetailsProxy udp) {
        if(null == n || null == udp) {
            return false;
        }
        UserType typ = udp.getUser().getType();
        boolean permitted = false;
        if(udp.getUser().getId().equals(n.getOwner().getId())) {
            permitted = true;
        } else if(typ.equals(UserType.SUPER_ADMIN) ||
                typ.equals(UserType.ADMINISTRATOR) ||
                typ.equals(UserType.TEACHER)) {
            permitted = true;
        } else if(typ.equals(UserType.STUDENT)) {
            NotificationGroup g = n.getSubscribers();
            if(g.getType().equals(NotificationGroupType.SINGLE_STUDENT)) {
                if(udp.getUser().getId().equals(((SingleStudent)g).getStudent().getId())) {
                    permitted = true;
                }
            } else if(g.getType().equals(NotificationGroupType.SECTION_STUDENTS) ||
                    g.getType().equals(NotificationGroupType.FILTERED_STUDENTS)) {
                //if the recipients to be alerted are groups of students, permit the request
                permitted = true;
            }
        }
        return permitted;
    }

    @Override
    public ServiceResponse<List<Notification>> getAllNotificationsForUser(Long userId) {
        StatusCode code = pm.getUserManager().userExists(userId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(notificationPersistence.selectAllForUser(userId));
    }

    @Override
    public ServiceResponse<List<Notification>> getAllNotifications() {
        List<Notification> ns = notificationPersistence.selectAll();
        List<Notification> filtered = new ArrayList<>();
        if( null != ns) {
            UserDetailsProxy udp = pm.getUserManager().getCurrentUserDetails();
            for (Notification n : ns) {
                if (userCanAccessNotification(n, udp)) {
                    filtered.add(n);
                }
            }
        }
        return new ServiceResponse<>(filtered);
    }

    @Override
    public ServiceResponse<Void> evaluateNotifications(Long schoolId) {
        //TODO: this is not thread safe, in fact, its thread cluster-fucked. We need a way to globally lock this call.
        ServiceResponse<List<Notification>> notificationResponse = getAllNotifications();
        if(null != notificationResponse.getValue()) {
            NotificationTriggerEvaluator factory = new NotificationTriggerEvaluator(pm);
            List<Notification> notifications = notificationResponse.getValue();
            //For each notification, evaluate it and create any triggered notifications generated by the evaluation
            for(Notification n: notifications) {
                List<TriggeredNotification> triggeredNotifications = factory.evaluate(n);
                if(null != triggeredNotifications) {
                    //If its not null it was triggered. Set that bad boy to be triggered
                    n.setTriggered(true);
                    replaceNotification(n.getId(),n);
                    //Resolve existing triggered notification for this notifiaiton and store in a map for 0(1) access
                    List<TriggeredNotification> active = notificationPersistence.selectTriggeredActive(n.getId());
                    HashMap<Long, HashMap<Long, TriggeredNotification>> activeMap = new HashMap<>();
                    if(null != active) {
                        for(TriggeredNotification not: active) {
                            if(!activeMap.containsKey(not.getUserIdToNotify())) {
                                activeMap.put(not.getUserIdToNotify(), new HashMap<>());
                            }
                            Long subjectId = not.getSubjectUserId();
                            if(null == subjectId) {
                                subjectId = -1L;
                            }
                            activeMap.get(not.getUserIdToNotify()).put(subjectId, not);
                        }
                    }
                    //For every triggered notification, insert it into the database, first marking any previous
                    //triggered notifications on the same data as inactive so that the most recent triggered notification
                    //is the active triggered notification, which users will see
                    for(TriggeredNotification tr : triggeredNotifications) {
                        try {
                            TriggeredNotification prev = null;
                            if(activeMap.containsKey(tr.getUserIdToNotify())) {
                                Long subjectId = tr.getSubjectUserId();
                                if(null == subjectId) {
                                    subjectId = -1L;
                                }
                                if(activeMap.get(tr.getUserIdToNotify()).containsKey(subjectId)) {
                                    prev = activeMap.get(tr.getUserIdToNotify()).get(subjectId);
                                    tr.setId(prev.getId());
                                    if(!prev.equals(tr)) {
                                        prev.setIsActive(false);
                                        notificationPersistence.updateTriggeredNotification(prev.getId(), prev);
                                    }
                                }
                            }
                            //Only insert if the new triggered notification if there wasn't one already triggered
                            //for this combination of notification, recipient and target or if the values are unequal
                            if(null == prev || !prev.equals(tr)) {
                                tr.setId(null);
                                notificationPersistence.insertTriggeredNotification(n.getId(), n.getOwner().getId(), tr);
                            }
                        } catch(Exception e) {
                            LOGGER.info("Triggered notification not inserted due to: " + e.getMessage());
                        }
                    }

                }

            }
        } else {
            return new ServiceResponse<>(notificationResponse.getCode());
        }
        return new ServiceResponse<>((Void) null);
    }

    @Override
    public ServiceResponse<EntityId> createNotification(Notification notification) {
        return new ServiceResponse<>(new EntityId(notificationPersistence.insertNotification(notification)));
    }

    @Override
    public ServiceResponse<Void> replaceNotification(Long notificationId, Notification notification) {
        ServiceResponse<Notification> nResp = getNotification(notificationId);
        if(null == nResp.getCode() || nResp.getCode().isOK()) {
            UserDetailsProxy udp = pm.getUserManager().getCurrentUserDetails();
            if(userCanAccessNotification(nResp.getValue(), udp)) {
                notificationPersistence.replaceNotification(notificationId, notification);
                return new ServiceResponse<>((Void) null);
            }
        }
        return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.FORBIDDEN, null));
    }

    @Override
    public ServiceResponse<Void> deleteNotification(Long notificationId) {
        ServiceResponse<Notification> nResp = getNotification(notificationId);
        if(null == nResp.getCode() || nResp.getCode().isOK()) {
            UserDetailsProxy udp = pm.getUserManager().getCurrentUserDetails();
            if(userCanAccessNotification(nResp.getValue(), udp)) {
                notificationPersistence.deleteNotification(notificationId);
                return new ServiceResponse<>((Void) null);
            }
        }
        return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.FORBIDDEN, null));
    }

    @Override
    public ServiceResponse<List<TriggeredNotification>>
            getAllTriggeredNotificationsForUser(Long userId, Boolean includeInactive) {
        StatusCode code = pm.getUserManager().userExists(userId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(notificationPersistence.selectTriggeredForUser(userId ,includeInactive));
    }

    @Override
    public ServiceResponse<Void> dismissTriggeredNotification(Long notificationId, Long triggeredId, Long userId) {
        StatusCode code = pm.getUserManager().userExists(userId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        TriggeredNotification tn = notificationPersistence.selectTriggered(triggeredId);
        if(null == tn) {
            code = StatusCodes.getStatusCode(
                            StatusCodeType.MODEL_NOT_FOUND,
                            new Object[]{ TRIGGERED_NOTIFICATION, triggeredId});
            return new ServiceResponse<>(code);
        }
        UserDetailsProxy udp = pm.getUserManager().getCurrentUserDetails();
        UserType typ = udp.getUser().getType();
        if(tn.getUserIdToNotify().equals(udp.getUser().getId()) ||
                UserType.SUPER_ADMIN.equals(typ) ||
                UserType.ADMINISTRATOR.equals(typ) ||
                UserType.TEACHER.equals(typ)) {
            tn.setIsActive(false);
            notificationPersistence.updateTriggeredNotification(triggeredId, tn);
            return new ServiceResponse<>((Void) null);
        }
        return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.FORBIDDEN, null));
    }

    /**
     * There is a host of notifications we need to create when a goal is created,
     * lets generate all of them here.
     * @param goal
     */
    public ServiceResponse<Long> createGoalNotifications(long schoolId, long studentId, long goalId) {

        ServiceResponse<Goal> goalResponse = pm.getGoalManager().getGoal(studentId, goalId);
        if (null != goalResponse.getCode() && !goalResponse.getCode().isOK()) {
            return new ServiceResponse<Long>(goalResponse.getCode());
        }
        Goal goal = goalResponse.getValue();

        ServiceResponse<Student> studResponse = pm.getStudentManager().getStudent(studentId);
        if (null != studResponse.getCode() && !studResponse.getCode().isOK()) {
            return new ServiceResponse<Long>(studResponse.getCode());
        }
        Student student = studResponse.getValue();

        //If there is an advisor, create a notification for the goal being created and one for
        //IF goal is met and not met
        if (goal.getStaff() != null) {
            SingleStudent studentSub = new SingleStudent();
            studentSub.setStudent(student);

            SingleTeacher teacherSub = new SingleTeacher();
            teacherSub.setTeacherId(goal.getStaff().getId());

            Notification goalCreated = new Notification();
            goalCreated.setGoal(goal);
            goalCreated.setCreatedDate(LocalDate.now());
            goalCreated.setTriggerWhenGreaterThan(true);
            goalCreated.setExpiryDate(LocalDate.now().plusMonths(3));
            goalCreated.setMeasure(NotificationMeasure.GOAL_CREATED);
            goalCreated.setName("Goal Created");
            goalCreated.setOwner(goal.getStaff());
            goalCreated.setTriggerValue(-1D);
            goalCreated.setSubjects(studentSub);
            goalCreated.setSchoolId(schoolId);
            goalCreated.setSubscribers(teacherSub);
            goalCreated.setOneTime(true);
            createNotification(goalCreated);

            SingleStudent studentMetSub = new SingleStudent();
            studentMetSub.setStudent(student);

            SingleTeacher teacherMetSub = new SingleTeacher();
            teacherMetSub.setTeacherId(goal.getStaff().getId());

            Notification goalMetAdvisor = new Notification();
            goalMetAdvisor.setGoal(goal);
            goalMetAdvisor.setCreatedDate(LocalDate.now());
            goalMetAdvisor.setTriggerWhenGreaterThan(true);
            goalMetAdvisor.setExpiryDate(LocalDate.now().plusMonths(3));
            goalMetAdvisor.setMeasure(NotificationMeasure.GOAL_MET);
            goalMetAdvisor.setName("Goal Met");
            goalMetAdvisor.setOwner(goal.getStaff());
            goalMetAdvisor.setTriggerValue(-1D);
            goalMetAdvisor.setSubjects(studentMetSub);
            goalMetAdvisor.setSchoolId(schoolId);
            goalMetAdvisor.setSubscribers(teacherMetSub);
            goalMetAdvisor.setOneTime(true);
            createNotification(goalMetAdvisor);

            SingleStudent studentUnMetSub = new SingleStudent();
            studentUnMetSub.setStudent(student);

            SingleTeacher teacherUnMetSub = new SingleTeacher();
            teacherUnMetSub.setTeacherId(goal.getStaff().getId());

            Notification goalUnMetAdvisor = new Notification();
            goalUnMetAdvisor.setGoal(goal);
            goalUnMetAdvisor.setCreatedDate(LocalDate.now());
            goalUnMetAdvisor.setTriggerWhenGreaterThan(true);
            goalUnMetAdvisor.setExpiryDate(LocalDate.now().plusMonths(3));
            goalUnMetAdvisor.setMeasure(NotificationMeasure.GOAL_UNMET);
            goalUnMetAdvisor.setName("Goal UnMet");
            goalUnMetAdvisor.setOwner(goal.getStaff());
            goalUnMetAdvisor.setTriggerValue(-1D);
            goalUnMetAdvisor.setSubjects(studentUnMetSub);
            goalUnMetAdvisor.setSchoolId(schoolId);
            goalUnMetAdvisor.setSubscribers(teacherUnMetSub);
            goalUnMetAdvisor.setOneTime(true);
            createNotification(goalUnMetAdvisor);
        }

        //Goal Approved notification, only notify student that it was approved
        SingleStudent studentGroup = new SingleStudent();
        studentGroup.setStudent(student);

        Notification goalApproved = new Notification();
        goalApproved.setGoal(goal);
        goalApproved.setCreatedDate(LocalDate.now());
        goalApproved.setTriggerWhenGreaterThan(true);
        goalApproved.setExpiryDate(LocalDate.now().plusMonths(3));
        goalApproved.setMeasure(NotificationMeasure.GOAL_APPROVED);
        goalApproved.setName("Goal Approved");
        goalApproved.setOwner(student);
        goalApproved.setTriggerValue(-1D);
        goalApproved.setSubjects(studentGroup);
        goalApproved.setSchoolId(schoolId);
        goalApproved.setSubscribers(studentGroup);
        goalApproved.setOneTime(true);
        createNotification(goalApproved);

        //Goal MEt notification for the student
        SingleStudent studentSub = new SingleStudent();
        studentSub.setStudent(student);
        Notification goalMetStudent = new Notification();
        goalMetStudent.setGoal(goal);
        goalMetStudent.setCreatedDate(LocalDate.now());
        goalMetStudent.setTriggerWhenGreaterThan(true);
        goalMetStudent.setExpiryDate(LocalDate.now().plusMonths(3));
        goalMetStudent.setMeasure(NotificationMeasure.GOAL_MET);
        goalMetStudent.setName("Goal Met");
        goalMetStudent.setOwner(student);
        goalMetStudent.setTriggerValue(-1D);
        goalMetStudent.setSubjects(studentSub);
        goalMetStudent.setSchoolId(schoolId);
        goalMetStudent.setSubscribers(studentSub);
        goalMetStudent.setOneTime(true);
        createNotification(goalMetStudent);

        //Goal not met notification
        SingleStudent studentUnMetSub = new SingleStudent();
        studentUnMetSub.setStudent(student);
        Notification goalUnMetStudent = new Notification();
        goalUnMetStudent.setGoal(goal);
        goalUnMetStudent.setCreatedDate(LocalDate.now());
        goalUnMetStudent.setTriggerWhenGreaterThan(true);
        goalUnMetStudent.setExpiryDate(LocalDate.now().plusMonths(3));
        goalUnMetStudent.setMeasure(NotificationMeasure.GOAL_UNMET);
        goalUnMetStudent.setName("Goal UnMet");
        goalUnMetStudent.setOwner(student);
        goalUnMetStudent.setTriggerValue(-1D);
        goalUnMetStudent.setSubjects(studentUnMetSub);
        goalUnMetStudent.setSchoolId(schoolId);
        goalUnMetStudent.setSubscribers(studentUnMetSub);
        goalUnMetStudent.setOneTime(true);
        createNotification(goalUnMetStudent);

        return new ServiceResponse<Long>(StatusCodes.getStatusCode(StatusCodeType.OK));
    }
}
