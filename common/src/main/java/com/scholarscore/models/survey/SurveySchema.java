package com.scholarscore.models.survey;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Created by markroper on 12/16/15.
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveySchema  implements Serializable {
    @NotNull
    @Size(min = 1)
    protected List<SurveyQuestion> questions;

    public List<SurveyQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SurveyQuestion> questions) {
        this.questions = questions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(questions);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SurveySchema other = (SurveySchema) obj;
        return Objects.equals(this.questions, other.questions);
    }
}
