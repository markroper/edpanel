package com.scholarscore.models;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Sets;
import com.scholarscore.models.assignment.Assignment;
import com.scholarscore.models.gradeformula.GradeFormula;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;
import com.scholarscore.utils.CommonTestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * SectionBuilderUnitTest tests that we can build equivalent Section objects using setters or builders
 * Created by cschneider on 10/11/15.
 */
@Test(groups = { "unit" })
public class SectionBuilderUnitTest extends AbstractBuilderUnitTest<Section>{
    @DataProvider
    @Override
    public Object[][] builderProvider() {
        Section emptySection = new Section();
        Section emptySectionByBuilder = new Section.SectionBuilder().build();

        String name = RandomStringUtils.randomAlphabetic(15);
        Long id = RandomUtils.nextLong(0L, Long.MAX_VALUE);
        LocalDate startDate = CommonTestUtils.getRandomLocalDate();
        LocalDate endDate = startDate.plusWeeks(RandomUtils.nextInt(1, 5));
        String room = RandomStringUtils.randomAlphanumeric(3);
        GradeFormula gradeFormula = CommonTestUtils.generateGradeFormula();
        String gradeFormulaString = RandomStringUtils.randomAlphanumeric(10);
        Term term = CommonTestUtils.generateTerm(LocalDate.now(),
                LocalDate.now().plusMonths(3),
                CommonTestUtils.generateSchoolYearWithoutTerms(CommonTestUtils.generateSchool()));
        Course course = CommonTestUtils.generateCourse();
        String sourceSystemId = RandomStringUtils.randomNumeric(10);

        Set<Teacher> teachers = Sets.newHashSet();
        for(int i = 0; i < RandomUtils.nextInt(1, 5); i++) {
            teachers.add(CommonTestUtils.generateTeacher());
        }

        List<Student> enrolledStudents = Lists.newArrayList();
        List<StudentSectionGrade> grades = Lists.newArrayList();
        for(int i = 0; i < RandomUtils.nextInt(10, 30); i++) {
            Student student = CommonTestUtils.generateStudent();
            enrolledStudents.add(student);
            grades.add(CommonTestUtils.generateSectionGradeWithoutSection(student));
        }

        List<Assignment> assignments = Lists.newArrayList();
        for(int i = 0; i< RandomUtils.nextInt(10, 20); i++) {
            assignments.add(CommonTestUtils.generateRandomAssignmentWithoutSection());
        }

        Section fullSection = new Section();
        fullSection.setId(id);
        fullSection.setName(name);
        fullSection.setStartDate(startDate);
        fullSection.setEndDate(endDate);
        fullSection.setRoom(room);
        fullSection.setGradeFormula(gradeFormula);
        fullSection.setGradeFormulaString(gradeFormulaString);
        fullSection.setTerm(term);
        fullSection.setCourse(course);
        fullSection.setTeachers(teachers);
        fullSection.setEnrolledStudents(enrolledStudents);
        fullSection.setAssignments(assignments);
        fullSection.setSourceSystemId(sourceSystemId);

        Section fullSectionBuilder = new Section.SectionBuilder()
                .withId(id)
                .withName(name)
                .withStartDate(startDate)
                .withEndDate(endDate)
                .withRoom(room)
                .withGradeFormula(gradeFormula)
                .withTerm(term)
                .withCourse(course)
                .withTeachers(teachers)
                .withEnrolledStudents(enrolledStudents)
                .withAssignments(assignments)
                .withStudentSectionGrades(grades)
                .withSourceSystemId(sourceSystemId)
                .build();

        Section.SectionBuilder sectionOneByOneBuilder = new Section.SectionBuilder()
                .withId(id)
                .withName(name)
                .withStartDate(startDate)
                .withEndDate(endDate)
                .withRoom(room)
                .withGradeFormula(gradeFormula)
                .withTerm(term)
                .withCourse(course)
                .withSourceSystemId(sourceSystemId);

        for(Teacher teacher : teachers){
            sectionOneByOneBuilder.withTeacher(teacher);

        }

        for(Student student : enrolledStudents){
            sectionOneByOneBuilder.withEnrolledStudent(student);
        }

        for(Assignment assignment : assignments){
            sectionOneByOneBuilder.withAssignment(assignment);
        }

        for(StudentSectionGrade grade : grades){
            sectionOneByOneBuilder.withStudentSectionGrade(grade);
        }

        Section sectionOneByOne = sectionOneByOneBuilder.build();

        return new Object[][]{
                {"Empty section", emptySectionByBuilder, emptySection},
                {"Full section", fullSectionBuilder, fullSection},
                {"Full section built one by one in lists", sectionOneByOne, fullSection}
        };
    }
}
