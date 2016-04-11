package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.School;
import com.scholarscore.models.state.ma.McasComplexity;
import com.scholarscore.models.state.ma.McasPerfLevel;
import com.scholarscore.models.state.ma.McasPerfLevel2;
import com.scholarscore.models.state.ma.McasResult;
import com.scholarscore.models.state.ma.McasStatus;
import com.scholarscore.models.state.ma.McasTopicScore;
import com.scholarscore.models.user.Student;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by markroper on 11/24/15.
 */
@Test(groups = { "integration" })
public class McasControllerIntegrationTest extends IntegrationBase {
    private School school;
    private List<Student> students = new ArrayList<>();

    @BeforeClass
    public void init() {
        authenticate();

        school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolValidatingExecutor.create(school, "Create base school");
        for(int i = 0; i < 5; i++) {
            Student student = new Student();
            student.setName(localeServiceUtil.generateName());
            student.setCurrentSchoolId(school.getId());
            student = studentValidatingExecutor.create(student, "create base student");
            students.add(student);
        }
    }

    private static McasTopicScore genScore() {
        McasTopicScore score = new McasTopicScore();
        score.setAlternateExam(true);
        score.setQuartile(RandomUtils.nextLong(0l, 101l));
        score.setPerformanceLevel2(McasPerfLevel2.ADVANCED);
        score.setPerformanceLevel(McasPerfLevel.ADVANCED_ALT);
        score.setRawScore(RandomUtils.nextDouble(0, 200));
        score.setScaledScore(RandomUtils.nextDouble(0, 100));
        score.setAlternateExam(false);
        score.setComplexity(McasComplexity.GL);
        score.setExamStatus(McasStatus.TESTED);
        return score;
    }
    @DataProvider
    public Object[][] createMcasProvider() {
        Student namedStudent = new Student();
        namedStudent.setName(localeServiceUtil.generateName());
        Object[][] objects = new Object[students.size()][2];
        int i = 0;
        for(Student s: students) {
            McasResult result = new McasResult();
            result.setAdminYear(RandomUtils.nextLong(2000, 2016));
            result.setStudent(s);
            result.setSchoolId(school.getId());
            result.setEnglishCompositionScore(RandomUtils.nextDouble(0, 200));
            result.setEnglishTopicScore(RandomUtils.nextDouble(0, 200));
            result.setMathScore(genScore());
            result.setEnglishScore(genScore());
            if(s.getId() % 2 == 0) {
                result.setScienceScore(genScore());
            }
            objects[i][0] = "Mcas Score: " + i;
            objects[i][1] = result;
            i++;
        }
        return objects;
    }

    @Test(dataProvider = "createMcasProvider")
    public void createAndDeleteMcas(String msg, McasResult result) {
        McasResult created = this.mcasValidatinExecutor.create(school.getId(), result.getStudent().getId(), result, msg);
        this.mcasValidatinExecutor.delete(school.getId(), result.getStudent().getId(), created.getId(), msg);
    }

    public void createMultipleGpasForOneStudent() {
        Student student = new Student();
        student.setName(localeServiceUtil.generateName());
        student.setCurrentSchoolId(school.getId());
        student = studentValidatingExecutor.create(student, "create base student");
        List<Long> ids = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            McasResult result = new McasResult();
            result.setAdminYear(RandomUtils.nextLong(2000, 2016));
            result.setStudent(student);
            result.setSchoolId(school.getId());
            result.setEnglishCompositionScore(RandomUtils.nextDouble(0, 200));
            result.setEnglishTopicScore(RandomUtils.nextDouble(0, 200));
            result.setMathScore(genScore());
            result.setEnglishScore(genScore());
            if(student.getId() % 2 == 0) {
                result.setScienceScore(genScore());
            }
            ids.add(mcasValidatinExecutor.create(school.getId(), student.getId(), result, "craete multiple GPAs for one student").getId());
        }
        mcasValidatinExecutor.getAll(school.getId(), student.getId(), 3, "Number of items returned doesn't match number created");
    }

    public void createThenUpdate() {
        Student student = new Student();
        student.setName(localeServiceUtil.generateName());
        student.setCurrentSchoolId(school.getId());
        student = studentValidatingExecutor.create(student, "create base student");
        McasResult result = new McasResult();
        result.setAdminYear(RandomUtils.nextLong(2000, 2016));
        result.setStudent(student);
        result.setSchoolId(school.getId());
        result.setEnglishCompositionScore(RandomUtils.nextDouble(0, 200));
        result.setEnglishTopicScore(RandomUtils.nextDouble(0, 200));
        result.setMathScore(genScore());
        result.setEnglishScore(genScore());
        if(student.getId() % 2 == 0) {
            result.setScienceScore(genScore());
        }
        McasResult created = mcasValidatinExecutor.create(
                school.getId(), student.getId(), result, "create multiple GPAs for one student");

        created.setEnglishScore(genScore());
        mcasValidatinExecutor.replace(school.getId(), student.getId(), created.getId(), created, "Updating a crated MCAS result");
        mcasValidatinExecutor.getAll(school.getId(), student.getId(), 1, "Number of items returned doesn't match number created");
    }

}
