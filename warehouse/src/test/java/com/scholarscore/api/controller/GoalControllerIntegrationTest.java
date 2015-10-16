package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.*;
import com.scholarscore.models.goal.*;
import com.scholarscore.models.user.Student;
import com.scholarscore.models.user.Teacher;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

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
        behaviorGoal.setDesiredValue(41.5d);
        behaviorGoal.setName("To win them all");
        behaviorGoal.setApproved(false);

        AssignmentGoal assGoal = new AssignmentGoal();
        assGoal.setStudent(student);
        assGoal.setTeacher(teacher);
        assGoal.setName("The final final");
        assGoal.setApproved(false);
        assGoal.setParentId(studentAssignment.getId());
        assGoal.setDesiredValue(95d);

        CumulativeGradeGoal cumulativeGradeGoal = new CumulativeGradeGoal();
        cumulativeGradeGoal.setStudent(student);
        cumulativeGradeGoal.setTeacher(teacher);
        cumulativeGradeGoal.setName("ALL OF THE As");
        cumulativeGradeGoal.setApproved(false);
        cumulativeGradeGoal.setParentId(section.getId());
        cumulativeGradeGoal.setDesiredValue(6d);

        AttendanceGoal attendanceGoal = new AttendanceGoal();
        attendanceGoal.setStudent(student);
        attendanceGoal.setTeacher(teacher);
        attendanceGoal.setName("Weekly attendance goal");
        attendanceGoal.setApproved(false);
        attendanceGoal.setParentId(section.getId());
        attendanceGoal.setDesiredValue(5D);
        attendanceGoal.setStartDate(today);
        attendanceGoal.setEndDate(nextYear);

        //Generate goal Components for complex goal
        GoalAggregate aggregate = new GoalAggregate();
        List<GoalComponent> goalComponents = new ArrayList<GoalComponent>();
        aggregate.setGoalComponents(goalComponents);

        //Behavior component
        BehaviorComponent behaviorComponent = new BehaviorComponent();
        behaviorComponent.setBehaviorCategory(BehaviorCategory.DEMERIT);
        behaviorComponent.setStartDate(today);
        behaviorComponent.setEndDate(nextYear);
        behaviorComponent.setModifier(2D);
        behaviorComponent.setStudent(student);
        goalComponents.add(behaviorComponent);

        //Assignment Component
        AssignmentComponent assignmentComponent = new AssignmentComponent();
        assignmentComponent.setParentId(studentAssignment.getId());
        assignmentComponent.setModifier(1D);
        assignmentComponent.setStudent(student);
        goalComponents.add(assignmentComponent);

        //Attendance Component
        AttendanceComponent attendanceComponent = new AttendanceComponent();
        attendanceComponent.setStartDate(today);
        attendanceComponent.setEndDate(nextYear);
        attendanceComponent.setParentId(section.getId());
        attendanceComponent.setStudent(student);
        attendanceComponent.setModifier(3D);
        goalComponents.add(attendanceComponent);

        //Cumulative Component
        CumulativeGradeComponent cumulativeGradeComponent = new CumulativeGradeComponent();
        cumulativeGradeComponent.setStudent(student);
        cumulativeGradeComponent.setParentId(section.getId());
        cumulativeGradeComponent.setModifier(4D);
        goalComponents.add(cumulativeGradeComponent);

        //Constant Component
        ConstantComponent constantComponent = new ConstantComponent();
        constantComponent.setStudent(student);
        constantComponent.setInitialValue(50D);
        goalComponents.add(constantComponent);


        //Generate complex goal
        ComplexGoal complexGoal = new ComplexGoal();
        complexGoal.setStudent(student);
        complexGoal.setTeacher(teacher);
        complexGoal.setName("Formula Goal");
        complexGoal.setApproved(false);
        complexGoal.setDesiredValue(100D);
        complexGoal.setGoalAggregate(aggregate);



        return new Object[][] {
                {behaviorGoal, "Test failed with a behavior goal"},
                {assGoal, "Test failed with an assignment goal"},
                {cumulativeGradeGoal, "Test failed with a cumulative grade goal"},
                {attendanceGoal, "Test failed with an attendance goal"},
                {complexGoal, "Test failed with a complex goal"}
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
        Double EXPECTED_VALUE = 3D;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();
        cal.add(Calendar.YEAR, -1); // to get previous year add -1
        Date lastYear = cal.getTime();
        cal.add(Calendar.MONTH, 1);
        Date midDate = cal.getTime();

        //Generate behaviors so we can test that calculatedValue matches
        Behavior namedBehavior = new Behavior();
        namedBehavior.setStudent(student);
        namedBehavior.setTeacher(teacher);
        namedBehavior.setName("BehaviorEvent");
        namedBehavior.setBehaviorCategory(BehaviorCategory.DEMERIT);
        namedBehavior.setPointValue("1");
        namedBehavior.setBehaviorDate(midDate);
        for (int i = 0; i < EXPECTED_VALUE; i++) {
            behaviorValidatingExecutor.create(student.getId(), namedBehavior, "Beahvior creation failed");
        }

        //Generate behavior goal
        BehaviorGoal behaviorGoal = new BehaviorGoal();
        behaviorGoal.setStudent(student);
        behaviorGoal.setTeacher(teacher);
        behaviorGoal.setBehaviorCategory(BehaviorCategory.DEMERIT);
        behaviorGoal.setStartDate(lastYear);
        behaviorGoal.setEndDate(today);
        behaviorGoal.setDesiredValue(41d);
        behaviorGoal.setName("To win them all");
        behaviorGoal.setApproved(false);
        behaviorGoal.setCalculatedValue(EXPECTED_VALUE);

        //Generate goal components that make up our complex goal
        List<GoalComponent> goalComponents = new ArrayList<GoalComponent>();
        BehaviorComponent behaviorComponent = new BehaviorComponent();
        behaviorComponent.setBehaviorCategory(BehaviorCategory.DEMERIT);
        behaviorComponent.setStartDate(today);
        behaviorComponent.setEndDate(lastYear);
        behaviorComponent.setModifier(2D);
        behaviorComponent.setStudent(student);
        goalComponents.add(behaviorComponent);
        GoalAggregate aggregate = new GoalAggregate();
        aggregate.setGoalComponents(goalComponents);

        //Generate Complex Goal with expected value of twice the behavior goal
        ComplexGoal complexGoal = new ComplexGoal();
        complexGoal.setStudent(student);
        complexGoal.setTeacher(teacher);
        complexGoal.setName("Formula Goal");
        complexGoal.setApproved(false);
        complexGoal.setDesiredValue(100D);
        complexGoal.setCalculatedValue(EXPECTED_VALUE * 2);
        complexGoal.setGoalAggregate(aggregate);

        return new Object[][]{
                {behaviorGoal, "We did not receive teh expected value from your goal"},
                {complexGoal, "We did not receive expected value for complex goal"}
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

        createdGoal.setName(localeServiceUtil.generateName());
        //PATCH the existing record with a new name.
        goalValidatingExecutor.update(student.getId(), createdGoal.getId(), goal, msg);
        itemsCreated++;
    }

    @Test
    public void testGetAll() {
        goalValidatingExecutor.getAll(student.getId(),"Get all test failed", itemsCreated);
    }
}
