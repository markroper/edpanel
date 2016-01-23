package com.scholarscore.models.survey.question;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.survey.SurveyQuestionType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;
import java.util.Objects;

/**
 * Created by markroper on 12/16/15.
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyMultipleChoiceQuestion extends SurveyQuestion<Integer> {
    protected List<String> choices;

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    @Override
    @Enumerated(EnumType.STRING)
    public SurveyQuestionType getType() {
        return SurveyQuestionType.MULTIPLE_CHOICE;
    }

    @Override
    @JsonIgnore
    public Class<Integer> getResponseClass() {
        return Integer.class;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(choices);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final SurveyMultipleChoiceQuestion other = (SurveyMultipleChoiceQuestion) obj;
        return Objects.equals(this.choices, other.choices);
    }
}
