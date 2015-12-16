package com.scholarscore.api.persistence.mysql.jdbc;

import com.scholarscore.api.persistence.SurveyPersistence;
import com.scholarscore.models.survey.Survey;
import com.scholarscore.models.survey.SurveyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by markroper on 12/16/15.
 */
@Transactional
public class SurveyJdbc implements SurveyPersistence {
    private static final String USER_JOIN_FETCH =
            "join fetch s.creator c left join fetch c.contactMethods left join fetch c.homeAddress";
    private static final String SURVEY_BASE_HQL = "select s from survey s " + USER_JOIN_FETCH;
    private static final String SURVEY_RESP_BASE_HQL =
            "select s from survey_response sr" +
            " join fetch sr.survey s " + USER_JOIN_FETCH +
            " join fetch sr.respondent r" +
                    " left join fetch r.contactMethods" +
                    " left join fetch r.homeAddress" +
                    " left join fetch r.mailingAddress";

    @Autowired
    private HibernateTemplate hibernateTemplate;

    public SurveyJdbc() {
    }

    public SurveyJdbc(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }

    @Override
    public Long insertSurvey(Survey survey) {
        Survey s = this.hibernateTemplate.merge(survey);
        return s.getId();
    }

    @Override
    public Survey selectSurvey(long surveyId) {
        return hibernateTemplate.get(Survey.class, surveyId);
    }

    @Override
    public void deleteSurvey(long surveyId) {
        Survey s = selectSurvey(surveyId);
        if(null != s) {
            hibernateTemplate.delete(s);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Survey> selectSurveyByUserId(long userId) {
        return (List<Survey>) hibernateTemplate.findByNamedParam(
                SURVEY_BASE_HQL + " where s.creator.id = :userId", "userId", userId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Survey> selectSurveyBySchoolId(long schoolId, LocalDate start, LocalDate end) {
        List<Survey> surveys = null;
        if(null == start && null == end) {
            surveys = (List<Survey>)hibernateTemplate.findByNamedParam(
                    SURVEY_BASE_HQL + " where s.schoolFk = :schoolId", "schoolId", schoolId);
        } else {
            String[] params;
            Object[] paramValues;
            String hqlString = SURVEY_BASE_HQL + " where s.schoolFk = :schoolId";
            String endLimit = " and s.createdDate <= :end";
            String startLimit = " and s.createdDate >= :start";
            if(null == end) {
                params = new String[]{"schoolId", "start"};
                paramValues = new Object[]{ new Long(schoolId), start };
                hqlString += startLimit;
            } else if(null == start) {
                params = new String[]{"schoolId", "end"};
                paramValues = new Object[]{ new Long(schoolId), end };
                hqlString += endLimit;
            } else {
                params = new String[]{"schoolId", "start", "end"};
                paramValues = new Object[]{ new Long(schoolId), start, end };
                hqlString += startLimit + endLimit;
            }
            surveys = (List<Survey>)hibernateTemplate.findByNamedParam(
                    hqlString,
                    params,
                    paramValues);
        }
        return surveys;
    }

    @Override
    public long insertSurveyResponse(SurveyResponse resp) {
        SurveyResponse s = this.hibernateTemplate.merge(resp);
        return s.getId();
    }

    @Override
    public void deleteSurveyResponse(long surveyId, long respId) {
        SurveyResponse s = selectSurveyResponse(surveyId, respId);
        if(null != s) {
            hibernateTemplate.delete(s);
        }
    }

    @Override
    public void updateSurveyResponse(long surveyId, long respId, SurveyResponse response) {
        hibernateTemplate.merge(response);
    }

    @Override
    public SurveyResponse selectSurveyResponse(long surveyId, long respId) {
        return hibernateTemplate.get(SurveyResponse.class, respId);
    }

    @Override
    public List<SurveyResponse> selectSurveyResponses(long surveyId) {
        return (List<SurveyResponse>) hibernateTemplate.findByNamedParam(
                SURVEY_RESP_BASE_HQL + " where s.survey.id = :surveyId", "surveyId", surveyId);
    }

    @Override
    public List<SurveyResponse> selectSurveyResponsesByRespondent(long respondentId, LocalDate start, LocalDate end) {
        List<SurveyResponse> responses = null;
        if(null == start && null == end) {
            responses = (List<SurveyResponse>)hibernateTemplate.findByNamedParam(
                    SURVEY_RESP_BASE_HQL + " where sr.respondent.id = :respondentId", "respondentId", respondentId);
        } else {
            String[] params;
            Object[] paramValues;
            String hqlString = SURVEY_RESP_BASE_HQL + " where sr.respondent.id = :respondentId";
            String endLimit = " and s.survey.createdDate <= :end";
            String startLimit = " and s.survey.createdDate >= :start";
            if(null == end) {
                params = new String[]{"respondentId", "start"};
                paramValues = new Object[]{ new Long(respondentId), start };
                hqlString += startLimit;
            } else if(null == start) {
                params = new String[]{"respondentId", "end"};
                paramValues = new Object[]{ new Long(respondentId), end };
                hqlString += endLimit;
            } else {
                params = new String[]{"respondentId", "start", "end"};
                paramValues = new Object[]{ new Long(respondentId), start, end };
                hqlString += startLimit + endLimit;
            }
            responses = (List<SurveyResponse>)hibernateTemplate.findByNamedParam(
                    hqlString,
                    params,
                    paramValues);
        }
        return responses;
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        this.hibernateTemplate = template;
    }
}
