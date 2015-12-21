package com.scholarscore.api.manager;

import com.scholarscore.api.persistence.SurveyPersistence;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.api.util.StatusCodeType;
import com.scholarscore.api.util.StatusCodes;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.survey.Survey;
import com.scholarscore.models.survey.SurveyAggregate;
import com.scholarscore.models.survey.SurveyQuestionAggregate;
import com.scholarscore.models.survey.SurveyQuestionType;
import com.scholarscore.models.survey.SurveyResponse;
import com.scholarscore.models.survey.answer.QuestionAnswer;
import com.scholarscore.models.survey.question.SurveyMultipleChoiceQuestion;
import com.scholarscore.models.survey.question.SurveyQuestion;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        StatusCode code;
        if(null != survey.getSchoolFk()) {
            code = pm.getSchoolManager().schoolExists(survey.getSchoolFk());
            if(!code.isOK()) {
                return new ServiceResponse<>(code);
            }
        }
        if(null != survey.getSectionFk()) {
            code = pm.getSectionManager().sectionExists(0L, 0L, 0L, survey.getSectionFk());
            if(!code.isOK()) {
                return new ServiceResponse<>(code);
            }
        }
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
        return new ServiceResponse<>(surveyPersistence.selectSurveyByCreatingUserId(userId));
    }

    @Override
    public ServiceResponse<List<Survey>> getSurveysByDistrict(LocalDate startDate, LocalDate endDate) {
        if(null != startDate && null != endDate && startDate.isAfter(endDate)) {
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.ENTITY_INVALID_IN_CONTEXT));
        }
        return new ServiceResponse<>(surveyPersistence.selectDistrictSurveys(startDate, endDate));
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
        return new ServiceResponse<>(surveyPersistence.selectSurveyBySchoolId(schoolId, startDate, endDate));
    }

    @Override
    public ServiceResponse<List<Survey>> getSurveysBySectionId(
            long schoolId, long sectionId, LocalDate startDate, LocalDate endDate) {
        StatusCode code = pm.getSchoolManager().schoolExists(schoolId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        code = pm.getSectionManager().sectionExists(schoolId, 0L, 0L, sectionId);
        if(!code.isOK()) {
            return new ServiceResponse<>(code);
        }
        if(null != startDate && null != endDate && startDate.isAfter(endDate)) {
            return new ServiceResponse<>(StatusCodes.getStatusCode(StatusCodeType.ENTITY_INVALID_IN_CONTEXT));
        }
        return new ServiceResponse<>(surveyPersistence.selectSurveyBySectionId(schoolId, startDate, endDate));
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
    public ServiceResponse<SurveyAggregate> getSurveyAggregateResults(long surveyId) {
        //Fetch the survey & responses from the DB
        ServiceResponse<List<SurveyResponse>> responsesResp = getSurveyResponses(surveyId);
        Survey s = surveyPersistence.selectSurvey(surveyId);
        if(null == s) {
            return new ServiceResponse<>(
                    StatusCodes.getStatusCode(StatusCodeType.MODEL_NOT_FOUND, new Object[]{SURVEY, surveyId}));
        }
        if(null == responsesResp.getValue()) {
            return new ServiceResponse<SurveyAggregate>(responsesResp.getCode());
        }
        SurveyAggregate aggregate = new SurveyAggregate();
        aggregate.setSurveyId(surveyId);
        aggregate.setRespondents(responsesResp.getValue().size());
        aggregate.setQuestions(new ArrayList<>());
        //Set up question and response counts collections
        if(null != s.getQuestions()) {
            for (SurveyQuestion question: s.getQuestions()) {
                List<Integer> counter = new ArrayList<>();
                if(SurveyQuestionType.MULTIPLE_CHOICE.equals(question.getType())) {
                    Integer size = ((SurveyMultipleChoiceQuestion) question).getChoices().size();
                    for(int i = 0; i < size; i++) {
                        counter.add(0);
                    }
                } else if(SurveyQuestionType.TRUE_FALSE.equals(question.getType())) {
                    counter.add(0);
                    counter.add(0);
                }
                aggregate.getQuestions().add(new SurveyQuestionAggregate(0, question, counter));
            }
        }
        //Generate a map for O(1) lookup
        Map<SurveyQuestion, SurveyQuestionAggregate> qMap = new HashMap<>();
        for(SurveyQuestionAggregate agg: aggregate.getQuestions()) {
            qMap.put(agg.getQuestion(), agg);
        }
        //For each survey response, for each question, increment counts on the SurveyAggregate instance
        //WARNING: runtime scales linearly with number of responses
        for(SurveyResponse resp : responsesResp.getValue()) {
            for(QuestionAnswer qa : resp.getAnswers()){
                if(qMap.containsKey(qa.getQuestion())) {
                    SurveyQuestionAggregate agg = qMap.get(qa.getQuestion());
                    agg.setRespondents(agg.getRespondents() + 1);
                    if(SurveyQuestionType.MULTIPLE_CHOICE.equals(qa.getQuestion().getType())) {
                        Integer answerIndex = (Integer)qa.getAnswer();
                        Integer previousValue = agg.getResults().get(answerIndex);
                        if(null == previousValue) {
                            previousValue = 0;
                        }
                        agg.getResults().set(answerIndex, previousValue + 1);
                    } else if(SurveyQuestionType.TRUE_FALSE.equals(qa.getQuestion().getType())) {
                        if(null != (Boolean)qa.getAnswer()) {
                            if((Boolean)qa.getAnswer()) {
                                agg.getResults().set(0, agg.getResults().get(0) + 1);
                            } else {
                                agg.getResults().set(1, agg.getResults().get(1) + 1);
                            }

                        }
                    }
                }
            }
        }
        return new ServiceResponse<>(aggregate);
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
