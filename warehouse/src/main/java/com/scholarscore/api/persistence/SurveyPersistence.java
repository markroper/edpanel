package com.scholarscore.api.persistence;

import com.scholarscore.models.survey.Survey;
import com.scholarscore.models.survey.SurveyResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by markroper on 12/16/15.
 */
public interface SurveyPersistence {
    Long insertSurvey(Survey survey);
    Survey selectSurvey(long surveyId);
    void deleteSurvey(long surveyId);
    List<Survey> selectSurveyByUserId(long userId);
    List<Survey> selectSurveyBySchoolId(long schoolId, LocalDate start, LocalDate end);

    long insertSurveyResponse(SurveyResponse resp);
    void deleteSurveyResponse(long surveyId, long respId);
    void updateSurveyResponse(long surveyId, long respId, SurveyResponse response);
    SurveyResponse selectSurveyResponse(long surveyId, long respId);
    List<SurveyResponse> selectSurveyResponses(long surveyId);
    List<SurveyResponse> selectSurveyResponsesByRespondent(long respondentId, LocalDate start, LocalDate end);
}
