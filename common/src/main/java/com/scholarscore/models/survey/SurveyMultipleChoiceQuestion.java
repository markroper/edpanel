package com.scholarscore.models.survey;

import com.fasterxml.jackson.annotation.JsonInclude;

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
    public SurveyQuestionTypes getType() {
        return SurveyQuestionTypes.MULTIPLE_CHOICE;
    }

    @Override
    public Class<Integer> getResponseClass() {
        return Integer.class;
    }

    @Override
    public int hashCode() {
        return Objects.hash(choices);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SurveyMultipleChoiceQuestion other = (SurveyMultipleChoiceQuestion) obj;
        return Objects.equals(this.choices, other.choices);
    }
}
