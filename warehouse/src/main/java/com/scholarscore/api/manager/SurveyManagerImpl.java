package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.SurveyPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.survey.Survey;
import com.scholarscore.models.survey.SurveyResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by markroper on 12/16/15.
 */
public class SurveyManagerImpl implements SurveyManager {
    private static final String SURVEY = "survey";
    private static final String SURVEY_RESP = "survey response";

    @Autowired
    private OrchestrationManager pm;

    @Autowired
    private SurveyPersistence surveyPersistence;

    public void setPm(OrchestrationManager pm) {
        this.pm = pm;
    }

    public void setSurveyPersistence(SurveyPersistence surveyPersistence) {
        this.surveyPersistence = surveyPersistence;
    }

    @Override
    public StatusCode surveyExists(long surveyId) {
        Survey s = surveyPersistence.selectSurvey(surveyId);
        if(null == s) {
            return StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{SURVEY, surveyId});
        };
        return StatusCodes.getStatusCode(StatusCodeType.OK);
    }
    @Override
    public ServiceResponse<EntityId> createSurvey(Survey survey) {
        return new ServiceResponse<>(new EntityId(surveyPersistence.insertSurvey(survey)));
    }

    @Override
    public ServiceResponse<Void> deleteSurvey(long surveyId) {
        surveyPersistence.deleteSurvey(surveyId);
        return new ServiceResponse<>((Void)null);
    }

    @Override
    public ServiceResponse<Survey> getSurvey(long surveyId) {
        Survey s = surveyPersistence.selectSurvey(surveyId);
        if(null == s) {
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{SURVEY, surveyId}));
        };
        return new ServiceResponse<>(s);
    }

    @Override
    public ServiceResponse<List<Survey>> getSurveysByUserId(long userId) {
        StatusCode code = pm.getUserManager().userExists(userId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<List<Survey>>(surveyPersistence.selectSurveyByUserId(userId));
    }

    @Override
    public ServiceResponse<List<Survey>> getSurveysBySchoolId(long schoolId, LocalDate startDate, LocalDate endDate) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        if(null != startDate && null != endDate && startDate.isAfter(endDate)) {
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.ENTITY_INVALID_IN_CONTEXT));
        }
        return new ServiceResponse<List<Survey>>(surveyPersistence.selectSurveyBySchoolId(schoolId, startDate, endDate));
    }

    @Override
    public ServiceResponse<EntityId> createSurveyResponse(SurveyResponse response) {
        long surveyId = response.getSurvey().getId();
        Survey s = surveyPersistence.selectSurvey(surveyId);
        if(null == s) {
            return new ServiceResponse<>(
                    StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{SURVEY, surveyId}));
        }
        response.setSurvey(s);
        if(!response.isvalid()) {
            StatusCode code = StatusCodes.getStatusCode(
                    StatusCodeType.ENTITY_INVALID_IN_CONTEXT,
                    new Object[]{ SURVEY_RESP, "null", SURVEY, surveyId });
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<>(new EntityId(surveyPersistence.insertSurveyResponse(response)));
    }

    @Override
    public ServiceResponse<Void> deleteSurveyResponse(long surveyId, long surveyResponseId) {
        surveyPersistence.deleteSurveyResponse(surveyId, surveyResponseId);
        return new ServiceResponse<Void>((Void) null);
    }

    @Override
    public ServiceResponse<Void> updateSurveyResponse(long surveyId, long surveyResponseId, SurveyResponse response) {
        Survey s = surveyPersistence.selectSurvey(surveyId);
        if(null == s) {
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{SURVEY, surveyId}));
        }
        response.setSurvey(s);
        if(!response.isvalid()) {
            StatusCode code = StatusCodes.getStatusCode(
                    StatusCodeType.ENTITY_INVALID_IN_CONTEXT,
                    new Object[]{ SURVEY_RESP, surveyResponseId, SURVEY, surveyId });
            return new ServiceResponse<>(code);
        }
        surveyPersistence.updateSurveyResponse(surveyId, surveyResponseId, response);
        return new ServiceResponse<>((Void) null);
    }

    @Override
    public ServiceResponse<SurveyResponse> getSurveyResponse(long surveyId, long responseId) {
        StatusCode code = surveyExists(surveyId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        SurveyResponse s = surveyPersistence.selectSurveyResponse(surveyId, responseId);
        if(null == s) {
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{SURVEY_RESP, surveyId}));
        };
        return new ServiceResponse<>(s);
    }

    @Override
    public ServiceResponse<List<SurveyResponse>> getSurveyResponses(long surveyId) {
        StatusCode code = surveyExists(surveyId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<List<SurveyResponse>>(
                surveyPersistence.selectSurveyResponses(surveyId));
    }

    @Override
    public ServiceResponse<List<SurveyResponse>> getSurveyResponsesByRespondentId(
            long respondentId, LocalDate startDate, LocalDate endDate) {
        if(null != startDate && null != endDate && startDate.isAfter(endDate)) {
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.ENTITY_INVALID_IN_CONTEXT));
        }
        StatusCode code = pm.getUserManager().userExists(respondentId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        return new ServiceResponse<List<SurveyResponse>>(
                surveyPersistence.selectSurveyResponsesByRespondent(respondentId, startDate, endDate));
    }
}
