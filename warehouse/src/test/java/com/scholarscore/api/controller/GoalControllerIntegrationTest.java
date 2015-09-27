package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by cwallace on 9/21/2015.
 */
@Test(groups = { "integration" })
public class GoalControllerIntegrationTest extends IntegrationBase {

    private Student student;
    private Teacher teacher;
    private School school;
    private SchoolYear schoolYear;
    private Term term;
    private Course course;
    private Section section;
    private GradedAssignment sectionAssignment;
    private StudentAssignment studentAssignment;
    StudentSectionGrade studentSectionGrade;

    private static final Float EXPECTED_SECTION_GRADE = 5.3f;

    private int itemsCreated = 0;

    @BeforeClass
    public void init() {
        authenticate();

        student = new Student();
        student.setName(localeServiceUtil.generateName());
        student = studentValidatingExecutor.create(student, "create base student");

        teacher = new Teacher();
        teacher.setName(localeServiceUtil.generateName());
        teacher = teacherValidatingExecutor.create(teacher, "create base teacher");

        school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolValidatingExecutor.create(school, "Create base school");

        schoolYear = new SchoolYear();
        schoolYear.setName(localeServiceUtil.generateName());
        schoolYear = schoolYearValidatingExecutor.create(school.getId(), schoolYear, "create base schoolYear");

        term = new Term();
        term.setName(localeServiceUtil.generateName());
        term = termValidatingExecutor.create(school.getId(), schoolYear.getId(), term, "create test base term");

        course = new Course();
        course.setName(localeServiceUtil.generateName());
        course = courseValidatingExecutor.create(school.getId(), course, "create base course");

        section = new Section();
        section.setCourse(course);
        section.setName(localeServiceUtil.generateName());
        section.setEnrolledStudents(new ArrayList<Student>());
        section.getEnrolledStudents().add(student);
        section.setTeachers(new HashSet<Teacher>());
        section.getTeachers().add(teacher);
        section = sectionValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section, "create test base term");

