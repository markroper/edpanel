package com.scholarscore.models.survey;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

/**
 * Expresses aggregate counts of all responses to a single survey.
 *
 * Created by markroper on 12/21/15.
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyAggregate {
    protected Long surveyId;
    protected Integer respondents;
    protected List<SurveyQuestionAggregate> questions;

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Integer getRespondents() {
        return respondents;
    }

    public void setRespondents(Integer respondents) {
        this.respondents = respondents;
    }

    public List<SurveyQuestionAggregate> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SurveyQuestionAggregate> questions) {
        this.questions = questions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(respondents, questions, surveyId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SurveyAggregate other = (SurveyAggregate) obj;
        return Objects.equals(this.respondents, other.surveyId)
                && Objects.equals(this.questions, other.questions);
    }
}
