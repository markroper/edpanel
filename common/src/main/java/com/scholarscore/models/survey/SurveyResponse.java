package com.scholarscore.models.survey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.scholarscore.models.ApiModel;
import com.scholarscore.models.HibernateConsts;
import com.scholarscore.models.user.User;
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
import java.beans.Transient;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

/**
 * Expresses a single user's response to a single survey.
 *
 * Created by markroper on 12/16/15.
 */
@Entity(name = HibernateConsts.SURVEY_RESPONSES_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyResponse extends ApiModel {
    protected User respondent;
    protected LocalDate responseDate;
    protected Survey survey;
    // hibernate uses different getter to access the string value and store in a blob
    protected Map<SurveyQuestion<?>, ?> answers;

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
    @JoinColumn(name = HibernateConsts.SURVEY_USER_FK)
    @Fetch(FetchMode.JOIN)
    public User getRespondent() {
        return respondent;
    }

    public void setRespondent(User respondent) {
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
    @Column(name = HibernateConsts.SURVEY_SCHEMA)
    public String getQuestionsString() {
        try {
            return EdPanelObjectMapper.MAPPER.writeValueAsString(answers);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public void setQuestionsString(String string) {
        if(null == string) {
            this.answers = null;
        } else {
            try {
                this.answers = EdPanelObjectMapper.MAPPER.readValue( string, new TypeReference<Map<SurveyQuestion<?>, ?>>(){});
            } catch (IOException e) {
                this.answers =  null;
            }
        }
    }

    @Transient
    public Map<SurveyQuestion<?>, ?> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<SurveyQuestion<?>, ?> answers) {
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
