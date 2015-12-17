package com.scholarscore.api.controller.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.survey.SurveyResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.Assert;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by markroper on 12/16/15.
 */
public class SurveyResponseValidatingExecutor {
    private final IntegrationBase serviceBase;

    public SurveyResponseValidatingExecutor(IntegrationBase sb) {
        this.serviceBase = sb;
    }

    public SurveyResponse create(SurveyResponse s, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.POST,
                serviceBase.getSurveyResponseEndpoint(s.getSurvey().getId()),
                null,
                s);
        EntityId surveyId = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(surveyId, "unexpected null day ID returned from create call for case: " + msg);
        return retrieveAndValidateCreatedSurveyResponse(s, surveyId, msg);
    }

    public void createNegative(SurveyResponse s, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.POST,
                serviceBase.getSurveyResponseEndpoint(s.getSurvey().getId()),
                null,
                s);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected status code returned for case: " + msg);
    }

    public void update(SurveyResponse s, String msg) {
        //Create the term
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT,
                serviceBase.getSurveyResponseEndpoint(s.getSurvey().getId(), s.getId()),
                null,
                s);
        EntityId sid = serviceBase.validateResponse(response, new TypeReference<EntityId>(){});
        Assert.assertNotNull(sid, "unexpected null app returned from create call for case: " + msg);
        retrieveAndValidateCreatedSurveyResponse(s, sid, msg);
    }

    public void updateNegative(SurveyResponse s, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(HttpMethod.PUT,
                serviceBase.getSurveyResponseEndpoint(s.getSurvey().getId(), s.getId()),
                null,
                s);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected status code returned for case: " + msg);
    }

    public SurveyResponse get(long sid, long respId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getSurveyResponseEndpoint(sid, respId),
                null);
        SurveyResponse survey = serviceBase.validateResponse(response, new TypeReference<SurveyResponse>(){});
        Assert.assertNotNull(survey, "Unexpected null day for case: " + msg);
        return survey;
    }

    public List<SurveyResponse> getBySurveyId(long surveyId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getSurveyResponseEndpoint(surveyId),
                null);
        List<SurveyResponse> survey = serviceBase.validateResponse(response, new TypeReference<List<SurveyResponse>>(){});
        Assert.assertNotNull(survey, "Unexpected null day for case: " + msg);
        return survey;
    }

    public List<SurveyResponse> getByRespondentAndDate(long respondentId, LocalDate start, LocalDate end, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getSurveyResponseByRespondentEndpoint(respondentId),
                null);
        List<SurveyResponse> survey = serviceBase.validateResponse(response, new TypeReference<List<SurveyResponse>>(){});
        Assert.assertNotNull(survey, "Unexpected null day for case: " + msg);
        return survey;
    }

    public void getNegative(long sid, long respId, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.GET,
                serviceBase.getSurveyResponseEndpoint(sid, respId),
                null);
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected status code returned while retrieving survey resp: " + msg);
    }

    public void delete(long sid, long respId, String msg) {
        ResultActions response = serviceBase.makeRequest(
                HttpMethod.DELETE,
                serviceBase.getSurveyResponseEndpoint(sid, respId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), HttpStatus.OK.value(),
                "Non 200 HttpStatus returned on delete for case: " + msg);
        getNegative(sid, respId, HttpStatus.NOT_FOUND, msg);
    }

    public void deleteNegative(long sid, long respId, HttpStatus expectedCode, String msg) {
        ResultActions response = serviceBase.makeRequest(HttpMethod.DELETE,
                serviceBase.getSurveyResponseEndpoint(sid, respId));
        Assert.assertEquals(response.andReturn().getResponse().getStatus(), expectedCode.value(),
                "Unexpected Http status code returned for negative test case for DELETE: " + msg);
    }

    public SurveyResponse retrieveAndValidateCreatedSurveyResponse(SurveyResponse submitted, EntityId id, String msg) {
        submitted.setId(id.getId());
        SurveyResponse created = this.get(submitted.getSurvey().getId(), id.getId(), msg);
        Assert.assertEquals(created, submitted, msg + " - these should be equal but they are not:\nCREATED:\n"
                + created + "\n" + "SUBMITTED:\n" + submitted);
        return created;
    }
}
