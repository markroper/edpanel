package com.scholarscore.models;

import com.scholarscore.models.grade.SectionGrade;
import com.scholarscore.models.grade.StudentSectionGrade;
import com.scholarscore.models.user.Student;
import com.scholarscore.utils.CommonTestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;

/**
 * StudentSectionGradeBuilderUnitTest tests that we can create equivalent StudentSectionGrade objects from
 * setters and builders
 * Created by cschneider on 10/11/15.
 */
@Test(groups = { "unit" })
public class StudentSectionGradeBuilderUnitTest extends AbstractBuilderUnitTest<StudentSectionGrade>{
    @DataProvider
    @Override
    public Object[][] builderProvider() {
        StudentSectionGrade emptyStudentSectionGrade = new StudentSectionGrade();
        StudentSectionGrade emptyStudentSectionGradeByBuilder = new StudentSectionGrade.StudentSectionGradeBuilder().build();

        String name = RandomStringUtils.randomAlphabetic(15);
        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        Boolean complete = CommonTestUtils.getRandomBoolean();
        Double grade = RandomUtils.nextDouble(0d, 100d);
        Section section = CommonTestUtils.generateSection();
        Student student = CommonTestUtils.generateStudent();

        SectionGrade overall = new SectionGrade();
        overall.setDate(LocalDate.now());
        overall.setScore(RandomUtils.nextDouble(0d, 100d));
        overall.setManuallyOverridden(false);
        overall.setSectionFk(section.getId());

        StudentSectionGrade fullStudentSectionGrade = new StudentSectionGrade();
        fullStudentSectionGrade.setId(id);
        fullStudentSectionGrade.setName(name);
        fullStudentSectionGrade.setComplete(complete);
        fullStudentSectionGrade.setOverallGrade(overall);
        fullStudentSectionGrade.setSection(section);
        fullStudentSectionGrade.setStudent(student);

        StudentSectionGrade fullStudentSectionGradeBuilder = new StudentSectionGrade.StudentSectionGradeBuilder()
                .withId(id)
                .withName(name)
                .withComplete(complete)
                .withOverallGrade(overall)
                .withSection(section)
                .withStudent(student)
                .build();

        return new Object[][]{
                {"Empty student section grade", emptyStudentSectionGradeByBuilder, emptyStudentSectionGrade},
                {"Full student section grade", fullStudentSectionGradeBuilder, fullStudentSectionGrade},
        };
    }
}
