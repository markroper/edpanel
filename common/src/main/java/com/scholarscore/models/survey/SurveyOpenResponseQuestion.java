package com.scholarscore.models.survey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Objects;

/**
 * Created by markroper on 12/16/15.
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyOpenResponseQuestion extends SurveyQuestion<String> {
    protected Integer maxResponseLength;

    public Integer getMaxResponseLength() {
        return maxResponseLength;
    }

    public void setMaxResponseLength(Integer maxResponseLength) {
        this.maxResponseLength = maxResponseLength;
    }

    @Override
    @Enumerated(EnumType.STRING)
    public SurveyQuestionType getType() {
        return SurveyQuestionType.OPEN_RESPONSE;
    }

    @Override
    @JsonIgnore
    public Class<String> getResponseClass() {
        return String.class;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxResponseLength);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SurveyOpenResponseQuestion other = (SurveyOpenResponseQuestion) obj;
        return Objects.equals(this.maxResponseLength, other.maxResponseLength);
    }
}
