package com.scholarscore.models.survey.question;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.scholarscore.models.survey.SurveyQuestionType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Objects;

/**
 * Created by markroper on 12/16/15.
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyBooleanQuestion extends SurveyQuestion<Boolean> {
    //Shown as a radio button by default, if true, diplay as a checkbox
    protected Boolean showAsCheckbox;

    public Boolean getShowAsCheckbox() {
        return showAsCheckbox;
    }

    public void setShowAsCheckbox(Boolean showAsCheckbox) {
        this.showAsCheckbox = showAsCheckbox;
    }

    @Override
    @Enumerated(EnumType.STRING)
    public SurveyQuestionType getType() {
        return SurveyQuestionType.TRUE_FALSE;
    }

    @Override
    @JsonIgnore
    public Class<Boolean> getResponseClass() {
        return Boolean.class;
    }

    @Override
    public int hashCode() {
        return Objects.hash(showAsCheckbox);
    }

    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SurveyBooleanQuestion other = (SurveyBooleanQuestion) obj;
        return Objects.equals(this.showAsCheckbox, other.showAsCheckbox);
    }
}