        sectionAssignment = new GradedAssignment();
        sectionAssignment.setType(AssignmentType.FINAL);
        sectionAssignment.setName(localeServiceUtil.generateName());
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();
        cal.add(Calendar.YEAR, 1); // to get previous year add -1
        Date nextYear = cal.getTime();
        sectionAssignment.setAssignedDate(today);
        sectionAssignment.setDueDate(nextYear);
        sectionAssignment = (GradedAssignment) sectionAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(),
                term.getId(), section.getId(), sectionAssignment, "create test base term");



        studentAssignment = new StudentAssignment();
        studentAssignment.setAssignment(sectionAssignment);
        studentAssignment.setStudent(student);

        studentAssignment = studentAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(),
                section.getId(), sectionAssignment.getId(), studentAssignment, "Initializing assignmnet");

        studentSectionGrade = new StudentSectionGrade();
        studentSectionGrade.setGrade(EXPECTED_SECTION_GRADE.doubleValue());
        studentSectionGrade.setComplete(false);
        studentSectionGrade = studentSectionGradeValidatingExecutor.update(school.getId(), schoolYear.getId(), term.getId(), section.getId(),
                student.getId(), studentSectionGrade, "update student section grade w/ value " + EXPECTED_SECTION_GRADE);
    }

    @DataProvider(name = "createGoalDataProvider")
    public Object[][] createGoalDataMethod() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();
        cal.add(Calendar.YEAR, 1); // to get previous year add -1
        Date nextYear = cal.getTime();



        BehaviorGoal behaviorGoal = new BehaviorGoal();
        behaviorGoal.setStudent(student);
        behaviorGoal.setTeacher(teacher);
        behaviorGoal.setBehaviorCategory(BehaviorCategory.DEMERIT);
        behaviorGoal.setStartDate(today);
        behaviorGoal.setEndDate(nextYear);
        behaviorGoal.setDesiredValue(41.5f);
        behaviorGoal.setName("To win them all");
        behaviorGoal.setApproved(false);

        AssignmentGoal assGoal = new AssignmentGoal();
        assGoal.setStudent(student);
        assGoal.setTeacher(teacher);
        assGoal.setName("The final final");
        assGoal.setApproved(false);
        assGoal.setParentId(studentAssignment.getId());
        assGoal.setDesiredValue(95f);

        CumulativeGradeGoal cumulativeGradeGoal = new CumulativeGradeGoal();
        cumulativeGradeGoal.setStudent(student);
        cumulativeGradeGoal.setTeacher(teacher);
        cumulativeGradeGoal.setName("ALL OF THE As");
        cumulativeGradeGoal.setApproved(false);
        cumulativeGradeGoal.setParentId(section.getId());
        cumulativeGradeGoal.setDesiredValue(6f);

        return new Object[][] {
                {behaviorGoal, "Test failed with a behavior goal"},
                {assGoal, "Test failed with an assignment goal"},
                {cumulativeGradeGoal, "Test failed with a cumulative grade goal"}
        };
    }

    @Test(dataProvider = "createGoalDataProvider" )
    public void createTest(Goal goal, String msg) {
        goalValidatingExecutor.create(goal.getStudent().getId(), goal, msg);
        itemsCreated++;
    }

    @Test(dataProvider = "createGoalDataProvider")
    public void deleteGoalTest(Goal goal, String msg) {
        Goal createdGoal = goalValidatingExecutor.create(goal.getStudent().getId(), goal, msg);
        goalValidatingExecutor.delete(student.getId(), createdGoal.getId(), msg);
    }

    @DataProvider(name = "testCalculatedMethodDataProvider")
    public Object[][] testCalculatedValuesDateMethod() {
        Float EXPECTED_VALUE = 3F;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();
        cal.add(Calendar.YEAR, -1); // to get previous year add -1
        Date lastYear = cal.getTime();
        cal.add(Calendar.MONTH, 1);
        Date midDate = cal.getTime();


        BehaviorGoal behaviorGoal = new BehaviorGoal();
        behaviorGoal.setStudent(student);
        behaviorGoal.setTeacher(teacher);
        behaviorGoal.setBehaviorCategory(BehaviorCategory.DEMERIT);
        behaviorGoal.setStartDate(lastYear);
        behaviorGoal.setEndDate(today);
        behaviorGoal.setDesiredValue(41f);
        behaviorGoal.setName("To win them all");
        behaviorGoal.setApproved(false);
        behaviorGoal.setCalculatedValue(EXPECTED_VALUE);

        Behavior namedBehavior = new Behavior();
        // teacher is always required or constraint exception
        namedBehavior.setStudent(student);
        namedBehavior.setTeacher(teacher);
        namedBehavior.setName("BehaviorEvent");
        namedBehavior.setBehaviorCategory(BehaviorCategory.DEMERIT);
        namedBehavior.setPointValue("1");
        namedBehavior.setBehaviorDate(midDate);
        for (int i = 0; i < EXPECTED_VALUE; i++) {
            behaviorValidatingExecutor.create(student.getId(), namedBehavior, "Beahvior creation failed");
        }
        return new Object[][]{
                {behaviorGoal,"We did not receive teh expected value from your goal"}
        };

    }

        @Test(dataProvider = "testCalculatedMethodDataProvider")
        public void testCalculatedValue(Goal goal, String message) {
            goalValidatingExecutor.create(goal.getStudent().getId(), goal, message);
            itemsCreated++;

    }

    @Test(dataProvider = "createGoalDataProvider")
    public void replaceGoalTest(Goal goal, String msg) {
        Goal createdGoal = goalValidatingExecutor.create(student.getId(), goal, msg);
        goalValidatingExecutor.replace(student.getId(), createdGoal.getId(), goal, msg);
        itemsCreated++;

    }

    @Test(dataProvider = "createGoalDataProvider")
    public void updateAssignmentTest(Goal goal, String msg) {
        Goal createdGoal = goalValidatingExecutor.create(student.getId(), goal, msg);

        Goal updatedGoal;
        if (goal instanceof BehaviorGoal) {
            updatedGoal = new BehaviorGoal((BehaviorGoal)createdGoal);
        } else if (goal instanceof AssignmentGoal) {
            updatedGoal = new AssignmentGoal((AssignmentGoal)createdGoal);
        } else if (goal instanceof CumulativeGradeGoal){
            updatedGoal = new CumulativeGradeGoal((CumulativeGradeGoal)createdGoal);
        } else {
            updatedGoal = null;
        }

        updatedGoal.setName(localeServiceUtil.generateName());
        //PATCH the existing record with a new name.
        goalValidatingExecutor.update(student.getId(), createdGoal.getId(), goal, msg);
        itemsCreated++;
    }

    @Test
    public void testGetAll() {
        goalValidatingExecutor.getAll(student.getId(),"Get all test failed", itemsCreated);
    }
}
