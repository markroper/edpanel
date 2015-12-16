package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.School;
import com.scholarscore.models.survey.Survey;
import com.scholarscore.models.survey.SurveyBooleanQuestion;
import com.scholarscore.models.survey.SurveyMultipleChoiceQuestion;
import com.scholarscore.models.survey.SurveyOpenResponseQuestion;
import com.scholarscore.models.survey.SurveyQuestion;
import com.scholarscore.models.survey.SurveySchema;
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

        SurveyBooleanQuestion boolQ = new SurveyBooleanQuestion();
        boolQ.setShowAsCheckbox(true);
        boolQ.setQuestion("Can I ask you a question?");
        boolQ.setResponseRequired(true);
        SurveyOpenResponseQuestion openQ = new SurveyOpenResponseQuestion();
        openQ.setMaxResponseLength(100);
        openQ.setQuestion("Can I ask you a question?");
        openQ.setResponseRequired(true);
        SurveyMultipleChoiceQuestion mcQ = new SurveyMultipleChoiceQuestion();
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

        return new Object[][]{
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
        Assert.assertEquals(surveyrs.size(), 4, "Unexpected number of survey's returned for school");

        List<Survey> teachersSurveys = this.surveyValidatingExecutor.getByUserId(teacher2.getId(), "");
        Assert.assertEquals(teachersSurveys.size(), 4, "Unexpected number of surveys returned for teacher");
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
}
