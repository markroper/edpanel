package com.scholarscore.api.controller;

import com.scholarscore.api.controller.base.IntegrationBase;
import com.scholarscore.models.Behavior;
import com.scholarscore.models.BehaviorCategory;
import com.scholarscore.models.Course;
import com.scholarscore.models.School;
import com.scholarscore.models.SchoolYear;
import com.scholarscore.models.Section;
import com.scholarscore.models.Term;
import com.scholarscore.models.assignment.AssignmentType;
import com.scholarscore.models.assignment.GradedAssignment;
import com.scholarscore.models.assignment.StudentAssignment;
import com.scholarscore.models.goal.AssignmentGoal;
import com.scholarscore.models.goal.AttendanceGoal;
import com.scholarscore.models.goal.BehaviorGoal;
import com.scholarscore.models.goal.Goal;
import com.scholarscore.models.goal.SectionGradeGoal;
import com.scholarscore.models.grade.SectionGrade;
import com.scholarscore.models.grade.StudentSectionGrade;
import com.scholarscore.models.user.Staff;
import com.scholarscore.models.user.Student;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import static org.testng.Assert.fail;

/**
 * Created by cwallace on 9/21/2015.
 */
@Test(groups = { "integration" })
public class GoalControllerIntegrationTest extends IntegrationBase {

    private Student student;
    private Staff teacher;
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
    private boolean initialized = false;
    
    @BeforeClass
    public void init() {
        authenticate();

        student = new Student();
        student.setName(localeServiceUtil.generateName());
        student = studentValidatingExecutor.create(student, "create base student");

        teacher = new Staff();
        teacher.setIsTeacher(true);
        teacher.setName(localeServiceUtil.generateName());
        teacher = teacherValidatingExecutor.create(teacher, "create base teacher");

        school = new School();
        school.setName(localeServiceUtil.generateName());
        school = schoolValidatingExecutor.create(school, "Create base school");

        schoolYear = new SchoolYear();
        schoolYear.setName(localeServiceUtil.generateName());
        schoolYear.setSchool(school);
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
        section.setEnrolledStudents(new ArrayList<>());
        section.getEnrolledStudents().add(student);
        section.setTeachers(new HashSet<>());
        section.getTeachers().add(teacher);
        section.setTerm(term);
        section = sectionValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(), section, "create test base term");

        sectionAssignment = new GradedAssignment();
        sectionAssignment.setType(AssignmentType.FINAL);
        sectionAssignment.setName(localeServiceUtil.generateName());
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();
        cal.add(Calendar.YEAR, 1); // to get previous year add -1
        Date nextYear = cal.getTime();
        sectionAssignment.setAssignedDate(today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        sectionAssignment.setDueDate(nextYear.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        sectionAssignment = (GradedAssignment) sectionAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(),
                term.getId(), section.getId(), sectionAssignment, "create test base term");
        

        studentAssignment = new StudentAssignment();
        studentAssignment.setAssignment(sectionAssignment);
        studentAssignment.setStudent(student);

        studentAssignment = studentAssignmentValidatingExecutor.create(school.getId(), schoolYear.getId(), term.getId(),
                section.getId(), sectionAssignment.getId(), studentAssignment, "Initializing assignmnet");

        studentSectionGrade = new StudentSectionGrade();
        SectionGrade sg = new SectionGrade();
        sg.setDate(LocalDate.now());
        sg.setScore(EXPECTED_SECTION_GRADE.doubleValue());
        sg.setSectionFk(section.getId());
        sg.setStudentFk(student.getId());
        studentSectionGrade.setOverallGrade(sg);
        studentSectionGrade.setComplete(false);
        studentSectionGrade = studentSectionGradeValidatingExecutor.update(school.getId(), schoolYear.getId(), term.getId(), section.getId(),
                student.getId(), studentSectionGrade, "update student section grade w/ value " + EXPECTED_SECTION_GRADE);
        initialized = true;
    }

