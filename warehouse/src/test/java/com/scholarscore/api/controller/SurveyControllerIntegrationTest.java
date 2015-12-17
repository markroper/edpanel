package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.School;
import com.scholarscore.models.survey.Survey;
import com.scholarscore.models.survey.question.SurveyBooleanQuestion;
import com.scholarscore.models.survey.question.SurveyMultipleChoiceQuestion;
import com.scholarscore.models.survey.question.SurveyOpenResponseQuestion;
import com.scholarscore.models.survey.question.SurveyQuestion;
import com.scholarscore.models.survey.SurveyResponse;
import com.scholarscore.models.survey.SurveySchema;
import com.scholarscore.models.survey.answer.BooleanAnswer;
import com.scholarscore.models.survey.answer.MultipleChoiceAnswer;
import com.scholarscore.models.survey.answer.OpenAnswer;
import com.scholarscore.models.survey.answer.QuestionAnswer;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by markroper on 12/16/15.
 */
@Test( groups = { "integration" })
public class SurveyControllerIntegrationTest extends IntegrationBase {
    private School school;
    private Student student1;
    private Teacher teacher1;
    private Student student2;
    private Teacher teacher2;
    private SurveySchema simpleSchema;
    SurveyBooleanQuestion boolQ;
    SurveyOpenResponseQuestion openQ;
    SurveyMultipleChoiceQuestion mcQ;

    @BeforeClass
    public void init() {
        authenticate();
        school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolValidatingExecutor.create(school, "Create base school");

        student1 = new Student();
        student1.setName(localeServiceUtil.generateName());
        student1 = studentValidatingExecutor.create(student1, "create base student");

        teacher1 = new Teacher();
        teacher1.setName(localeServiceUtil.generateName());
        teacher1 = teacherValidatingExecutor.create(teacher1, "create base teacher");

        student2 = new Student();
        student2.setName(localeServiceUtil.generateName());
        student2 = studentValidatingExecutor.create(student2, "create base student");

        teacher2 = new Teacher();
        teacher2.setName(localeServiceUtil.generateName());
        teacher2 = teacherValidatingExecutor.create(teacher2, "create base teacher");

        boolQ = new SurveyBooleanQuestion();
        boolQ.setShowAsCheckbox(true);
        boolQ.setQuestion("Can I ask you a question?");
        boolQ.setResponseRequired(true);
        openQ = new SurveyOpenResponseQuestion();
        openQ.setMaxResponseLength(100);
        openQ.setQuestion("Can I ask you a question?");
        openQ.setResponseRequired(true);
        mcQ = new SurveyMultipleChoiceQuestion();
        mcQ.setChoices(new ArrayList<String>(){{ add("one"); add("two"); }});
        mcQ.setQuestion("Can I ask you a question?");
        mcQ.setResponseRequired(false);
        SurveySchema simpleSchema = new SurveySchema();
        simpleSchema.setQuestions(new ArrayList<SurveyQuestion>(){{ add(boolQ); add(openQ); add(mcQ); }});
    }

    @DataProvider
    public Object[][] createSurveysProvider() {
        Survey simpleSurvey = new Survey();
        simpleSurvey.setCreator(teacher1);
        simpleSurvey.setQuestions(simpleSchema);

        Survey createdDate = new Survey();
        createdDate.setCreator(teacher1);
        createdDate.setQuestions(simpleSchema);
        createdDate.setCreatedDate(LocalDate.now());

        Survey administerDate = new Survey();
        administerDate.setCreator(teacher1);
        administerDate.setQuestions(simpleSchema);
        administerDate.setAdministeredDate(LocalDate.now().plusDays(10));
        administerDate.setCreatedDate(LocalDate.now());

        Survey schoolFk = new Survey();
        schoolFk.setCreator(teacher1);
        schoolFk.setQuestions(simpleSchema);
        schoolFk.setSchoolFk(school.getId());
        schoolFk.setAdministeredDate(LocalDate.now().plusDays(10));
        schoolFk.setCreatedDate(LocalDate.now());

        return new Object[][] {
                { "Empty behavior", simpleSurvey },
                { "Named behavior", createdDate },
                { "Fully populated behavior", administerDate },
                { "Fully populated behavior", schoolFk },
        };
    }

    @Test(dataProvider = "createSurveysProvider")
    public void createSurvey(String msg, Survey s) {
        this.surveyValidatingExecutor.create(s, msg);
    }

    @Test
    public void createDelete() {
        Survey simpleSurvey = new Survey();
        simpleSurvey.setCreator(teacher1);
        simpleSurvey.setQuestions(simpleSchema);
        simpleSurvey = this.surveyValidatingExecutor.create(simpleSurvey, "Create to be deleted");
        this.surveyValidatingExecutor.delete(simpleSurvey.getId(), "Delete that shit");
    }

