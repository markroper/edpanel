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
import javax.persistence.Transient;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a survey definition including properties like survey name, survey questions, survey creator, and so on.
 *
 * One survey can be responded to by multiple users.  These responses are expressed in SurveyResponse.java
 *
 * Created by markroper on 12/16/15.
 */
@Entity(name = HibernateConsts.SURVEY_TABLE)
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Survey extends ApiModel {
    protected Long schoolFk;
    protected User creator;
    protected LocalDate createdDate;
    protected LocalDate administeredDate;
    //For jackson & for java (hibernate uses different getter to access the string value and store in a blob)
    @Valid
    protected SurveySchema questions;

    @Override
    public void mergePropertiesIfNull(ApiModel model) {
        super.mergePropertiesIfNull(model);
        if(null != model && model instanceof Survey) {
            Survey input = (Survey) model;
            if(null == this.schoolFk) {
                this.schoolFk = input.schoolFk;
            }
            if(null == this.creator) {
                this.creator = input.creator;
            }
            if(null == this.createdDate) {
                this.createdDate = input.createdDate;
            }
            if(null == this.administeredDate) {
                this.administeredDate = input.administeredDate;
            }
            if(null == this.questions) {
                this.questions = input.questions;
            }
        }
    }
    @Id
    @Column(name = HibernateConsts.SURVEY_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Override
    public Long getId() {
        return this.id;
    }

    @Column(name = HibernateConsts.SURVEY_NAME)
    @Override
    public String getName() {
        return this.name;
    }

    @Column(name = HibernateConsts.SCHOOL_FK)
    public Long getSchoolFk() {
        return schoolFk;
    }

    public void setSchoolFk(Long schoolFk) {
        this.schoolFk = schoolFk;
    }

    @ManyToOne(optional = true, fetch= FetchType.EAGER)
    @JoinColumn(name = HibernateConsts.SURVEY_USER_FK)
    @Fetch(FetchMode.JOIN)
    public User getCreator() {
        return creator;
    }

    public void setCreator(User user) {
        this.creator = user;
    }

    @Column(name = HibernateConsts.SURVEY_CREATED_DATE)
    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    @Column(name = HibernateConsts.SURVEY_ADMINISTER_DATE)
    public LocalDate getAdministeredDate() {
        return administeredDate;
    }

    public void setAdministeredDate(LocalDate administeredDate) {
        this.administeredDate = administeredDate;
    }

    @JsonIgnore
    @Column(name = HibernateConsts.SURVEY_SCHEMA, columnDefinition="blob")
    public String getQuestionsString() {
        try {
            return EdPanelObjectMapper.MAPPER.writeValueAsString(questions);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public void setQuestionsString(String string) {
        if(null == string) {
            this.questions = null;
        } else {
            try {
                this.questions = EdPanelObjectMapper.MAPPER.readValue( string, new TypeReference<SurveySchema>(){});
            } catch (IOException e) {
                this.questions =  null;
            }
        }
    }

    @Transient
    public SurveySchema getQuestions() {
        return questions;
    }

    @Transient
    public void setQuestions(SurveySchema questions) {
        this.questions = questions;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(schoolFk, creator, createdDate, administeredDate, questions);
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
        final Survey other = (Survey) obj;
        return Objects.equals(this.schoolFk, other.schoolFk)
                && Objects.equals(this.creator, other.creator)
                && Objects.equals(this.createdDate, other.createdDate)
                && Objects.equals(this.administeredDate, other.administeredDate)
                && Objects.equals(this.questions, other.questions);
    }
}
