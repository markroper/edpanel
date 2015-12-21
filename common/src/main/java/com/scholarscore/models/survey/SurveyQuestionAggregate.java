package com.scholarscore.models.survey;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.survey.question.SurveyQuestion;

import java.util.List;
import java.util.Objects;

/**
 * Expresses aggregate results for all responses to a single question on a survey.
 *
 * Created by markroper on 12/21/15.
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyQuestionAggregate {
    private Integer respondents;
    private SurveyQuestion question;
    //For MULIPLE_CHOICE length and order of list matches the surveyQuestion choices.  Int value equals number
    //of responses matching that choice.  For TRUE_FALSE, length is to with the first position being the count of TRUE
    //responses and the second position being the count of FALSE responses.  Open responses have an empty results
    //array since the response type is not summable.
    private List<Integer> results;

    public SurveyQuestionAggregate() {

    }

    public SurveyQuestionAggregate(Integer respondents, SurveyQuestion question, List<Integer> results) {
        this.respondents = respondents;
        this.question = question;
        this.results = results;
    }
    public SurveyQuestion getQuestion() {
        return question;
    }

    public void setQuestion(SurveyQuestion question) {
        this.question = question;
    }

    public List<Integer> getResults() {
        return results;
    }

    public void setResults(List<Integer> results) {
        this.results = results;
    }

    public Integer getRespondents() {
        return respondents;
    }

    public void setRespondents(Integer respondents) {
        this.respondents = respondents;
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, results, respondents);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SurveyQuestionAggregate other = (SurveyQuestionAggregate) obj;
        return Objects.equals(this.question, other.question)
                && Objects.equals(this.respondents, other.respondents)
                && Objects.equals(this.results, other.results);
    }
}
