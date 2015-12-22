package com.scholarscore.models.survey.question;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.scholarscore.models.survey.SurveyQuestionType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by markroper on 12/16/15.
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SurveyBooleanQuestion.class, name="TRUE_FALSE"),
        @JsonSubTypes.Type(value = SurveyMultipleChoiceQuestion.class, name = "MULTIPLE_CHOICE"),
        @JsonSubTypes.Type(value = SurveyOpenResponseQuestion.class, name = "OPEN_RESPONSE"),
})
public abstract class SurveyQuestion<T> implements Serializable {
    protected String question;
    protected Boolean responseRequired;

    @Enumerated(EnumType.STRING)
    public abstract SurveyQuestionType getType();

    public void setType(SurveyQuestionType type) {
    }

    public abstract Class<T> getResponseClass();

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Boolean getResponseRequired() {
        return responseRequired;
    }

    public void setResponseRequired(Boolean responseRequired) {
        this.responseRequired = responseRequired;
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, responseRequired);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SurveyQuestion other = (SurveyQuestion) obj;
        return Objects.equals(this.question, other.question)
                && Objects.equals(this.responseRequired, other.responseRequired);
    }
}