    @DataProvider(name = "createGoalDataProvider")
    public Object[][] createGoalDataMethod() {
        if (!initialized) { fail("Failed to initialize"); }
        LocalDate today = LocalDate.now();
        LocalDate nextYear = today.plusYears(1l);

        BehaviorGoal behaviorGoal = new BehaviorGoal();
        behaviorGoal.setStudent(student);
        behaviorGoal.setStaff(teacher);
        behaviorGoal.setBehaviorCategory(BehaviorCategory.DEMERIT);
        behaviorGoal.setStartDate(today);
        behaviorGoal.setEndDate(nextYear);
        behaviorGoal.setDesiredValue(41.5d);
        behaviorGoal.setName("To win them all");
        behaviorGoal.setApproved(false);
        behaviorGoal.setAutocomplete(false);
        behaviorGoal.setPlan("WE WILL MAKE THE GAOL");

        AssignmentGoal assGoal = new AssignmentGoal();
        assGoal.setStudent(student);
        assGoal.setStaff(teacher);
        assGoal.setName("The final final");
        assGoal.setApproved(false);
        assGoal.setStudentAssignment(studentAssignment);
        assGoal.setDesiredValue(95d);
        assGoal.setAutocomplete(false);
        assGoal.setPlan("WE WILL MAKE THE GAOL");

        //When we get goals with sections we don't want enrolled students to come back when we query for goals
        Section goalSection = new Section(section);
        goalSection.setEnrolledStudents(new ArrayList<Student>());

        SectionGradeGoal sectionGradeGoal = new SectionGradeGoal();
        sectionGradeGoal.setStudent(student);
        sectionGradeGoal.setStaff(teacher);
        sectionGradeGoal.setName("ALL OF THE As");
        sectionGradeGoal.setApproved(false);
        sectionGradeGoal.setSection(goalSection);
        sectionGradeGoal.setDesiredValue(6d);
        sectionGradeGoal.setAutocomplete(false);
        sectionGradeGoal.setPlan("WE WILL MAKE THE GAOL");

        AttendanceGoal attendanceGoal = new AttendanceGoal();
        attendanceGoal.setStudent(student);
        attendanceGoal.setStaff(teacher);
        attendanceGoal.setName("Weekly attendance goal");
        attendanceGoal.setApproved(false);
        attendanceGoal.setSection(goalSection);
        attendanceGoal.setDesiredValue(5D);
        attendanceGoal.setStartDate(today);
        attendanceGoal.setEndDate(nextYear);
        attendanceGoal.setAutocomplete(false);
        attendanceGoal.setPlan("WE WILL MAKE THE GAOL");





        return new Object[][] {
                {behaviorGoal, "Test failed with a behavior goal"},
                {assGoal, "Test failed with an assignment goal"},
                {sectionGradeGoal, "Test failed with a cumulative grade goal"},
                {attendanceGoal, "Test failed with an attendance goal"}
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
        LocalDate today = LocalDate.now();
        LocalDate lastYear = today.minusYears(1l);
        LocalDate midDate = lastYear.plusMonths(1l);

        //Generate behaviors so we can test that calculatedValue matches
        Behavior namedBehavior = new Behavior();
        namedBehavior.setStudent(student);
        namedBehavior.setAssigner(teacher);
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
        behaviorGoal.setStaff(teacher);
        behaviorGoal.setBehaviorCategory(BehaviorCategory.DEMERIT);
        behaviorGoal.setStartDate(lastYear);
        behaviorGoal.setEndDate(today);
        behaviorGoal.setDesiredValue(41d);
        behaviorGoal.setName("To win them all");
        behaviorGoal.setApproved(false);
        behaviorGoal.setCalculatedValue(EXPECTED_VALUE);
        behaviorGoal.setAutocomplete(false);
        behaviorGoal.setPlan("WE WILL MAKE THE GAOL");



        return new Object[][]{
                {behaviorGoal, "We did not receive teh expected value from your goal"}
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
