package com.scholarscore.models.survey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.models.ApiModel;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.survey.answer.QuestionAnswer;
import com.scholarscore.models.survey.question.SurveyQuestion;
import com.scholarscore.models.user.Student;
import com.scholarscore.util.EdPanelObjectMapper;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Expresses a single user's response to a single survey.
 *
 * Created by markroper on 12/16/15.
 */
@Entity(name = HibernateConsts.SURVEY_RESPONSES_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyResponse extends ApiModel {
    protected Student respondent;
    protected LocalDate responseDate;
    protected Survey survey;
    // hibernate uses different getter to access the string value and store in a blob
    @NotNull
    @Size(min = 1)
    protected List<QuestionAnswer> answers;

    /**
     * Given a survey response with a fully populated Survey object set upon it, returns true if the response
     * answers are compatible with the survey schema. This means that all required survey questions have answers
     * and that there are no answers to questions not on the survery.  Returns false in other cases, meaning the
     * survey response is invalid and should not be stored.
     * 
     * @return
     */
    @JsonIgnore
    @Transient
    public boolean isvalid() {
        //If the survey is null, or the answers are null, return false, the response is not valid
        if(null == survey || null == survey.getQuestions() || null == answers) {
            return false;
        }
        //Make a set of all Survey questions for 0(1) lookup
        Set<SurveyQuestion> questionsSet = new HashSet<>();
        for(SurveyQuestion q : survey.getQuestions()) {
            questionsSet.add(q);
        }
        //For every question on the survey for which we have an answer, remove the question from the
        //question set.  If there are answers to questions that are not on the survey, return false, the
        //response is not valid.
        for(QuestionAnswer qa : answers) {
            if(questionsSet.contains(qa.getQuestion())) {
                questionsSet.remove(qa.getQuestion());
            } else {
                return false;
            }
        }
        //If the survey has a required unanswered question, return false, the response is not valid
        for(SurveyQuestion q : questionsSet) {
            if(null != q.getResponseRequired() && q.getResponseRequired().equals(true)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void mergePropertiesIfNull(ApiModel model) {
        super.mergePropertiesIfNull(model);
        if(null != model && model instanceof SurveyResponse) {
            SurveyResponse input = (SurveyResponse) model;
            if(null == this.respondent) {
                this.respondent = input.respondent;
            }
            if(null == this.responseDate) {
                this.responseDate = input.responseDate;
            }
            if(null == this.answers) {
                this.answers = input.answers;
            }
            if(null == this.survey) {
                this.survey = input.survey;
            }
        }
    }
    @Id
    @Column(name = HibernateConsts.SURVEY_RESPONSE_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    @ManyToOne(optional = true, fetch= FetchType.EAGER)
    @JoinColumn(name = HibernateConsts.STUDENT_FK)
    @Fetch(FetchMode.JOIN)
    public Student getRespondent() {
        return respondent;
    }

    public void setRespondent(Student respondent) {
        this.respondent = respondent;
    }

    @ManyToOne
    @JoinColumn(name=HibernateConsts.SURVEY_FK)
    @Fetch(FetchMode.JOIN)
    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    @Column(name = HibernateConsts.SURVEY_RESPONSE_DATE)
    public LocalDate getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(LocalDate responseDate) {
        this.responseDate = responseDate;
    }

    @JsonIgnore
    @Column(name = HibernateConsts.SURVEY_RESPONSE, columnDefinition="blob")
    public String getAnswersString() {
        try {
            return EdPanelObjectMapper.MAPPER.writeValueAsString(answers);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public void setAnswersString(String string) {
        if(null == string) {
            this.answers = null;
        } else {
            try {
                this.answers = EdPanelObjectMapper.MAPPER.readValue( string, new TypeReference<List<QuestionAnswer>>(){});
            } catch (IOException e) {
                this.answers =  null;
            }
        }
    }

    @Transient
    public List<QuestionAnswer> getAnswers() {
        return answers;
    }

    @Transient
    public void setAnswers(List<QuestionAnswer> answers) {
        this.answers = answers;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(respondent, responseDate, answers, survey);
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
        final SurveyResponse other = (SurveyResponse) obj;
        return Objects.equals(this.respondent, other.respondent)
                && Objects.equals(this.responseDate, other.responseDate)
                && Objects.equals(this.survey, other.survey)
                && Objects.equals(this.answers, other.answers);
    }
}