    @Test
    public void createGetAll() {
        Survey simpleSurvey = new Survey();
        simpleSurvey.setCreator(teacher2);
        simpleSurvey.setSchoolFk(school.getId());
        simpleSurvey.setQuestions(simpleSchema);

        Survey createdDate = new Survey();
        createdDate.setCreator(teacher2);
        createdDate.setQuestions(simpleSchema);
        createdDate.setSchoolFk(school.getId());
        createdDate.setCreatedDate(LocalDate.now());

        Survey administerDate = new Survey();
        administerDate.setCreator(teacher2);
        administerDate.setQuestions(simpleSchema);
        administerDate.setSchoolFk(school.getId());
        administerDate.setAdministeredDate(LocalDate.now().plusDays(10));
        administerDate.setCreatedDate(LocalDate.now());

        Survey schoolFk = new Survey();
        schoolFk.setCreator(teacher2);
        schoolFk.setQuestions(simpleSchema);
        schoolFk.setSchoolFk(school.getId());
        schoolFk.setAdministeredDate(LocalDate.now().plusDays(10));
        schoolFk.setCreatedDate(LocalDate.now());

        this.surveyValidatingExecutor.create(simpleSurvey, "");
        this.surveyValidatingExecutor.create(createdDate, "");
        this.surveyValidatingExecutor.create(administerDate, "");
        this.surveyValidatingExecutor.create(schoolFk, "");

        List<Survey> surveyrs = this.surveyValidatingExecutor.getBySchoolAndDate(school.getId(), null, null, "");
        Assert.assertTrue(surveyrs.size() >= 4, "Unexpected number of survey's returned for school");

        List<Survey> teachersSurveys = this.surveyValidatingExecutor.getByUserId(teacher2.getId(), "");
        Assert.assertTrue(teachersSurveys.size() >= 4, "Unexpected number of surveys returned for teacher");
    }

    @DataProvider
    public Object[][] createSurveysNegProvider() {
        Survey missingSchool = new Survey();
        missingSchool.setCreator(teacher2);
        missingSchool.setQuestions(simpleSchema);
        missingSchool.setSchoolFk(1234567L);

        Survey noQuestions = new Survey();
        noQuestions.setQuestions(new SurveySchema());
        noQuestions.setCreator(teacher2);

        Survey noCreator = new Survey();
        noCreator.setQuestions(simpleSchema);

        return new Object[][]{
                { "No school", missingSchool, HttpStatus.BAD_REQUEST },
                { "No questions", noQuestions, HttpStatus.BAD_REQUEST },
                { "No creator", noCreator, HttpStatus.BAD_REQUEST }
        };
    }

    @Test(dataProvider = "createSurveysNegProvider")
    public void createSurveyNeg(String msg, Survey s, HttpStatus status) {
        this.surveyValidatingExecutor.createNegative(s, status, msg);
    }

    @Test
    public void createSurveyResponseAndValidate() {
        Survey simpleSurvey = new Survey();
        simpleSurvey.setCreator(teacher1);
        simpleSurvey.setQuestions(simpleSchema);
        simpleSurvey = this.surveyValidatingExecutor.create(simpleSurvey, "Create to be deleted");

        SurveyResponse resp = new SurveyResponse();
        resp.setSurvey(simpleSurvey);
        resp.setRespondent(student1);
        List<QuestionAnswer> answers = new ArrayList<>();
        BooleanAnswer ba = new BooleanAnswer();
        ba.setAnswer(true);
        ba.setQuestion(boolQ);
        answers.add(ba);
        MultipleChoiceAnswer ma = new MultipleChoiceAnswer();
        ma.setAnswer(2);
        ma.setQuestion(mcQ);
        answers.add(ma);
        OpenAnswer oa = new OpenAnswer();
        oa.setAnswer("something something");
        oa.setQuestion(openQ);
        answers.add(oa);
        resp.setAnswers(answers);
        resp = this.surveyResponseValidatingExecutor.create(resp, "Create a survey response");

        SurveyResponse resp2 = new SurveyResponse();
        resp2.setSurvey(simpleSurvey);
        resp2.setRespondent(student2);
        resp2.setAnswers(answers);
        resp2 = this.surveyResponseValidatingExecutor.create(resp2, "Create a survey response");

        List<SurveyResponse> responses =
                this.surveyResponseValidatingExecutor.getBySurveyId(simpleSurvey.getId(), "get two surveys");
        Assert.assertEquals(responses.size(), 2, "Unexpected number of responses returned");

        List<SurveyResponse> student2Responses =
                this.surveyResponseValidatingExecutor.getByRespondentAndDate(student2.getUserId(), null, null, "get student2's surveys");
        Assert.assertEquals(student2Responses.size(), 1, "Unexpected number of responses returned");

        this.surveyResponseValidatingExecutor.delete(simpleSurvey.getId(), resp.getId(), "delete survey");
        this.surveyResponseValidatingExecutor.delete(simpleSurvey.getId(), resp2.getId(), "delete survey");
        this.surveyValidatingExecutor.delete(simpleSurvey.getId(), "Delete that shit");
    }
}
