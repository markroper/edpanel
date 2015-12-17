package com.scholarscore.models.survey.answer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.scholarscore.models.survey.question.SurveyQuestion;
import com.scholarscore.models.survey.SurveyQuestionType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by markroper on 12/16/15.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BooleanAnswer.class, name="TRUE_FALSE"),
        @JsonSubTypes.Type(value = MultipleChoiceAnswer.class, name = "MULTIPLE_CHOICE"),
        @JsonSubTypes.Type(value = OpenAnswer.class, name = "OPEN_RESPONSE"),
})
public abstract class QuestionAnswer<T, U extends SurveyQuestion> implements Serializable {
    @NotNull
    protected U question;
    protected T answer;
    protected SurveyQuestionType type;

    @Enumerated(EnumType.STRING)
    public SurveyQuestionType getType() {
        return type;
    }

    public void setType(SurveyQuestionType type) {
        this.type = type;
    }

    public T getAnswer() {
        return answer;
    }

    public void setAnswer(T answer) {
        this.answer = answer;
    }

    public U getQuestion() {
        return question;
    }

    public void setQuestion(U question) {
        this.question = question;
    }

    @Override
    public int hashCode() {
        return Objects.hash(answer, question, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final QuestionAnswer other = (QuestionAnswer) obj;
        return Objects.equals(this.answer, other.answer)
                && Objects.equals(this.question, other.question)
                && Objects.equals(this.type, other.type);
    }
}
