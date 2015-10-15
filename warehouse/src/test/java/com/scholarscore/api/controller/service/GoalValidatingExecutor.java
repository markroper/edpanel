package com.scholarscore.api.controller.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.*;
import com.scholarscore.models.goal.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import java.util.ArrayList;

/**
 * Created by cwallace on 9/21/2015.
 */
public class GoalValidatingExecutor {
    private final IntegrationBase serviceBase;

    public GoalValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }

    public Goal create(Long studentId, Goal goal, String msg) {
        //Create the behavior
        ResultActions response = serviceBase.makeRequest(HttpMethod.POST, serviceBase.getGoalEndpoint(studentId), null, goal);
        EntityId returnedGoalId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(returnedGoalId, "unexpected null ID returned from create call for case: " + msg);
        return retrieveAndValidateCreatedGoal(studentId, returnedGoalId.getId(), goal, HttpMethod.POST, msg);
    }

    public Goal get(Long studentId, Long goalId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getGoalEndpoint(studentId, goalId),
                null);
        Goal goal = serviceBase.validateResponse(response, new TypeReference<Goal>() {
        });
        Assert.assertNotNull(goal, "Unexpected null behavior returned for case: " + msg);

        return goal;
    }

    public void delete(Long studentId, Long goalId, String msg) {
        //Delete the behavior
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE,
                serviceBase.getGoalEndpoint(studentId, goalId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(studentId, goalId, HttpStatus.NOT_FOUND, msg);
    }

    public void getAll(Long studentId, String msg, int numberOfItems) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getGoalEndpoint(studentId),
                null);
        ArrayList<Goal> goals = serviceBase.validateResponse(response, new TypeReference<ArrayList<Goal>>() {
        });
        Assert.assertNotNull(goals, "Unexpected null behavior returned for case: " + msg);
        Assert.assertEquals(goals.size(), numberOfItems, "Unexpected number of items returned for case: " + msg);
    }

    /**
     * Given an ID and the value submitted to a GET/PUT endpoint, retrieve the behavior via GET, validate it, and
     * return it to the caller.
     *
     * @param schoolYearId
     * @param submittedSchoolYear
     * @param msg
     * @return
     */
    protected Goal retrieveAndValidateCreatedGoal(Long studentId, Long goalId, Goal submittedGoal, HttpMethod method, String msg) {
        //Retrieve and validate the created behavior
        Goal createdGoal = this.get(studentId, goalId, msg);
        Goal expectedGoal = generateExpectationGoal(submittedGoal, createdGoal, method);
        Assert.assertEquals(createdGoal, expectedGoal, "Unexpected behavior created for case: " + msg);

        return createdGoal;

    }

        /**
         * Given a submitted behavior object and an behavior instance returned by the API after creation,
         * this method returns a new Behavior instance that represents the expected state of the submitted
         * Behavior after creation.  The reason that there are differences in the submitted and expected
         * instances is that there may be system assigned values not in the initially submitted object, for
         * example, the id property.
         *
         * @param submitted
         * @param created
         * @return
         */
        protected Goal generateExpectationGoal(Goal submitted, Goal created, HttpMethod method) {

            Goal returnGoal;
            if (submitted instanceof BehaviorGoal) {
                returnGoal = new BehaviorGoal((BehaviorGoal)submitted);
            } else if (submitted instanceof AssignmentGoal) {
                returnGoal = new AssignmentGoal((AssignmentGoal)submitted);
            } else if (submitted instanceof CumulativeGradeGoal){
                returnGoal =  new CumulativeGradeGoal((CumulativeGradeGoal)submitted);
            } else if (submitted instanceof AttendanceGoal){
                returnGoal = new AttendanceGoal((AttendanceGoal)submitted);
            } else {
                return null;
            }
            //TODO Should make a factory constructor for goals

            if(method == HttpMethod.PATCH) {
                returnGoal.mergePropertiesIfNull(created);
            } else if(null == returnGoal.getId()) {
                returnGoal.setId(created.getId());
                returnGoal.setCalculatedValue(created.getCalculatedValue());
            }
            return returnGoal;
        }

    public void getNegative(Long studentId, Long goalId, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getGoalEndpoint(studentId, goalId),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected status code returned while retrieving behavior: " + msg);
    }

    public Goal replace(Long studentId, Long goalId, Goal goal, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT,
                serviceBase.getGoalEndpoint(studentId, goalId),
                null,
                goal);
        EntityId goalIdEntity = serviceBase.validateResponse(response, new TypeReference<EntityId>() {
        });
        Assert.assertNotNull(goalId, "unexpected null goal ID returned from create call for case: " + msg);
        return retrieveAndValidateCreatedGoal(studentId, goalIdEntity.getId(), goal, HttpMethod.PUT, msg);
    }

    public Goal update(Long studentId, Long goalId, Goal goal, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PATCH,
                serviceBase.getGoalEndpoint(studentId, goalId),
                null,
                goal);
        EntityId goalEntityId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(goalId, "unexpected null section assignment ID returned from create call for case: " + msg);
        return retrieveAndValidateCreatedGoal(studentId, goalEntityId.getId(),goal, HttpMethod.PATCH, msg);
    }

}
