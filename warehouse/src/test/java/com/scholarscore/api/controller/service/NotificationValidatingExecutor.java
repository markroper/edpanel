package com.scholarscore.api.controller.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.notification.Notification;
import com.scholarscore.models.notification.TriggeredNotification;
import com.scholarscore.models.notification.group.SectionStudents;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by markroper on 1/11/16.
 */
public class NotificationValidatingExecutor {

    private final IntegrationBase serviceBase;

    public NotificationValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }

    public void evaluateNotifications(Long schoolId) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.POST,
                serviceBase.getNotificationEndpoint() + "/schools/" + schoolId + "/evaluations",
                null,
                null);
    }

    public void createGoalNotifications(long schoolId, long studentId, long goalId) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.POST,
                serviceBase.getNotificationEndpoint() + "/schools/" + schoolId +
                        "/students/"+ studentId + "/goals/" + goalId,
                null,
                null);
    }

    public Notification create(Notification s, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.POST,
                serviceBase.getNotificationEndpoint(),
                null,
                s);
            EntityId surveyId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(surveyId, "unexpected null day ID returned from create call for case: " + msg);
        return retrieveAndValidateCreatedNotification(s, surveyId, msg);
    }

    public void createNegative(Notification s, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.POST,
                serviceBase.getNotificationEndpoint(),
                null,
                s);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected status code returned for case: " + msg);
    }

    public Notification get(long nid, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getNotificationEndpoint(nid),
                null);
        Notification n = serviceBase.validateResponse(response, new TypeReference<Notification>(){});
        Assert.assertNotNull(n, "Unexpected null notifications for case: " + msg);
        return n;
    }

    public void getNegative(long nid, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getNotificationEndpoint(nid),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected status code returned while retrieving survey: " + msg);
    }

    public List<Notification> getByUserId(long userId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getNotificationEndpoint() + "/users/" + userId ,
                null);
        List<Notification> nots = serviceBase.validateResponse(response, new TypeReference<List<Notification>>(){});
        Assert.assertNotNull(nots, "Unexpected null notifications for case: " + msg);
        return nots;
    }

    public List<Notification> getAll(String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getNotificationEndpoint(),
                null);
        List<Notification> nots = serviceBase.validateResponse(response, new TypeReference<List<Notification>>(){});
        Assert.assertNotNull(nots, "Unexpected null notifications for case: " + msg);
        return nots;
    }

    public void delete(long nid, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.DELETE,
                serviceBase.getNotificationEndpoint(nid));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(nid, HttpStatus.NOT_FOUND, msg);
    }

    public void update(Notification n, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT,
                serviceBase.getNotificationEndpoint(n.getId()),
                null,
                n);
        Void nid = serviceBase.validateResponse(response, new TypeReference<Void>(){});
        retrieveAndValidateCreatedNotification(n, new EntityId(n.getId()), msg);
    }

    /*
        TRIGGERED NOTIFICATIONS
     */

    public List<TriggeredNotification> getTriggeredNotificationsForUser(Long userId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getNotificationEndpoint() + "/users/" + userId + "/triggerednotifications",
                null);
        List<TriggeredNotification> n = serviceBase.validateResponse(response, new TypeReference<List<TriggeredNotification>>(){});
        Assert.assertNotNull(n, "Unexpected null triggered notifications list for case: " + msg);
        return n;
    }

    public void disableTriggeredNotification(Long notificationId, Long triggeredId, Long userId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.PUT,
                serviceBase.getNotificationEndpoint(notificationId) +
                        "/triggerednotifications/" + triggeredId + "/users/" + userId,
                null);
        Void n = serviceBase.validateResponse(response, new TypeReference<Void>(){});
        Assert.assertNull(n, "Unexpected null triggered notifications list for case: " + msg);
    }

    public Notification retrieveAndValidateCreatedNotification(Notification submitted, EntityId id, String msg) {
        submitted.setId(id.getId());
        if(null != submitted.getSection()) {
            submitted.getSection().setEnrolledStudents(new ArrayList<>());
        }
        if(submitted.getSubjects() instanceof SectionStudents) {
            ((SectionStudents) submitted.getSubjects()).getSection().setEnrolledStudents(new ArrayList<>());
        }
        if(submitted.getSubscribers() instanceof SectionStudents) {
            ((SectionStudents) submitted.getSubscribers()).getSection().setEnrolledStudents(new ArrayList<>());
        }
        Notification created = this.get(id.getId(), msg);
        submitted.getSubjects().setId(created.getSubjects().getId());
        submitted.getSubscribers().setId(created.getSubscribers().getId());
        Assert.assertEquals(created, submitted, msg + " - these should be equal but they are not:\nCREATED:\n"
                + created + "\n" + "SUBMITTED:\n" + submitted);
        return created;
    }
}
