package com.scholarscore.api.controller.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.survey.Survey;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by markroper on 12/16/15.
 */
public class SurveyValidatingExecutor {
    private final IntegrationBase serviceBase;

    public SurveyValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }

    public Survey create(Survey s, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.POST,
                serviceBase.getSurveyEndpoint(),
                null,
                s);
        EntityId surveyId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(surveyId, "unexpected null day ID returned from create call for case: " + msg);
        return retrieveAndValidateCreatedSurvey(s, surveyId, msg);
    }

    public void createNegative(Survey s, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.POST,
                serviceBase.getSurveyEndpoint(),
                null,
                s);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected status code returned for case: " + msg);
    }

    public Survey get(long sid, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getSurveyEndpoint(sid),
                null);
        Survey survey = serviceBase.validateResponse(response, new TypeReference<Survey>(){});
        Assert.assertNotNull(survey, "Unexpected null day for case: " + msg);
        return survey;
    }

    public List<Survey> getByUserId(long userId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getSurveyEndpointWithUserId(userId),
                null);
        List<Survey> surveys = serviceBase.validateResponse(response, new TypeReference<List<Survey>>(){});
        Assert.assertNotNull(surveys, "Unexpected null day for case: " + msg);
        return surveys;
    }

    public List<Survey> getBySchoolAndDate(
            long schoolId, LocalDate start, LocalDate end, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getSurveyEndpointWithSchoolId(schoolId),
                null);
        List<Survey> surveys = serviceBase.validateResponse(response, new TypeReference<List<Survey>>(){});
        Assert.assertNotNull(surveys, "Unexpected null day for case: " + msg);
        return surveys;
    }

    public void getNegative(long sid, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getSurveyEndpoint(sid),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected status code returned while retrieving survey: " + msg);
    }

    public void delete(long sid, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.DELETE,
                serviceBase.getSurveyEndpoint(sid));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(sid, HttpStatus.NOT_FOUND, msg);
    }

    public void deleteNegative(long sid, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE,
                serviceBase.getSurveyEndpoint(sid));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }

    public Survey retrieveAndValidateCreatedSurvey(Survey submitted, EntityId id, String msg) {
        submitted.setId(id.getId());
        Survey created = this.get(id.getId(), msg);
        Assert.assertEquals(created, submitted, msg + " - these should be equal but they are not:\nCREATED:\n"
                + created + "\n" + "SUBMITTED:\n" + submitted);
        return created;
    }
}
