package com.scholarscore.api.manager;

import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.api.util.StatusCode;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.survey.Survey;
import com.scholarscore.models.survey.SurveyResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * The manager interface defining survey behaviors supported by EdPanel.  Includes
 * actions on surveys and survey responses.
 *
 * Created by markroper on 12/16/15.
 */
public interface SurveyManager {
    StatusCode surveyExists(long surveyId);
    /**
     * Creates a survey and returns its system generated ID.
     * @param survey
     * @return
     */
    ServiceResponse<EntityId> createSurvey(Survey survey);
    /**
     * Deletes a survey by its ID
     * @param surveyId
     * @return
     */
    ServiceResponse<Void> deleteSurvey(long surveyId);
    /**
     * Get a survey by its ID
     * @param surveyId
     * @return
     */
    ServiceResponse<Survey> getSurvey(long surveyId);
    /**
     * Get and return the list of surveys created by a user.
     * @param surveyId
     * @return
     */
    ServiceResponse<List<Survey>> getSurveysByUserId(long surveyId);
    /**
     * Get and return a list of surveys within a school.  If startDate and endDate are provided,
     * limit the returned set to those surveys created during the range provided.  If the date params
     * are not set, include all surveys within the school regardless of time.
     * @param schoolId
     * @param startDate
     * @param endDate
     * @return
     */
    ServiceResponse<List<Survey>> getSurveysBySchoolId(long schoolId, LocalDate startDate, LocalDate endDate);
    /**
     * Create a survey response and return the generated ID
     * @param survey
     * @return
     */
    ServiceResponse<EntityId> createSurveyResponse(SurveyResponse survey);
    /**
     * Delete a survey response and return an empty body on success
     * @param surveyId
     * @param surveyResponseId
     * @return
     */
    ServiceResponse<Void> deleteSurveyResponse(long surveyId, long surveyResponseId);
    /**
     * Update an existing survey response and return an empty body on success
     * @param surveyId
     * @param surveyResponseId
     * @param response
     * @return
     */
    ServiceResponse<Void> updateSurveyResponse(long surveyId, long surveyResponseId, SurveyResponse response);
    /**
     * Get and return a single survey response by survey ID and survey response ID.
     * @param surveyId
     * @param responseId
     * @return
     */
    ServiceResponse<SurveyResponse> getSurveyResponse(long surveyId, long responseId);
    /**
     * Get and return a collection of survey responses associates with a single survey
     * @param surveyId
     * @return
     */
    ServiceResponse<List<SurveyResponse>> getSurveyResponses(long surveyId);
    /**
     * Get and return a collection of survey responses associated with a single respondent over
     * a period of time.  If the start and end date bounds are not supplied, over all time.
     * @param respondentId
     * @param startDate
     * @param endDate
     * @return
     */
    ServiceResponse<List<SurveyResponse>> getSurveyResponsesByRespondentId(long respondentId, LocalDate startDate, LocalDate endDate);
}
