package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.School;
import com.scholarscore.models.gpa.AddedValueGpa;
import com.scholarscore.models.gpa.Gpa;
import com.scholarscore.models.user.Student;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by markroper on 11/24/15.
 */
@Test(groups = { "integration" })
public class GpaControllerIntegrationTest extends IntegrationBase {
    private int numberOfItemsCreated = 0;
    private School school;
    private List<Student> students = new ArrayList<>();

    @BeforeClass
    public void init() {
        authenticate();

        school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolValidatingExecutor.create(school, "Create base school");
        for(int i = 0; i < 10; i++) {
            Student student = new Student();
            student.setName(localeServiceUtil.generateName());
            student.setCurrentSchoolId(school.getId());
            student = studentValidatingExecutor.create(student, "create base student");
            students.add(student);
        }
    }

    @DataProvider
    public Object[][] createGpaProvider() {
        Student namedStudent = new Student();
        namedStudent.setName(localeServiceUtil.generateName());
        Object[][] objects = new Object[students.size()][2];
        int i = 0;
        for(Student s: students) {
            AddedValueGpa g = new AddedValueGpa();
            g.setStudentId(s.getId());
            g.setCalculationDate(LocalDate.now());
            g.setScore(RandomUtils.nextDouble(0D, 5D));
            objects[i][0] = "GPA: " + i;
            objects[i][1] = g;
            i++;
        }
        return objects;
    }

    @Test(dataProvider = "createGpaProvider")
    public void createGpas(String msg, Gpa gpa) {
        this.gpaValidatingExecutor.create(gpa.getStudentId(), gpa, msg);
    }


}
