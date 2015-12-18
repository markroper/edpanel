package com.scholarscore.api.controller;

import com.scholarscore.api.ApiConsts;
import com.scholarscore.api.util.ServiceResponse;
import com.scholarscore.models.EntityId;
import com.scholarscore.models.Section;
import com.scholarscore.models.survey.Survey;
import com.scholarscore.models.survey.SurveyResponse;
import com.scholarscore.util.EdPanelDateUtil;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Supports the API operations on surveys and survey resposnes.  Because responses are tightly coupled with
 * survey schemas, surveys can be created and deleted, but not modified.  This is to prevent responses from being out
 * of sync with surveys.  Consumers of the API will have to delete and recreate a survey in order to offer an
 * 'edit' experience to end users and any survey responses already collected will be lost in the process. This is
 * to simplify the edge cases and complexity of the initial implementation and can be changed later if there is desire
 * for that to happen.
 *
 * SurveyResponses, in contrast, are update-able.
 *
 * Created by markroper on 12/16/15.
 */
@Controller
@RequestMapping(ApiConsts.API_V1_ENDPOINT + "/surveys")
public class SurveyController extends BaseController {
    @ApiOperation(
            value = "Create a survey",
            notes = "Creates, assigns an ID, persists and returns a survey",
            response = EntityId.class)
    @RequestMapping(
            method = RequestMethod.POST,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody
    ResponseEntity createSurvey(@RequestBody @Valid Survey survey) {
        return respond(pm.getSurveyManager().createSurvey(survey));
    }

    @ApiOperation(
            value = "Delete a survey",
            response = Void.class)
    @RequestMapping(
            value = "/{surveyId}",
            method = RequestMethod.DELETE,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity deleteSurvey(
            @ApiParam(name = "surveyId", required = true, value = "Survey ID")
            @PathVariable(value="surveyId") Long surveyId) {
        return respond(pm.getSurveyManager().deleteSurvey(surveyId));
    }

    @ApiOperation(
            value = "Get a survey by ID",
            notes = "Retrieve a survey by its ID",
            response = Survey.class)
    @RequestMapping(
            value = "/{surveyId}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getSurvey(
            @ApiParam(name = "surveyId", required = true, value = "Survey ID")
            @PathVariable(value="surveyId") Long surveyId) {
        return respond(pm.getSurveyManager().getSurvey(surveyId));
    }

    @ApiOperation(
            value = "Get surveys by creating user ID",
            notes = "Retrieve surveys by the creating user ID",
            response = List.class)
    @RequestMapping(
            value = "/users/{userId}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getSurveysByCreatingUserId(
            @ApiParam(name = "userId", required = true, value = "User ID")
            @PathVariable(value="userId") Long userId) {
        return respond(pm.getSurveyManager().getSurveysByUserId(userId));
    }

    @ApiOperation(
            value = "Get surveys that the student with  respondent ID can respond to",
            notes = "Retrieve surveys by the respondent, school, & section",
            response = List.class)
    @RequestMapping(
            value = "/schools/{schoolId}/years/{yearId}/terms/{termId}/respondents/{respondentId}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getSurveysForRespondentId(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "yearId", required = true, value = "Year ID")
            @PathVariable(value="yearId") Long yearId,
            @ApiParam(name = "termId", required = true, value = "Term ID")
            @PathVariable(value="termId") Long termId,
            @ApiParam(name = "respondentId", required = true, value = "Respondent ID")
            @PathVariable(value="respondentId") Long respondentId) {
        List<Survey> surveys = new ArrayList<>();
        //Resolve all the surveys in the school the student is enrolled in
        ServiceResponse<List<Survey>> schoolSurveysResp =
                pm.getSurveyManager().getSurveysBySchoolId(schoolId, LocalDate.now().minusYears(1), null);
        if(null != schoolSurveysResp.getValue()) {
            surveys.addAll(schoolSurveysResp.getValue());
        }
        //Resolve all the surveys in the sections the student is enrolled in
        ServiceResponse<Collection<Section>> sectionsResp =
                pm.getSectionManager().getAllSections(respondentId, schoolId, yearId, termId);
        if(null != sectionsResp.getValue()){
            for(Section s: sectionsResp.getValue()) {
                ServiceResponse<List<Survey>> sectSurveyResp =
                        pm.getSurveyManager().getSurveysBySectionId(schoolId, s.getId(), LocalDate.now().minusYears(1), null);
                if(null != sectSurveyResp.getValue()) {
                    surveys.addAll(sectSurveyResp.getValue());
                }
            }
        }
        return respond(surveys);
    }

    @ApiOperation(
            value = "Get surveys by school ID with optional date ranges",
            notes = "Retrieve surveys by the parent school ID",
            response = List.class)
    @RequestMapping(
            value = "/schools/{schoolId}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getSurveysBySchoolId(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @RequestParam(value="startDate", required = false) @DateTimeFormat(pattern = EdPanelDateUtil.EDPANEL_DATE_FORMAT) LocalDate startDate,
            @RequestParam(value="endDate", required = false) @DateTimeFormat(pattern = EdPanelDateUtil.EDPANEL_DATE_FORMAT) LocalDate endDate) {
        return respond(pm.getSurveyManager().getSurveysBySchoolId(schoolId, startDate, endDate));
    }

    @ApiOperation(
            value = "Get surveys by section ID with optional date ranges",
            notes = "Retrieve surveys by the parent school ID",
            response = List.class)
    @RequestMapping(
            value = "/schools/{schoolId}/sections/{sectionId}",
            method = RequestMethod.GET,
            produces = { JSON_ACCEPT_HEADER })
    @SuppressWarnings("rawtypes")
    public @ResponseBody ResponseEntity getSurveysBySectionlId(
            @ApiParam(name = "schoolId", required = true, value = "School ID")
            @PathVariable(value="schoolId") Long schoolId,
            @ApiParam(name = "sectionId", required = true, value = "Section ID")
            @PathVariable(value="sectionId") Long sectionId,
            @RequestParam(value="startDate", required = false) @DateTimeFormat(pattern = EdPanelDateUtil.EDPANEL_DATE_FORMAT) LocalDate startDate,
            @RequestParam(value="endDate", required = false) @DateTimeFormat(pattern = EdPanelDateUtil.EDPANEL_DATE_FORMAT) LocalDate endDate) {
        return respond(pm.getSurveyManager().getSurveysBySectionId(schoolId, sectionId, startDate, endDate));
    }

    @ApiOperation(
            value = "Create a survey response",
            notes = "Creates, assigns an ID, persists and returns a survey resonse ID",
            response = EntityId.class)
    @RequestMapping(
            value = "/{surveyId}/responses",
            method = RequestMethod.POST,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody
    ResponseEntity createSurvey(@RequestBody @Valid SurveyResponse response) {
        return respond(pm.getSurveyManager().createSurveyResponse(response));
    }

    @ApiOperation(
            value = "Delete a survey response by ID",
            notes = "Deletes a single response to a survey",
            response = Void.class)
    @RequestMapping(
            value = "/{surveyId}/responses/{responseId}",
            method = RequestMethod.DELETE,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody
    ResponseEntity deleteSurveyResponse(@ApiParam(name = "surveyId", required = true, value = "Survey ID")
                                        @PathVariable(value="surveyId") Long surveyId,
                                        @ApiParam(name = "responseId", required = true, value = "Response ID")
                                        @PathVariable(value="responseId") Long responseId) {
        return respond(pm.getSurveyManager().deleteSurveyResponse(surveyId, responseId));
    }

    @ApiOperation(
            value = "Update a survey response by ID",
            notes = "Updates a single response to a survey",
            response = Void.class)
    @RequestMapping(
            value = "/{surveyId}/responses/{responseId}",
            method = RequestMethod.PUT,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody
    ResponseEntity deleteSurveyResponse(@ApiParam(name = "surveyId", required = true, value = "Survey ID")
                                        @PathVariable(value="surveyId") Long surveyId,
                                        @ApiParam(name = "responseId", required = true, value = "Response ID")
                                        @PathVariable(value="responseId") Long responseId,
                                        @RequestBody @Valid SurveyResponse response) {
        return respond(pm.getSurveyManager().updateSurveyResponse(surveyId, responseId, response));
    }

    @ApiOperation(
            value = "Get all responses to a survey",
            notes = "Returns all response to a survey",
            response = List.class)
    @RequestMapping(
            value = "/{surveyId}/responses",
            method = RequestMethod.GET,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody
    ResponseEntity getAllSurveyResponses(@ApiParam(name = "surveyId", required = true, value = "Survey ID")
                                         @PathVariable(value="surveyId") Long surveyId) {
        return respond(pm.getSurveyManager().getSurveyResponses(surveyId));
    }

    @ApiOperation(
            value = "Get all survey responses for a respondent",
            notes = "Returns all responses to surveys for a respondant with optional date range params",
            response = List.class)
    @RequestMapping(
            value = "/respondents/{respondentId}/responses",
            method = RequestMethod.GET,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody
    ResponseEntity getAllSurveyResponses(@ApiParam(name = "respondentId", required = true, value = "Respondent ID")
                                         @PathVariable(value="respondentId") Long respondentId,
                                         @RequestParam(value="startDate", required = false) @DateTimeFormat(pattern = EdPanelDateUtil.EDPANEL_DATE_FORMAT) LocalDate startDate,
                                         @RequestParam(value="endDate", required = false) @DateTimeFormat(pattern = EdPanelDateUtil.EDPANEL_DATE_FORMAT) LocalDate endDate) {
        return respond(pm.getSurveyManager().getSurveyResponsesByRespondentId(respondentId, startDate, endDate));
    }

    @ApiOperation(
            value = "Get a survey response by ID",
            notes = "Returns a single response to a survey",
            response = SurveyResponse.class)
    @RequestMapping(
            value = "/{surveyId}/responses/{responseId}",
            method = RequestMethod.GET,
            produces = {JSON_ACCEPT_HEADER})
    @SuppressWarnings("rawtypes")
    public @ResponseBody
    ResponseEntity getSurveyResponse(@ApiParam(name = "surveyId", required = true, value = "Survey ID")
                                    @PathVariable(value="surveyId") Long surveyId,
                                    @ApiParam(name = "responseId", required = true, value = "Response ID")
                                    @PathVariable(value="responseId") Long responseId) {
        return respond(pm.getSurveyManager().getSurveyResponse(surveyId, responseId));
    }
}
